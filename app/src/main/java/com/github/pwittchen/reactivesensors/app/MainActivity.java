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
package com.github.pwittchen.reactivesensors.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.pwittchen.reactivesensors.R;
import com.github.pwittchen.reactivesensors.app.samples.AccelerometerActivity;
import com.github.pwittchen.reactivesensors.app.samples.AmbientTemperatureActivity;
import com.github.pwittchen.reactivesensors.app.samples.GravityActivity;
import com.github.pwittchen.reactivesensors.app.samples.GyroscopeActivity;
import com.github.pwittchen.reactivesensors.app.samples.LightActivity;
import com.github.pwittchen.reactivesensors.app.samples.LinearAccelerationActivity;
import com.github.pwittchen.reactivesensors.app.samples.MagneticFieldActivity;
import com.github.pwittchen.reactivesensors.app.samples.OrientationActivity;
import com.github.pwittchen.reactivesensors.app.samples.PressureActivity;
import com.github.pwittchen.reactivesensors.app.samples.ProximityActivity;
import com.github.pwittchen.reactivesensors.app.samples.RelativeHumidityActivity;
import com.github.pwittchen.reactivesensors.app.samples.RotationVectorActivity;
import com.github.pwittchen.reactivesensors.app.samples.TemperatureActivity;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewAndSetOnClickListener(R.id.b_gyroscope, GyroscopeActivity.class);
    findViewAndSetOnClickListener(R.id.b_orientation, OrientationActivity.class);
    findViewAndSetOnClickListener(R.id.b_accelerometer, AccelerometerActivity.class);
    findViewAndSetOnClickListener(R.id.b_ambient_temperature, AmbientTemperatureActivity.class);
    findViewAndSetOnClickListener(R.id.b_gravity, GravityActivity.class);
    findViewAndSetOnClickListener(R.id.b_light, LightActivity.class);
    findViewAndSetOnClickListener(R.id.b_linear_acceleration, LinearAccelerationActivity.class);
    findViewAndSetOnClickListener(R.id.b_magnetic_field, MagneticFieldActivity.class);
    findViewAndSetOnClickListener(R.id.b_pressure, PressureActivity.class);
    findViewAndSetOnClickListener(R.id.b_proximity, ProximityActivity.class);
    findViewAndSetOnClickListener(R.id.b_relative_humidity, RelativeHumidityActivity.class);
    findViewAndSetOnClickListener(R.id.b_rotation_vector, RotationVectorActivity.class);
    findViewAndSetOnClickListener(R.id.b_temperature, TemperatureActivity.class);
  }

  private void findViewAndSetOnClickListener(int viewId, final Class<?> cls) {
    findViewById(viewId).setOnClickListener(view -> {
      Intent intent = new Intent(MainActivity.this, cls);
      startActivity(intent);
    });
  }
}
