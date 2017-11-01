package com.github.pwittchen.reactivesensors.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.github.pwittchen.reactivesensors.R;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import io.reactivex.disposables.Disposable;

public abstract class SensorActivity extends AppCompatActivity {
  protected int sensorType;
  protected String sensorName;

  private TextView tvSensor;
  private ReactiveSensors reactiveSensors;
  private Disposable subscription;
  private SensorHelper sensorHelper;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sensor_sample);
    tvSensor = (TextView) findViewById(R.id.sensor);
    reactiveSensors = new ReactiveSensors(this);
    sensorHelper = new SensorHelper(reactiveSensors, sensorType, sensorName, tvSensor);
  }

  @Override protected void onResume() {
    super.onResume();
    subscription = sensorHelper.createSubscription();
  }

  @Override protected void onPause() {
    super.onPause();
    sensorHelper.safelyDispose(subscription);
  }
}
