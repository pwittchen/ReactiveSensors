package com.github.pwittchen.reactivesensors.app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.pwittchen.reactivesensors.R;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class SensorActivity extends AppCompatActivity {
  protected int sensorType;
  protected String sensorName;

  private Disposable subscription;
  private SensorHelper sensorHelper;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sensor_sample);
    final TextView tvSensor = (TextView) findViewById(R.id.sensor);
    final ReactiveSensors reactiveSensors = new ReactiveSensors(getApplicationContext());
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
