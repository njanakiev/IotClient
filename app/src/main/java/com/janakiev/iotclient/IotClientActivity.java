package com.janakiev.iotclient;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class IotClientActivity extends ActionBarActivity{

    private static final String TAG = "IotClientActivity";
    private static final int DEVICE_ID = 1;

    private SensorManager sensorManager;
    private UDPSensorClient sensorClient;

    private EditText editIpAddress;
    private EditText editPort;
    private EditText editText;
    private CheckBox cbxAccelerometer;
    private CheckBox cbxGyroscope;
    private CheckBox cbxMagneticField;

    private int mode = SensorManager.SENSOR_DELAY_NORMAL;

    private InformationDisplay information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_client);

        editIpAddress =     (EditText) findViewById(R.id.editIpAddress);
        editPort =          (EditText) findViewById(R.id.editPort);
        editText =          (EditText) findViewById(R.id.editText);

        cbxAccelerometer =  (CheckBox) findViewById(R.id.cbxAccelerometer);
        cbxGyroscope =      (CheckBox) findViewById(R.id.cbxGyroscope);
        cbxMagneticField =  (CheckBox) findViewById(R.id.cbxMagneticField);

        information = new InformationDisplay((TextView) findViewById(R.id.txtInformation), 20);
        editIpAddress.setText("10.0.0.4");
        editPort.setText("3000");
        editText.setText("test");
        sensorClient = new UDPSensorClient(information, UDPSensorClient.JSON, "10.0.0.4", 3000, DEVICE_ID);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerSensorListeners();

        information.update(TAG + ", started");
    }

    public void onCheckBoxClicked(View view){

        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()){
            case R.id.cbxAccelerometer:
                if(checked){
                    Log.d(TAG, "accelerometer sensor registered");
                    information.update(TAG + ", accelerometer sensor registered");
                }else{
                    Log.d(TAG, "accelerometer sensor unregistered");
                    information.update(TAG + ", accelerometer sensor unregistered");
                }
                break;
            case R.id.cbxGyroscope:
                if(checked){
                    Log.d(TAG, "gyroscope sensor registered");
                    information.update(TAG + ", gyroscope sensor registered");
                }else{
                    Log.d(TAG, "gyroscope sensor unregistered");
                    information.update(TAG + ", gyroscope sensor unregistered");
                }
                break;
            case R.id.cbxMagneticField:
                if(checked){
                    Log.d(TAG, "magnetic field sensor registered");
                    information.update(TAG + ", magnetic field sensor registered");
                }else{
                    Log.d(TAG, "magnetic field sensor unregistered");
                    information.update(TAG + ", magnetic field sensor unregistered");
                }
                break;
        }

        sensorManager.unregisterListener(sensorClient);
        registerSensorListeners();
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rbnJSON:
                if(checked){
                    sensorClient.setFormat(UDPSensorClient.JSON);
                    information.update(TAG + ", Format set to JSON");
                    Log.d(TAG, "Format set to JSON");
                }
                break;
            case R.id.rbnCSV:
                if(checked){
                    sensorClient.setFormat(UDPSensorClient.CSV);
                    information.update(TAG + ", Format set to CSV");
                    Log.d(TAG, "Format set to CSV");
                }
                break;


            case R.id.rbnSpeedFastest:
                if(checked){
                    sensorManager.unregisterListener(sensorClient);
                    mode = SensorManager.SENSOR_DELAY_FASTEST;
                    Log.d(TAG, "Sampling speed set to SENSOR_DELAY_FASTEST");
                    information.update(TAG + ", Sampling speed set to SENSOR_DELAY_FASTEST");
                    registerSensorListeners();
                }
                break;
            case R.id.rbnSpeedGame:
                if(checked){
                    sensorManager.unregisterListener(sensorClient);
                    mode = SensorManager.SENSOR_DELAY_GAME;
                    Log.d(TAG, "Sampling speed set to SENSOR_DELAY_GAME");
                    information.update(TAG + ", Sampling speed set to SENSOR_DELAY_GAME");
                    registerSensorListeners();
                }
                break;
            case R.id.rbnSpeedUi:
                if(checked){
                    sensorManager.unregisterListener(sensorClient);
                    mode = SensorManager.SENSOR_DELAY_UI;
                    Log.d(TAG, "Sampling speed set to SENSOR_DELAY_UI");
                    information.update(TAG + ", Sampling speed set to SENSOR_DELAY_UI");
                    registerSensorListeners();
                }
                break;
            case R.id.rbnSpeedNormal:
                if(checked){
                    sensorManager.unregisterListener(sensorClient);
                    mode = SensorManager.SENSOR_DELAY_NORMAL;
                    Log.d(TAG, "Sampling speed set to SENSOR_DELAY_NORMAL");
                    information.update(TAG + ", Sampling speed set to SENSOR_DELAY_NORMAL");
                    registerSensorListeners();
                }
                break;
        }
    }

    public void registerSensorListeners(){
        if(cbxAccelerometer.isChecked()){
            sensorManager.registerListener(sensorClient,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mode);
        }
        if(cbxGyroscope.isChecked()){
            sensorManager.registerListener(sensorClient,
                    sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), mode);
        }
        if(cbxMagneticField.isChecked()){
            sensorManager.registerListener(sensorClient,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), mode);
        }
    }

    public void onButtonApply(View view){
        String ipAddress = editIpAddress.getText().toString();
        int port = Integer.parseInt(editPort.getText().toString());
        sensorClient.set(ipAddress, port);
    }

    public void onButtonSend(View view){
        String data = editText.getText().toString();
        sensorClient.send(data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorClient);
        sensorClient.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListeners();
        sensorClient.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_iot_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
