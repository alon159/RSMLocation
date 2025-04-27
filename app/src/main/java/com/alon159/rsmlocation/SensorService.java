package com.alon159.rsmlocation;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SensorService extends Service implements SensorEventListener {

    private final IBinder binder = new LocalBinder();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Socket socket;
    private PrintWriter writer;
    private static final int SERVERPORT=5000;
    private static final String SERVER_IP="10.0.2.2";

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Abre conexión de socket en un hilo aparte
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVERPORT); // Dirección provisional
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        SensorService getService(){
            return SensorService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("SensorService", "Service destroyed, sensor unregistered, and socket closed");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        String data = "X: " + x + " Y: " + y + " Z: " + z;

        new Thread(() -> {
            try {
                if (writer != null) {
                    writer.println(data);
                    writer.flush();  // Asegúrate de vaciar el buffer después de enviar
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
