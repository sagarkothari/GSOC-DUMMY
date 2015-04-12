package com.example.sagar.androidwatch;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements SensorEventListener{

    private TextView mTextView;
    private ProgressBar pb;
    Timer timer;
    TimerTask timerTask;
    SensorManager sm;
    final Handler handler=new Handler();
    int time=61;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

         sm=(SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor hear=sm.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sm.registerListener(this,hear,SensorManager.SENSOR_DELAY_UI);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                pb=(ProgressBar) stub.findViewById(R.id.progressBar);
                pb.setVisibility(View.VISIBLE);
                StartTimer();
            }
        });
    }

     public void StartTimer()
     {
         timer=new Timer();

         timerTask = new TimerTask() {

             public void run() {



                 //use a handler to run a toast that shows the current timestamp

                 handler.post(new Runnable() {

                     public void run() {

                         //get the current timeStamp
                        if(time>0)
                        time--;
                         else
                        {
                            timer.cancel();
                            unreg();
                            pb.setVisibility(View.GONE);
                            mTextView.setText("Place your watch properly and try again");
                        }
                     }

                 });

             }

         };


         timer.schedule(timerTask,0,1000);
     }

    public void unreg()
    {
        sm.unregisterListener(this);
    }

    @Override


    protected void onDestroy() {
        super.onDestroy();
        unreg();
    }

//    public void myclick(View view) {
//
//        Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            if ((int) event.values[0] > 0) {

                mTextView.setText("" + (int) event.values[0]);
                pb.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
    }


}
