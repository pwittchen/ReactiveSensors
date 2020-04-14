package com.github.pwittchen.reactivesensors.app;

import android.widget.TextView;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import com.github.pwittchen.reactivesensors.library.SensorNotFoundException;
import com.github.pwittchen.reactivesensors.library.SensorsProxy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


import java.util.Locale;

class SensorHelper {
  private SensorsProxy reactiveSensors;
  private int sensorType;
  private String sensorName;
  private TextView textViewForMessage;

  SensorHelper(ReactiveSensors sensors, int type, String name, TextView textViewForMessage) {
    this.reactiveSensors = sensors;
    this.sensorType = type;
    this.sensorName = name;
    this.textViewForMessage = textViewForMessage;
  }

  Disposable createSubscription() {

    return reactiveSensors.observeSensor(sensorType)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(ReactiveSensorEvent::sensorChanged)
        .subscribe(event -> {
          float x = event.sensorValues()[0];
          float y = event.sensorValues()[1];
          float z = event.sensorValues()[2];

          final String format = "%s readings:\n x = %f\n y = %f\n z = %f";
          String message = String.format(Locale.getDefault(), format, event.sensorName(), x, y, z);
          textViewForMessage.setText(message);
        }, throwable -> {
          if (throwable instanceof SensorNotFoundException) {
            textViewForMessage.setText("Sorry, your device doesn't have required sensor.");
          }
        });
  }

  void safelyDispose(Disposable disposable) {
    if (!reactiveSensors.hasSensor(sensorType)) {
      return;
    }

    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
