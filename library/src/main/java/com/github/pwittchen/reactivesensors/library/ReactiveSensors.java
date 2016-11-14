/*
 * Copyright (C) 2015 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.reactivesensors.library;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * ReactiveSensors is an Android library
 * monitoring hardware and software sensors with RxJava Observables
 */
public final class ReactiveSensors {

  private SensorManager sensorManager;

  private ReactiveSensors() {
  }

  /**
   * Creates ReactiveSensors object
   *
   * @param context context
   */
  public ReactiveSensors(Context context) {
    this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
  }

  /**
   * Gets lists of all sensors available on device
   *
   * @return list of sensors
   */
  public List<Sensor> getSensors() {
    return sensorManager.getSensorList(Sensor.TYPE_ALL);
  }

  /**
   * Checks if device has given sensor available
   *
   * @param sensorType from Sensor class available in Android SDK
   * @return boolean returns true if sensor is available
   */
  public boolean hasSensor(int sensorType) {
    return sensorManager.getDefaultSensor(sensorType) != null;
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors
   * as a stream of ReactiveSensorEvent object.
   * Sampling period is set to SensorManager.SENSOR_DELAY_NORMAL.
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Observable<ReactiveSensorEvent> observeSensor(int sensorType) {
    return observeSensor(sensorType, SensorManager.SENSOR_DELAY_NORMAL, null);
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors
   * as a stream of ReactiveSensorEvent object with defined sampling period
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @param samplingPeriodInUs sampling period in microseconds,
   * you can use predefined values from SensorManager class with prefix SENSOR_DELAY
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Observable<ReactiveSensorEvent> observeSensor(int sensorType,
                                                       final int samplingPeriodInUs) {
    return observeSensor(sensorType, samplingPeriodInUs, null);
  }


  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors
   * as a stream of ReactiveSensorEvent object with defined sampling period
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @param samplingPeriodInUs sampling period in microseconds,
   * @param handler the Handler the sensor events will be delivered to, use default if it is null
   * you can use predefined values from SensorManager class with prefix SENSOR_DELAY
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Observable<ReactiveSensorEvent> observeSensor(int sensorType,
                                                       final int samplingPeriodInUs,
                                                       final Handler handler) {

    if (!hasSensor(sensorType)) {
      String format = "Sensor with id = %d is not available on this device";
      String message = String.format(format, sensorType);
      throw new RuntimeException(message);
    }

    final Sensor sensor = sensorManager.getDefaultSensor(sensorType);

    return Observable.create(new Observable.OnSubscribe<ReactiveSensorEvent>() {
      @Override public void call(final Subscriber<? super ReactiveSensorEvent> subscriber) {

        final SensorEventListener listener = new SensorEventListener() {
          @Override public void onSensorChanged(SensorEvent sensorEvent) {
            ReactiveSensorEvent event = new ReactiveSensorEvent(sensorEvent);
            subscriber.onNext(event);
          }

          @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
            ReactiveSensorEvent event = new ReactiveSensorEvent(sensor, accuracy);
            subscriber.onNext(event);
          }
        };

        if (null == handler) {
          sensorManager.registerListener(listener, sensor, samplingPeriodInUs);
        } else {
          sensorManager.registerListener(listener, sensor, samplingPeriodInUs, handler);
        }

        subscriber.add(unsubscribeInUiThread(new Action0() {
          @Override public void call() {
            sensorManager.unregisterListener(listener);
          }
        }));
      }
    });
  }

  private Subscription unsubscribeInUiThread(final Action0 unsubscribe) {
    return Subscriptions.create(new Action0() {
      @Override public void call() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
          unsubscribe.call();
        } else {
          final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
          inner.schedule(new Action0() {
            @Override public void call() {
              unsubscribe.call();
              inner.unsubscribe();
            }
          });
        }
      }
    });
  }
}
