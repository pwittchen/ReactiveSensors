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
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ReactiveSensors is an Android library monitoring hardware and software sensors with RxJava
 * Observables
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
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object. Sampling period is set to SensorManager.SENSOR_DELAY_NORMAL.
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeSensor(int sensorType) {
    return observeSensor(sensorType, SensorManager.SENSOR_DELAY_NORMAL, null);
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object with defined sampling period
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @param samplingPeriodInUs sampling period in microseconds, you can use predefined values from
   * SensorManager class with prefix SENSOR_DELAY
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeSensor(int sensorType, final int samplingPeriodInUs) {
    return observeSensor(sensorType, samplingPeriodInUs, null);
  }

  public Flowable<ReactiveSensorEvent> observeSensor(int sensorType, final int samplingPeriodInUs,
      final Handler handler) {
    return observeSensor(sensorType, samplingPeriodInUs, handler, BackpressureStrategy.BUFFER);
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object with defined sampling period
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @param samplingPeriodInUs sampling period in microseconds,
   * @param handler the Handler the sensor events will be delivered to, use default if it is null
   * you can use predefined values from SensorManager class with prefix SENSOR_DELAY
   * @param strategy BackpressureStrategy for RxJava 2 Flowable type
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeSensor(int sensorType, final int samplingPeriodInUs,
      final Handler handler, final BackpressureStrategy strategy) {

    if (!hasSensor(sensorType)) {
      String format = "Sensor with id = %d is not available on this device";
      String message = String.format(Locale.getDefault(), format, sensorType);
      return Flowable.error(new SensorNotFoundException(message));
    }

    final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
    final SensorEventListenerWrapper wrapper = new SensorEventListenerWrapper();
    final SensorEventListener listener = wrapper.create();

    return Flowable.create(new FlowableOnSubscribe<ReactiveSensorEvent>() {
      @Override
      public void subscribe(final FlowableEmitter<ReactiveSensorEvent> emitter) {

        wrapper.setEmitter(emitter);

        if (handler == null) {
          sensorManager.registerListener(listener, sensor, samplingPeriodInUs);
        } else {
          sensorManager.registerListener(listener, sensor, samplingPeriodInUs, handler);
        }
      }
    }, strategy).doOnCancel(new Action() {
      @Override
      public void run() {
        sensorManager.unregisterListener(listener);
      }
    });
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object with defined sampling period
   *
   * @param samplingPeriodInUs sampling period in microseconds, you can use predefined values from
   * SensorManager class with prefix SENSOR_DELAY
   * @param sensorTypes sensor type from Sensor class from Android SDK
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeManySensors(final int samplingPeriodInUs,
      int... sensorTypes) {
    return observeManySensors(samplingPeriodInUs, null, sensorTypes);
  }

  public Flowable<ReactiveSensorEvent> observeManySensors(final int samplingPeriodInUs,
      final Handler handler, int... sensorTypes) {
    return observeManySensors(samplingPeriodInUs, handler, BackpressureStrategy.BUFFER,
        sensorTypes);
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object with defined sampling period
   *
   * @param samplingPeriodInUs sampling period in microseconds,
   * @param handler the Handler the sensor events will be delivered to, use default if it is null
   * you can use predefined values from SensorManager class with prefix SENSOR_DELAY
   * @param strategy BackpressureStrategy for RxJava 2 Flowable type
   * @param sensorTypes sensor types array from Sensor class from Android SDK
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeManySensors(final int samplingPeriodInUs,
      final Handler handler, final BackpressureStrategy strategy, int... sensorTypes) {
    final String errorMessage = getErrorMessage(sensorTypes);
    if (errorMessage.length() != 0) {
      return Flowable.error(new SensorNotFoundException(errorMessage));
    }

    final SensorEventListenerWrapper wrapper = new SensorEventListenerWrapper();
    final SensorEventListener listener = wrapper.create();

    final List<Sensor> sensors = new ArrayList<>();
    for (int sensorType : sensorTypes) {
      sensors.add(sensorManager.getDefaultSensor(sensorType));
    }

    return Flowable.create(new FlowableOnSubscribe<ReactiveSensorEvent>() {
      @Override
      public void subscribe(final FlowableEmitter<ReactiveSensorEvent> emitter) {
        wrapper.setEmitter(emitter);

        if (handler == null) {
          for (Sensor sensor : sensors) {
            sensorManager.registerListener(listener, sensor, samplingPeriodInUs);
          }
        } else {
          for (Sensor sensor : sensors) {
            sensorManager.registerListener(listener, sensor, samplingPeriodInUs, handler);
          }
        }
      }
    }, strategy).doOnCancel(new Action() {
      @Override
      public void run() {
        sensorManager.unregisterListener(listener);
      }
    });
  }

  private String getErrorMessage(final int[] sensorTypes) {
    StringBuilder errorMessage = new StringBuilder();
    for (int sensorType : sensorTypes) {
      if (!hasSensor(sensorType)) {
        if (errorMessage.length() == 0) {
          errorMessage = new StringBuilder(
              "Following sensors are not available on current device: " + sensorType + ' '
          );
        }

        errorMessage.append(sensorType);
        errorMessage.append(' ');
      }
    }

    return errorMessage.toString();
  }
}
