package com.anwesh.uiprojects.threeupballview

/**
 * Created by anweshmishra on 21/07/18.
 */

import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val NODES : Int = 5

fun Canvas.drawTUBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / NODES
    val sc1 : Float = Math.min(scale, 0.5f) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0.0f, scale - 0.5f)) * 2
    var x : Float = gap
    if (i == NODES - 1) {
        x = gap * 0.4f
    }
    save()
    translate(gap/2 + i * x * sc2, h/2)
    for (j in 0..2) {
        val yGap : Float = gap * (1 - j) * (1 - sc1)
        drawCircle(0f, yGap, 0.1f * gap, paint)
    }
    restore()
}

class ThreeUpBallView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TUBNode(var i : Int, val state : State = State()) {

        var prev : TUBNode? = null

        var next : TUBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < NODES - 1) {
                next = TUBNode(i + 1)
                next?.prev = this
            }
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TUBNode {
            var curr : TUBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        fun draw(canvas : Canvas, paint : Paint) {
            paint.color = Color.parseColor("#4CAF50")
            canvas.drawTUBNode(i, state.scale, paint)
        }
    }

    data class LinkedTUB(var i : Int) {

        private var curr : TUBNode = TUBNode(i + 1)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {i, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(i, scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }
}
