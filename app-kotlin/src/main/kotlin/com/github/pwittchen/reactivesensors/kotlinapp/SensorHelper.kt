package com.github.pwittchen.reactivesensors.kotlinapp

import android.widget.TextView
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.github.pwittchen.reactivesensors.library.SensorNotFoundException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SensorHelper(private val reactiveSensors: ReactiveSensors, private val sensorType: Int,
    private val sensorName: String, private val textView: TextView) {

  fun createSubscription(): Subscription {
    return reactiveSensors.observeSensor(sensorType)
        .subscribeOn(Schedulers.computation())
        .filter(ReactiveSensorFilter.filterSensorChanged())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : Subscriber<ReactiveSensorEvent>() {
          override fun onCompleted() {
          }

          override fun onError(throwable: Throwable?) {
            if (throwable is SensorNotFoundException) {
              textView.text = "Sorry, your device doesn't have required sensor."
            }
          }

          override fun onNext(reactiveSensorEvent: ReactiveSensorEvent?) {
            if (reactiveSensorEvent != null) {
              val event = reactiveSensorEvent.sensorEvent
              val x = event.values[0]
              val y = event.values[1]
              val z = event.values[2]
              val message = "%s readings:\n x = %f\n y = %f\n z = %f"
              textView.text = message.format(sensorName, x, y, z)
            }
          }
        })
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
