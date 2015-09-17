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
package com.github.pwittchen.reactivesensors.samples;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.github.pwittchen.reactivesensors.R;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ProximityActivity extends AppCompatActivity {
  private final static int SENSOR_TYPE = Sensor.TYPE_PROXIMITY;

  private TextView tvSensor;
  private ReactiveSensors reactiveSensors;
  private Subscription subscription;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sensor_sample);
    tvSensor = (TextView) findViewById(R.id.sensor);
    reactiveSensors = new ReactiveSensors(this);
  }

  @Override protected void onResume() {
    super.onResume();

    if (!reactiveSensors.hasSensor(SENSOR_TYPE)) {
      tvSensor.setText("Sorry, your device doesn't have required sensor.");
      return;
    }

    subscription = reactiveSensors.observeSensor(SENSOR_TYPE)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .filter(ReactiveSensorEvent.filterSensorChanged())
        .subscribe(new Action1<ReactiveSensorEvent>() {
          @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
            SensorEvent event = reactiveSensorEvent.getSensorEvent();

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String format = "proximity:\n x = %f\n y = %f\n z = %f";
            String message = String.format(format, x, y, z);
            tvSensor.setText(message);
          }
        });
  }

  @Override protected void onPause() {
    super.onPause();

    if (!reactiveSensors.hasSensor(SENSOR_TYPE)) {
      return;
    }

    subscription.unsubscribe();
  }
}
