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
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import java.util.List;
import java.util.Locale;

/**
 * ReactiveSensors is an Android library monitoring hardware and software sensors with RxJava
 * Observables
 */
public class ReactiveSensors {

  private SensorManager sensorManager;

  /**
   * Creates ReactiveSensors object
   *
   * @param context context
   */
  public ReactiveSensors(final Context context) {
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
  public boolean hasSensor(final int sensorType) {
    return sensorManager.getDefaultSensor(sensorType) != null;
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object. Sampling period is set to SensorManager.SENSOR_DELAY_NORMAL.
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeSensor(final int sensorType) {
    return observeSensor(sensorType, SensorManager.SENSOR_DELAY_NORMAL);
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
  public Flowable<ReactiveSensorEvent> observeSensor(final int sensorType,
      final int samplingPeriodInUs) {
    return observeSensor(sensorType, samplingPeriodInUs, BackpressureStrategy.BUFFER);
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object with defined sampling period
   *
   * @param sensorType sensor type from Sensor class from Android SDK
   * @param samplingPeriodInUs sampling period in microseconds,
   * you can use predefined values from SensorManager class with prefix SENSOR_DELAY
   * @param strategy BackpressureStrategy for RxJava 2 Flowable type
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeSensor(final int sensorType,
      final int samplingPeriodInUs, final BackpressureStrategy strategy) {

    if (!hasSensor(sensorType)) {
      String format = "Sensor with id = %d is not available on this device";
      String message = String.format(Locale.getDefault(), format, sensorType);
      return Flowable.error(new SensorNotFoundException(message));
    }

    final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
    final SensorEventListenerWrapper wrapper = new SensorEventListenerWrapper();
    final SensorEventListener listener = wrapper.create();
    sensorManager.registerListener(listener, sensor, samplingPeriodInUs);

    return Flowable
        .create(wrapper::setEmitter, strategy)
        .doOnCancel(() -> sensorManager.unregisterListener(listener));
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
      final int... sensorTypes) {
    return observeManySensors(samplingPeriodInUs, BackpressureStrategy.BUFFER, sensorTypes);
  }

  /**
   * Returns RxJava Observable, which allows to monitor hardware sensors as a stream of
   * ReactiveSensorEvent object with defined sampling period
   *
   * @param samplingPeriodInUs sampling period in microseconds,
   * you can use predefined values from SensorManager class with prefix SENSOR_DELAY
   * @param strategy BackpressureStrategy for RxJava 2 Flowable type
   * @param sensorTypes sensor types array from Sensor class from Android SDK
   * @return RxJava Observable with ReactiveSensorEvent
   */
  public Flowable<ReactiveSensorEvent> observeManySensors(final int samplingPeriodInUs,
      final BackpressureStrategy strategy, final int... sensorTypes) {
    final String errorMessage = getErrorMessage(sensorTypes);
    if (errorMessage.length() != 0) {
      return Flowable.error(new SensorNotFoundException(errorMessage));
    }

    final SensorEventListenerWrapper wrapper = new SensorEventListenerWrapper();
    final SensorEventListener listener = wrapper.create();

    for (int sensorType : sensorTypes) {
      final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
      sensorManager.registerListener(listener, sensor, samplingPeriodInUs);
    }

    return Flowable
        .create(wrapper::setEmitter, strategy)
        .doOnCancel(() -> sensorManager.unregisterListener(listener));
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
