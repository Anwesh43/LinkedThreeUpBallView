package com.anwesh.uiprojects.linkedthreeupballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.threeupballview.ThreeUpBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThreeUpBallView.create(this)
    }
}
