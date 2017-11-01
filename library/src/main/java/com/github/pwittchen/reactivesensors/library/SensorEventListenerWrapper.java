package com.github.pwittchen.reactivesensors.library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import io.reactivex.FlowableEmitter;

public class SensorEventListenerWrapper {

  private FlowableEmitter<ReactiveSensorEvent> emitter;

  public FlowableEmitter<ReactiveSensorEvent> getEmitter() {
    return emitter;
  }

  public void setEmitter(FlowableEmitter<ReactiveSensorEvent> emitter) {
    this.emitter = emitter;
  }

  public SensorEventListener create() {
    return new SensorEventListener() {
      @Override public void onSensorChanged(SensorEvent sensorEvent) {
        ReactiveSensorEvent event = new ReactiveSensorEvent(sensorEvent);
        if (getEmitter() != null) {
          getEmitter().onNext(event);
        }
      }

      @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        ReactiveSensorEvent event = new ReactiveSensorEvent(sensor, accuracy);
        if (getEmitter() != null) {
          getEmitter().onNext(event);
        }
      }
    };
  }
}
