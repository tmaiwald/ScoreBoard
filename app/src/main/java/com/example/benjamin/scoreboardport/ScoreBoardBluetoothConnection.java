package com.example.benjamin.scoreboardport;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.spec.ECField;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tim on 30.04.2016.
 */
public class ScoreBoardBluetoothConnection extends Service {

    private static final String TAG = "BluetoothConnection";
    private BluetoothAdapter _bluetoothAdapter;
    private BluetoothDevice _raspberry;
    private boolean _raspberryIsConnected = false;
    private final static String MY_UUID ="00001101-0000-1000-8000-00805F9B34FB"; //"00001101-0000-1000-8000-00805f9b34fb";//="00001101-0000-1000-8000-00805f9b34fb";
    private BluetoothSocket mSocket=null;
    private OutputStream _outputStream;
    private InputStream _inputStream;
    private BufferedReader _reader;
    private PrintStream _sender;

    public static final int TEST = 0;

    private Handler _messageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            send("Test");
        }
    };

    private Messenger _messenger = new Messenger(_messageHandler);

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        if(mSocket != null) {
            try {
                mSocket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBluetooth();
        findRaspberry();

        try {
            UUID uuid = UUID.fromString(MY_UUID);
            mSocket = _raspberry.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mSocket != null){
            try {
                mSocket.connect();
             _outputStream =mSocket.getOutputStream();
            _sender = new PrintStream(_outputStream);

            _inputStream = mSocket.getInputStream();
            _reader = new BufferedReader(
                    new InputStreamReader(_inputStream));
            }catch (Exception e){
                e.printStackTrace();
            }

         /*   int i = 0;
            while(true){
                _sender.print(i++);
                try {
                Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            */
        }
    }

    private void send(String msg){
        Log.d(TAG,"send "+msg);
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {

            int i = 0;
           if(_sender != null){
               //_sender.print(msg);
               while(true){
                   _sender.print(i++);
                   try {
                       Thread.sleep(1000);
                   }catch(Exception e){
                       e.printStackTrace();
                   }
               }
           }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return _messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    private void findRaspberry() {
        Set<BluetoothDevice> pairedDevices = _bluetoothAdapter
                .getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String name = device.getName();
            if(name.equals("TIMS-PC")) {
                this._raspberry = device;
            }
        }
    }

    private void initBluetooth() {
        Log.d(TAG, "Checking Bluetooth...");
        if (_bluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth");
            //mSendBN.setClickable(false);
        } else{
            Log.d(TAG, "Bluetooth supported");
        }
        if (!_bluetoothAdapter.isEnabled()) {
            //mSendBN.setClickable(false);
            Log.d(TAG, "Bluetooth not enabled");
        }
        else{
            Log.d(TAG, "Bluetooth enabled");
        }
    }


}
