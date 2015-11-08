package com.github.pwittchen.reactivesensors.kotlinapp

import android.widget.TextView
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SensorHelper(private val reactiveSensors: ReactiveSensors, private val sensorType: Int, private val sensorName: String, private val textView: TextView) {

    fun deviceHasSensor(): Boolean {
        if (!reactiveSensors.hasSensor(sensorType)) {
            textView.text = "Sorry, your device doesn't have required sensor."
            return false
        }
        return true
    }

    fun createSubscription(): Subscription {
        return reactiveSensors.observeSensor(sensorType)
                .subscribeOn(Schedulers.io())
                .filter(ReactiveSensorEvent.filterSensorChanged())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { reactiveSensorEvent ->
                    val event = reactiveSensorEvent.sensorEvent
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    textView.text = "%s readings:\n x = %f\n y = %f\n z = %f".format(sensorName, x, y, z)
                }
    }

    fun safelyUnsubscribe(subscription: Subscription?) {
        if (!reactiveSensors.hasSensor(sensorType)) {
            return
        }

        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }
}
