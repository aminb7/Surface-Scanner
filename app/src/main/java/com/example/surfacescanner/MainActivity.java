package com.example.surfacescanner;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity {
    private static final float NS2S = 1000000000.0f;
    private Boolean isButtonStarted = false;

    private SensorManager sensorManager;
    private Sensor sensorGyroscope;
    private Sensor sensorAccelerometer;

    private double distance = 0;
    private double speed = 0;
    private double omega = 0;

    private SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (abs(sensorEvent.values[0]) < 0.01)
                return;
            omega = sensorEvent.values[2];
            TextView textView = findViewById(R.id.textView);
            textView.setText(Double.toString(omega));




//            Long currTime = System.nanoTime();
//            Long prevTime = sensorEvent.timestamp;
//            double timeDelta = (double)(currTime - prevTime) / NS2S;

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float acceleration = sensorEvent.values[0];
            if (abs(acceleration) < 0.01)
                return;

            Long currTime = System.nanoTime();
            Long prevTime = sensorEvent.timestamp;
            double timeDelta = (double)(currTime - prevTime) / NS2S;

//            System.out.println("timeDelta: " + timeDelta);

            double currentDistance = 1/2*acceleration * (timeDelta*timeDelta) + speed * timeDelta;
            distance += abs(currentDistance);
            speed = abs(sensorEvent.values[0] * timeDelta);


            System.out.println("currTime: " + currTime);
            System.out.println("prevTime: " + prevTime);
            System.out.println("timeDelta: " + timeDelta);
            System.out.println("speed: " + speed);
            System.out.println("distance: " + distance);
            System.out.println("--------------------------");

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

//        GraphView graphView = findViewById(R.id.idGraphView);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
//                // on below line we are adding
//                // each point on our x and y axis.
//                new DataPoint(0, 1),
//                new DataPoint(1, 3),
//                new DataPoint(2, 4),
//                new DataPoint(3, 9),
//                new DataPoint(4, 6),
//                new DataPoint(5, 3),
//                new DataPoint(6, 6),
//                new DataPoint(7, 1),
//                new DataPoint(8, 2)
//        });
//        graphView.setTitle("My Graph View");
//
//        // on below line we are setting
//        // text color to our graph view.
//        graphView.setTitleColor(R.color.purple_200);
//
//        // on below line we are setting
//        // our title text size.
//        graphView.setTitleTextSize(18);
//
//        // on below line we are adding
//        // data series to our graph view.
//        graphView.addSeries(series);

        Button button = findViewById(R.id.Start);
        button.setOnClickListener(v -> {
            if (!isButtonStarted) {
                sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(accelerometerListener, sensorGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                isButtonStarted = true;
                TextView textView = findViewById(R.id.textView);
                textView.setText("0");
                button.setText("end");
            } else {
                sensorManager.unregisterListener(gyroscopeListener);
                sensorManager.unregisterListener(accelerometerListener);
                isButtonStarted = false;
                speed = 0;
                distance = 0;
                button.setText("start");
            }
        });

    }
}