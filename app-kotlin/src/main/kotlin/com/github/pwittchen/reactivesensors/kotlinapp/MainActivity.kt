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
package com.github.pwittchen.reactivesensors.kotlinapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.github.pwittchen.reactivesensors.kotlinapp.samples.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewAndSetOnClickListener(R.id.b_gyroscope, GyroscopeActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_orientation, OrientationActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_accelerometer, AccelerometerActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_ambient_temperature, AmbientTemperatureActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_gravity, GravityActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_light, LightActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_linear_acceleration, LinearAccelerationActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_magnetic_field, MagneticFieldActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_pressure, PressureActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_proximity, ProximityActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_relative_humidity, RelativeHumidityActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_rotation_vector, RotationVectorActivity::class.java)
        findViewAndSetOnClickListener(R.id.b_temperature, TemperatureActivity::class.java)
    }

    private fun findViewAndSetOnClickListener(viewId: Int, cls: Class<*>) {
        findViewById<Button>(viewId).setOnClickListener({
            val intent = Intent(this@MainActivity, cls)
            startActivity(intent)
        })
    }
}
