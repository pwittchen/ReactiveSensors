package com.github.pwittchen.reactivesensors.kotlinapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import rx.Subscription

abstract class SensorActivity : AppCompatActivity() {
    private var subscription: Subscription? = null
    private var sensorHelper: SensorHelper? = null
    protected var sensorType: Int = 0
    protected var sensorName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_sample)
        sensorHelper = SensorHelper(ReactiveSensors(this), sensorType, sensorName, findViewById(R.id.sensor) as TextView)
        val sensorHelperWithCast = sensorHelper as SensorHelper //TODO: consider improvement to remove this additional val

        if (!sensorHelperWithCast.deviceHasSensor()) {
            return
        }

        subscription = sensorHelperWithCast.createSubscription()
    }

    override fun onPause() {
        super.onPause()
        if (sensorHelper == null) {
            return
        }

        sensorHelper!!.safelyUnsubscribe(subscription)
    }
}
