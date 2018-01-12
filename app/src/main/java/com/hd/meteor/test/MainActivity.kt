package com.hd.meteor.test

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.hd.meteor.MeteorBean
import com.hd.meteor.MeteorConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val meteorConfig=MeteorConfig(this)
        meteorConfig.setMeteorBean(MeteorBean(this))
        meteorConfig.nightSkyBackgroundDrawable=ContextCompat.getDrawable(this,R.drawable.meteor_background1)
        meteor.addConfig(meteorConfig)
    }
}
