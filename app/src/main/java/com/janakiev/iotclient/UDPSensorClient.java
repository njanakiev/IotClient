package com.janakiev.iotclient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Nikolai on 04.06.2015.
 */
public class UDPSensorClient implements SensorEventListener {

    private static final String TAG = "UDPSensorClient";

    private static final String ACCELEROMETER_ID = "accelerometer";
    private static final String GYROSCOPE_ID = "gyroscope";
    private static final String MAGNETIC_FIELD_ID = "magnetic_field";

    public static final String JSON = "json";
    public static final String CSV = "csv";

    private String format;
    private InformationDisplay information;
    private String ipAddress;
    private int port;
    private int deviceId;

    private DatagramSocket socket = null;

    public UDPSensorClient(InformationDisplay information, String format, String ipAddress, int port, int deviceId){
        this.information = information;
        this.format = format;
        this.ipAddress = ipAddress;
        this.port = port;
        this.deviceId = deviceId;

        open();
    }

    public void set(String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
        Log.d(TAG, ", set() method called with Address : " + ipAddress + ":" + port);
        information.update(TAG + ", set() method called with Address : " + ipAddress + ":" + port);

        open();
    }

    public void setFormat(String format){
        this.format = format;
    }

    public void send(String data){
        new UDPTask().execute(data);
    }

    public void open(){
        try {
            if(socket != null) socket.close();
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            information.update(TAG + ", Error : " + e.getMessage());
            Log.e(TAG, "Error : ", e);
        }
    }

    public void close(){
        socket.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        String type;
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:  type = ACCELEROMETER_ID; break;
            case Sensor.TYPE_GYROSCOPE:      type = GYROSCOPE_ID; break;
            case Sensor.TYPE_MAGNETIC_FIELD: type = MAGNETIC_FIELD_ID; break;
            default:
                Log.d(TAG, "Sensor type not supported : " + event.sensor.getType());
                information.update(TAG + ", Sensor type not supported : " + event.sensor.getType());
                return;
        }
        String data;
        switch(format){
            case JSON:  data = createJSONString(type, event.values, event.timestamp);
                break;
            case CSV:   data = createCSVString(type, event.values, event.timestamp);
                break;
            default:
                Log.d(TAG, "Format not supported : " + format);
                information.update(TAG + ", Format not supported : " + format);
                return;
        }
        new UDPTask().execute(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private String createCSVString(String type, float[] values, long timestamp){
        String csvString = deviceId + ", " + type + ", " + timestamp;
        for(int i = 0; i < values.length; i++){
            csvString += ", " + values[i];
        }
        return csvString;
    }

    private String createJSONString(String type, float[] values, long timestamp){

        try {
            JSONObject json = new JSONObject();
            json.put("device_id", deviceId);
            json.put("type", type);
            json.put("timestamp", timestamp);
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < values.length; i++){
                jsonArray.put(values[i]);
            }
            json.put("values", jsonArray);

            return json.toString();
        } catch (JSONException e) {
            information.update(TAG + ", Error : " + e.getMessage());
            Log.e(TAG, "Error : ", e);
        }
        return "";
    }

    private class UDPTask extends AsyncTask<String, Void, Void> {

        String message = null;

        @Override
        protected Void doInBackground(String... params) {
            try {
                for (int i = 0; i < params.length; i++) {
                    byte[] data = params[i].getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length,
                            InetAddress.getByName(ipAddress), port);
                    socket.send(packet);
                    //socket.close();
                }
            } catch (IOException e) {
                message = TAG + ", Error : " + e.getMessage();
                Log.e(TAG, "Error : ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(message != null)
                information.update(message);
        }
    }
}
