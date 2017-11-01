# ReactiveSensors 
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ReactiveSensors-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2451)

Android library monitoring hardware sensors with RxJava.

| Current Branch | Branch  | Artifact Id | Build Status  | Maven Central |
|:--------------:|:-------:|:-----------:|:-------------:|:-------------:|
| | [`RxJava1.x`](https://github.com/pwittchen/ReactiveSensors/tree/RxJava1.x) | `reactivesensors` | [![Build Status for RxJava1.x](https://travis-ci.org/pwittchen/ReactiveSensors.svg?branch=RxJava1.x)](https://travis-ci.org/pwittchen/ReactiveSensors) | ![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivesensors.svg?style=flat) |
| :ballot_box_with_check: | [`RxJava2.x`](https://github.com/pwittchen/ReactiveSensors/tree/RxJava2.x) | `reactivesensors-rx2` | [![Build Status for RxJava2.x](https://travis-ci.org/pwittchen/ReactiveSensors.svg?branch=RxJava2.x)](https://travis-ci.org/pwittchen/ReactiveSensors) | ![Maven Central](https://img.shields.io/maven-central/v/com.github.pwittchen/reactivesensors-rx2.svg?style=flat) |

This is **RxJava2.x** branch. To see documentation for RxJava1.x, switch to [RxJava1.x](https://github.com/pwittchen/ReactiveSensors/tree/RxJava1.x) branch.

min sdk version = 9

JavaDoc is available at: http://pwittchen.github.io/ReactiveSensors/RxJava2.x

Contents
--------
- [Usage](#usage)
  - [Setting sampling period](#setting-sampling-period)
- [Example](#example)
- [Good practices](#good-practices)
  - [Checking whether sensor exists](#checking-whether-sensor-exists)
  - [Letting it crash](#letting-it-crash)
  - [Subscribing and disposing flowables](#subscribing-and-disposing-flowables)
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

Please note that we are filtering events occurring when sensor readings change with `ReactiveSensorFilter.filterSensorChanged()` method. There's also event describing change of sensor's accuracy, which can be filtered with `ReactiveSensorFilter.filterAccuracyChanged()` method. When we don't apply any filter, we will be notified both about sensor readings and accuracy changes.

```java
new ReactiveSensors(context).observeSensor(Sensor.TYPE_GYROSCOPE)
    .subscribeOn(Schedulers.computation())
    .filter(ReactiveSensorFilter.filterSensorChanged())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<ReactiveSensorEvent>() {
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

Default sampling period for flowable below is set to `SensorManager.SENSOR_DELAY_NORMAL`.

```java
Flowable<ReactiveSensorEvent> observeSensor(int sensorType)
 ```

We can configure sampling period according to our needs with the following flowable:

```java
Flowable<ReactiveSensorEvent> observeSensor(int sensorType,
                                              final int samplingPeriodInUs)
 ```
 
We can use predefined values available in `SensorManager` class from Android SDK:
- `int SENSOR_DELAY_FASTEST` - get sensor data as fast as possible
- `int SENSOR_DELAY_GAME` - rate suitable for games
- `int SENSOR_DELAY_NORMAL` - rate (default) suitable for screen orientation changes
- `int SENSOR_DELAY_UI` - rate suitable for the user interface

We can also define our own integer value in microseconds, but it's recommended to use predefined values.

We can customize RxJava 2 Backpressure Strategy for our flowable with method:

```java
Flowable<ReactiveSensorEvent> observeSensor(int sensorType, final int samplingPeriodInUs,
      final Handler handler, final BackpressureStrategy strategy)
```

Default Backpressure Strategy is `BUFFER`.

Example
-------

Exemplary application, which gets readings of various sensors is located in `app` directory of this repository. You can easily change `SENSOR_TYPE` variable to read values from a different sensor in a given samples.

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

### Letting it crash

We can let our subscription crash and handle situation when device does not have given sensor e.g. in the `Consumer<Throwable>()` implementation (if we want to return `Disposable`) or in the `onError(throwable)` method implementation of the `Subscriber`. Other types of errors can be handled there as well.

```java
new ReactiveSensors(context).observeSensor(Sensor.TYPE_GYROSCOPE)
    .subscribeOn(Schedulers.computation())
    .filter(ReactiveSensorFilter.filterSensorChanged())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<ReactiveSensorEvent>() {
      @Override public void accept(ReactiveSensorEvent reactiveSensorEvent) throws Exception {
        // handle reactiveSensorEvent
      }
    }, new Consumer<Throwable>() {
      @Override public void accept(Throwable throwable) throws Exception {
        if (throwable instanceof SensorNotFoundException) {
          textViewForMessage.setText("Sorry, your device doesn't have required sensor.");
        }
      }
    });
```

### Subscribing and disposing flowables

When we are using Disposables in Activity, we should subscribe them in `onResume()` method and dispose them in `onPause()` method.

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
    <artifactId>reactivesensors-rx2</artifactId>
    <version>0.2.0</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.github.pwittchen:reactivesensors-rx2:0.2.0'
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
