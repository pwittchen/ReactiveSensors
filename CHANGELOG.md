CHANGELOG
=========

v. 0.3.1
--------
*07 Nov 2020*

- downgraded min Android SDK version to 16

v. 0.3.0
--------
*06 Nov 2020*

- migrated library to RxJava3

v. 0.2.0
--------
*02 Nov 2017*

- returning error through Rx to make the error handling easier - issue #29
- creating sensor observable with the ability to specify handler - PR #26
- updated project dependencies
- migrated library to RxJava2.x on `RxJava2.x` branch
- kept backward compatibility with RxJava1.x on `RxJava1.x` branch
- removed `master` branch

v. 0.1.2
--------
*31 Jul 2016*

- bumped RxJava dependency to v. 1.1.8
- bumped RxAndroid dependency to v. 1.2.1
- bumped Google Truth test dependency to v. 0.28
- bumped Compile SDK version to v. 23
- bumped Kotlin to v. 1.0.0
- updated sample apps


v. 0.1.1
--------
*13 Dec 2015*

- bumped RxJava dependency to v. 1.1.0
- bumped RxAndroid dependency to v. 1.1.0
- bumped Google Truth test dependency to v. 0.27
- bumped Gradle Build Tools to v. 1.3.1

v. 0.1.0
--------
*06 Dec 2015*

- removed `filterSensorChanged()` method from `ReactiveSensorEvent` class
- removed `filterAccuracyChanged()` method from `ReactiveSensorEvent` class
- created `ReactiveSensorFilter` class
- added `filterSensorChanged()` method to `ReactiveSensorFilter` class
- added `filterAccuracyChanged()` method to `ReactiveSensorFilter` class
- added sample app in Kotlin
- added code examples with more sensors in sample apps
- improved sample apps
- added Static Code Analysis (CheckStyle, PMD, FindBugs, Android Lint)
- updated documentation in `README.md` file

v. 0.0.1
--------
*05 Sep 2015*

- first release of the library
