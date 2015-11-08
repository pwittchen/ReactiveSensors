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
import android.view.View
import com.github.pwittchen.reactivesensors.kotlinapp.samples.AccelerometerActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.AmbientTemperatureActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.GravityActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.GyroscopeActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.LightActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.LinearAccelerationActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.MagneticFieldActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.OrientationActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.PressureActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.ProximityActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.RelativeHumidityActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.RotationVectorActivity
import com.github.pwittchen.reactivesensors.kotlinapp.samples.TemperatureActivity

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
        findViewById(viewId).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(this@MainActivity, cls)
                startActivity(intent)
            }
        })
    }
}
