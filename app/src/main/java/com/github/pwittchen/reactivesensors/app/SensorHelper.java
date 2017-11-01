package com.github.pwittchen.reactivesensors.app;

import android.hardware.SensorEvent;
import android.widget.TextView;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import com.github.pwittchen.reactivesensors.library.SensorNotFoundException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.Locale;

class SensorHelper {
  private ReactiveSensors reactiveSensors;
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
        .filter(ReactiveSensorFilter.filterSensorChanged())
        .subscribe(new Consumer<ReactiveSensorEvent>() {
          @Override public void accept(ReactiveSensorEvent reactiveSensorEvent) throws Exception {
            SensorEvent event = reactiveSensorEvent.getSensorEvent();

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String format = "%s readings:\n x = %f\n y = %f\n z = %f";
            String message = String.format(Locale.getDefault(), format, sensorName, x, y, z);
            textViewForMessage.setText(message);
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            if (throwable instanceof SensorNotFoundException) {
              textViewForMessage.setText("Sorry, your device doesn't have required sensor.");
            }
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
