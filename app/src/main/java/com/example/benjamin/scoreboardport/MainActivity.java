package com.example.benjamin.scoreboardport;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Set;
import java.util.logging.Handler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Messenger _scoreBoardMessenger;
    private boolean _serviceConnected = false;

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"service connceted");
            _scoreBoardMessenger = new Messenger(service);
            Toast.makeText(getApplicationContext(),"service connected",Toast.LENGTH_LONG).show();
            _serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"service disconnceted");
            Toast.makeText(getApplicationContext(),"service disconnected",Toast.LENGTH_LONG).show();
            _serviceConnected = false;
        }
    };

    private final static String TAG = "MainActivity";

    private EditText mMessageET;
    private Button mSendBN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMessageET = (EditText) findViewById(R.id.message_et);
        mSendBN = (Button) findViewById(R.id.send_bn);
        mSendBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG,"click");
                    Message msg = new Message();
                    _scoreBoardMessenger.send(msg);
                }catch (Exception e){
                    e.toString();
                }
            }
        });

        Intent serviceIntent = new Intent(this,ScoreBoardBluetoothConnection.class);
        bindService(serviceIntent,_serviceConnection,BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"unbind Service");
        unbindService(_serviceConnection);
        Intent stopServiceIntent = new Intent(this,ScoreBoardBluetoothConnection.class);
        stopService(stopServiceIntent);
    }
}
