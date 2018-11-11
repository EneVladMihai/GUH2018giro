package com.example.enevl.guh2018gyroscop;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private int previousAngleX, previousAngleY, previousAngleZ;
    private int futureStandardX, standardX;
    private Button calobrateButton;
    private TextView xtextView, ytextView, ztextView;

    private int move, angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        xtextView = (TextView) findViewById(R.id.xtextview);
        ytextView = (TextView) findViewById(R.id.ytextview);
        ztextView = (TextView) findViewById(R.id.ztextview);

        calobrateButton = (Button) findViewById(R.id.calibrate);

        calobrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standardX = futureStandardX;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            // Remap coordinate system
            float[] remappedRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Y,
                    remappedRotationMatrix);


            // Convert to orientations
            float[] orientations = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, orientations);

            for (int i = 0; i < 3; i++) {
                orientations[i] = (int) (Math.toDegrees(orientations[i]));
            }
            if (Math.abs(previousAngleX - orientations[0]) > 20) {
                Log.d("X", Float.toString(orientations[0]));
                previousAngleX = (int) orientations[0];
            }
            if (Math.abs(previousAngleY - orientations[1]) > 20) {
                Log.d("Y", Float.toString(orientations[1]));
                previousAngleY = (int) orientations[1];
            }
            if(Math.abs(previousAngleZ - orientations[2]) > 20)
            {
                Log.d("Z",Float.toString(orientations[2]));
                previousAngleZ = (int) orientations[2];
            }

            futureStandardX = (int) orientations[0];

            if(orientations[1] >= 10 && orientations[1] <= 70)
                move = 2;
            else if(orientations[1] <= -10 && orientations[1] >= -70)
                move = 1;
            else move = 0;

            if(orientations[0] - standardX >= 10)
                angle = (int) orientations[0] - standardX;
            else if(orientations[0] - standardX <= -10)
                angle = 360 - (int)(Math.abs(orientations[0] - standardX));
            else angle = 0;


            xtextView.setText("MOVE: " + move);
            ytextView.setText("Angle: " + angle);
            //ztextView.setText("Z: " + orientations[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
