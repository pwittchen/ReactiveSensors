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

import io.reactivex.functions.Predicate;

@SuppressWarnings("PMD")
public final class ReactiveSensorFilter {
  /**
   * Predicate, which can be used in filter(...) method from RxJava
   * to filter all events in which sensor value has changed
   *
   * @return BiPredicate<ReactiveSensorEvent, Boolean> predicate indicating if sensor value has
   * changed
   */
  public static Predicate<ReactiveSensorEvent> filterSensorChanged() {
    return new Predicate<ReactiveSensorEvent>() {
      @Override public boolean test(ReactiveSensorEvent event) throws Exception {
        return event.isSensorChanged();
      }
    };
  }

  /**
   * Predicate, which can be used in filter(...) method from RxJava
   * to filter all events in which accuracy value has changed
   *
   * @return BiPredicate<ReactiveSensorEvent, Boolean> predicate indicating if accuracy value has
   * changed
   */
  public static Predicate<ReactiveSensorEvent> filterAccuracyChanged() {

    return new Predicate<ReactiveSensorEvent>() {
      @Override public boolean test(ReactiveSensorEvent event) throws Exception {
        return event.isAccuracyChanged();
      }
    };
  }
}
