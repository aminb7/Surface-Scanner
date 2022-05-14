package com.example.surfacescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Boolean isButtonStarted = false;

    private SensorManager sensorManager;
    private Sensor sensorGyroscope;
    private Sensor sensorAccelerometer;

    private SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
//            System.out.println(sensorEvent.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            System.out.println(sensorEvent.values[1]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        isButtonStarted = false;

        Button button = findViewById(R.id.Start);
        button.setOnClickListener(v -> {
            if (!isButtonStarted) {
                sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(accelerometerListener, sensorGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                isButtonStarted = true;
                button.setText("end");
            } else {
                sensorManager.unregisterListener(gyroscopeListener);
                sensorManager.unregisterListener(accelerometerListener);
                isButtonStarted = false;
                button.setText("start");
            }
        });

    }
}