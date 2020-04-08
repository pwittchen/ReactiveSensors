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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import io.reactivex.Emitter;

class SensorEventListenerWrapper {

  private Emitter<ReactiveSensorEvent> emitter;

  void setEmitter(Emitter<ReactiveSensorEvent> emitter) {
    this.emitter = emitter;
  }

  SensorEventListener create() {
    return new SensorEventListener() {
      @Override public void onSensorChanged(SensorEvent sensorEvent) {
        ReactiveSensorEvent event = new ReactiveSensorEvent(sensorEvent);
        if (emitter != null) {
          emitter.onNext(event);
        }
      }

      @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        ReactiveSensorEvent event = new ReactiveSensorEvent(sensor, accuracy);
        if (emitter != null) {
          emitter.onNext(event);
        }
      }
    };
  }
}
