package com.github.pwittchen.reactivesensors.app;

import android.hardware.SensorEvent;
import android.widget.TextView;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SensorHelper {
  private ReactiveSensors reactiveSensors;
  private int sensorType;
  private String sensorName;
  private TextView textViewForMessage;

  public SensorHelper(ReactiveSensors sensors, int type, String name, TextView textViewForMessage) {
    this.reactiveSensors = sensors;
    this.sensorType = type;
    this.sensorName = name;
    this.textViewForMessage = textViewForMessage;
  }

  public boolean deviceHasSensor() {
    if (!reactiveSensors.hasSensor(sensorType)) {
      textViewForMessage.setText("Sorry, your device doesn't have required sensor.");
      return false;
    }
    return true;
  }

  public Subscription createSubscription() {
    Subscription subscription = reactiveSensors.observeSensor(sensorType)
        .subscribeOn(Schedulers.computation())
        .filter(ReactiveSensorFilter.filterSensorChanged())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ReactiveSensorEvent>() {
          @Override public void call(ReactiveSensorEvent reactiveSensorEvent) {
            SensorEvent event = reactiveSensorEvent.getSensorEvent();

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String format = "%s readings:\n x = %f\n y = %f\n z = %f";
            String message = String.format(format, sensorName, x, y, z);
            textViewForMessage.setText(message);
          }
        });

    return subscription;
  }

  public void safelyUnsubscribe(Subscription subscription) {
    if (!reactiveSensors.hasSensor(sensorType)) {
      return;
    }

    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}
