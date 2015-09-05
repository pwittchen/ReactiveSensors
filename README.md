# ReactiveSensors [![Build Status](https://travis-ci.org/pwittchen/ReactiveSensors.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveSensors)
Android library monitoring hardware sensors with RxJava Observables.

Library is compatible with RxJava 1.0.+ and RxAndroid 1.0.+ and uses them under the hood.

min sdk version = 9

Contents
--------
- [Usage](#usage)
- [Example](#example)
- [Good practices](#good-practices)
  - [Checking whether sensor exists](#checking-whether-sensor-exists)
  - [Subscribing and ubsubscribing observables](#subscribing-and-ubsubscribing-observables)
  - [Filtering stream](#filtering-stream)
  - [Other practices](#other-practices)
- [Download](#download)
- [Code style](#code-style)
- [References](#references)
- [License](#license)

Usage
-----

Code sample below demonstrates how to observe Gyroscope sensor. 

Please note that we are filtering events occuring when sensors reading change with `ReactiveSensorEvent.filterSensorChanged()` method. There's also event describing change of sensor's accuracy, which can be filter with `ReactiveSensorEvent.filterAccuracyChanged()` method. When we don't apply any filter, we will be notified about sensor readings changes and accuracy changes.

```java
new ReactiveSensors(this)
        .observeSensor(Sensor.TYPE_GYROSCOPE)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .filter(ReactiveSensorEvent.filterSensorChanged())
        .subscribe(new Action1<ReactiveSensorEvent>() {
            @Override
            public void call(ReactiveSensorEvent reactiveSensorEvent) {
                SensorEvent event = reactiveSensorEvent.getSensorEvent();

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                String message = String.format("x = %f, y = %f, z = %f", x, y, z);

                Log.d("gyroscope readings", message);
            }
        });
```

We can observe any hardware sensor in the same way. You can check [list of all sensors in official Android documentation](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-intro). To get list of all sensors available on the current device, you can use `getSensors()` method available in `ReactiveSensors` class.

Example
-------

Exemplary application, which reads gyroscope sensor is located in `app` directory of this repository. You can easily change `SENSOR_TYPE` variable to read values from a different sensor.

Good practices
--------------

### Checking whether sensor exists

We should check whether device has concrete sensor before we start observing it. We can do it in the following way

```java
if (reactiveSensors.hasSensor(SENSOR_TYPE)) {
  // observe sensor
} else {
  // show error message
}
```

### Subscribing and ubsubscribing observables

When we are using subscriptions in Activity, we should subscribe them in `onResume()` method and unsubscribe them in `onPause()` method.

### Filtering stream

When we want to receive only sensor updates, we should use `ReactiveSensorEvent.filterSensorChanged()` method in `filter(...)` method from RxJava.

When we want to receive only accurracy updates, we should use `ReactiveSensorEvent.filterAccuracyChanged()` method in `filter(...)` method from RxJava.

If we don't apply any filter, we will receive both accuracy and sensor readings updates.

### Other practices

See also [Best Practices for Accessing and Using Sensors](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-practices).

Download
--------

:construction: Download information will be available soon. :construction:

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles. Currently, library doesn't have checkstyle verification attached. It can be done in the future.

References
----------
- [Sensors Overview](http://developer.android.com/guide/topics/sensors/sensors_overview.html)

License
-------

    Copyright 2015 Piotr Wittchen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
