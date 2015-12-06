# ReactiveSensors 
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveSensors-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2451) [![Build Status](https://travis-ci.org/pwittchen/ReactiveSensors.svg?branch=master)](https://travis-ci.org/pwittchen/ReactiveSensors) ![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivesensors.svg?style=flat)

Android library monitoring hardware sensors with RxJava Observables.

Library is compatible with RxJava 1.0.+ and RxAndroid 1.0.+ and uses them under the hood.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveSensors/

Contents
--------
- [Usage](#usage)
  - [Setting sampling period](#setting-sampling-period)
- [Example](#example)
- [Good practices](#good-practices)
  - [Checking whether sensor exists](#checking-whether-sensor-exists)
  - [Subscribing and ubsubscribing observables](#subscribing-and-ubsubscribing-observables)
  - [Filtering stream](#filtering-stream)
  - [Other practices](#other-practices)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [References](#references)
- [License](#license)

Usage
-----

Code sample below demonstrates how to observe Gyroscope sensor. 

Please note that we are filtering events occuring when sensor readings change with `ReactiveSensorFilter.filterSensorChanged()` method. There's also event describing change of sensor's accuracy, which can be filtered with `ReactiveSensorFilter.filterAccuracyChanged()` method. When we don't apply any filter, we will be notified both about sensor readings and accuracy changes.

```java
new ReactiveSensors(context).observeSensor(Sensor.TYPE_GYROSCOPE)
    .subscribeOn(Schedulers.io())
    .filter(ReactiveSensorFilter.filterSensorChanged())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<ReactiveSensorEvent>() {
      @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
        SensorEvent event = reactiveSensorEvent.getSensorEvent();

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        String message = String.format("x = %f, y = %f, z = %f", x, y, z);

        Log.d("gyroscope readings", message);
      }
    });
}
```

We can observe any hardware sensor in the same way. You can check [list of all sensors in official Android documentation](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-intro). To get list of all sensors available on the current device, you can use `getSensors()` method available in `ReactiveSensors` class.

### Setting sampling period

Default sampling period for observable below is set to `SensorManager.SENSOR_DELAY_NORMAL`.

```java
Observable<ReactiveSensorEvent> observeSensor(int sensorType)
 ```

We can configure sampling period according to our needs with the following observable:

```java
Observable<ReactiveSensorEvent> observeSensor(int sensorType, 
                                              final int samplingPeriodInUs)
 ```
 
We can use predefined values available in `SensorManager` class from Android SDK:
- `int SENSOR_DELAY_FASTEST` - get sensor data as fast as possible
- `int SENSOR_DELAY_GAME` - rate suitable for games
- `int SENSOR_DELAY_NORMAL` - rate (default) suitable for screen orientation changes
- `int SENSOR_DELAY_UI` - rate suitable for the user interface

We can also define our own integer value in microseconds, but it's recommended to use predefined values.

Example
-------

Exemplary application, which gets readings of various sensors is located in `app` directory of this repository. You can easily change `SENSOR_TYPE` variable to read values from a different sensor in a given samples.

If you are interested in library usage with Kotlin, check sample in `app-kotlin` directory.

Good practices
--------------

### Checking whether sensor exists

We should check whether device has concrete sensor before we start observing it. 

We can do it in the following way:

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

When we want to receive **only sensor updates**, we should use `ReactiveSensorFilter.filterSensorChanged()` method in `filter(...)` method from RxJava.

When we want to receive **only accuracy updates**, we should use `ReactiveSensorFilter.filterAccuracyChanged()` method in `filter(...)` method from RxJava.

If we don't apply any filter, we will receive both accuracy and sensor readings updates.

### Other practices

See also [Best Practices for Accessing and Using Sensors](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-practices).

Download
--------

You can depend on the library through Maven:

```xml
<dependency>
    <groupId>com.github.pwittchen</groupId>
    <artifactId>reactivesensors</artifactId>
    <version>0.1.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivesensors:0.1.0'
}
```

Tests
-----

Tests are available in `library/src/androidTest/java/` directory and can be executed on emulator or Android device from Android Studio or CLI with the following command:

```
./gradlew connectedCheck
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles. Currently, library doesn't have checkstyle verification attached. It can be done in the future.

Static code analysis
--------------------

Static code analysis runs Checkstyle, FindBugs, PMD and Lint. It can be executed with command:

 ```
 ./gradlew check
 ```

Reports from analysis are generated in `library/build/reports/` directory.

References
----------
- [Sensors Overview](http://developer.android.com/guide/topics/sensors/sensors_overview.html)
- [SensorManager class from Android SDK](http://developer.android.com/reference/android/hardware/SensorManager.html)

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
