package com.example.surfacescanner;

import static java.lang.Math.abs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final float NS2S = 1000000000.0f;
    private Boolean isButtonStarted = false;

    private SensorManager sensorManager;
    private Sensor sensorGyroscope;
    private Sensor sensorAccelerometer;

    private double distance = 0;
    private double speed = 0;
    private GraphView graphView;
    private ArrayList<Double> xGraph = new ArrayList<>();
    private ArrayList<Double> yGraph = new ArrayList<>();
    private double omega = 0;
    private double angle = 0;
    private double y = 0;

//    private ArrayList<float> xGraph;

    private Long lastGyroTime;
    private Long lastAcceleroTime;
    private float zacceleration = 0;

    private final SensorEventListener gyroscopeListener = new SensorEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            omega = sensorEvent.values[1];
            if (Math.abs(zacceleration) < 0.04 && Math.abs(omega) < 0.08)
                return;

            double timeDelta = (double)(sensorEvent.timestamp - lastGyroTime) / NS2S;

            angle += omega * timeDelta;
            y -= Math.sin(angle);

            xGraph.add(distance * 2);
            yGraph.add(y);
            DataPoint[] dataPoints = new DataPoint[xGraph.size()];
            for (int i = 0; i < xGraph.size(); i++) {
                dataPoints[i] = new DataPoint(xGraph.get(i) , yGraph.get(i));
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            graphView.removeAllSeries();
            graphView.addSeries(series);

//            Long currTime = System.nanoTime();
//            Long prevTime = sensorEvent.timestamp;
//            double timeDelta = (double)(currTime - prevTime) / NS2S;
            lastGyroTime = sensorEvent.timestamp;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            zacceleration = sensorEvent.values[2];
            float acceleration = sensorEvent.values[0];
            if (abs(acceleration) < 0.01)
                return;

            long currTime = System.nanoTime();
            long prevTime = sensorEvent.timestamp;
            double timeDelta = (double)(sensorEvent.timestamp - lastAcceleroTime) / NS2S;

//            System.out.println("timeDelta: " + timeDelta);

            double currentDistance = acceleration * (timeDelta*timeDelta) / 2 + speed * timeDelta;
            distance += abs(currentDistance);
            double prevSpeed = speed;
            speed = abs(sensorEvent.values[0] * timeDelta) + prevSpeed;

//            sensorManager.registerListener(gyroscopeListener, sensorGyroscope, (int) (100 / Math.abs(speed)));

            lastAcceleroTime = sensorEvent.timestamp;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        isButtonStarted = false;

        lastGyroTime = SystemClock.elapsedRealtimeNanos();
        lastAcceleroTime = SystemClock.elapsedRealtimeNanos();

        graphView = findViewById(R.id.idGraphView);
        graphView.setTitle("Scan Result");
        graphView.setTitleColor(R.color.purple_500);
        graphView.setTitleTextSize(46);
//        graphView.setBackgroundColor((R.color.);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(30);
        graphView.getViewport().setMinY(-400);
        graphView.getViewport().setMaxY(400);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScalable(true);

        Button button = findViewById(R.id.Start);
        button.setOnClickListener(v -> {
            if (!isButtonStarted) {
                sensorManager.registerListener(gyroscopeListener, sensorGyroscope, SensorManager.SENSOR_DELAY_GAME);
                sensorManager.registerListener(accelerometerListener, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                isButtonStarted = true;
                button.setText("end");
                graphView.removeAllSeries();
            } else {
                sensorManager.unregisterListener(gyroscopeListener);
                sensorManager.unregisterListener(accelerometerListener);
                isButtonStarted = false;
                speed = 0;
                distance = 0;
                omega = 0;
                angle = 0;
                y = 0;
                zacceleration = 0;
                xGraph.clear();
                yGraph.clear();
                button.setText("start");
                lastGyroTime = SystemClock.elapsedRealtimeNanos();
                lastAcceleroTime = SystemClock.elapsedRealtimeNanos();
            }
        });
    }
}