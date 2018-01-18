package com.example.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.aidlservice.IAidlService;

public class MainActivity extends AppCompatActivity {

    private IAidlService myService;
    private AidlServiceConnection serviceConnection;

    public void buttonClicked(View view) {
        try {
            sendCommand();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    class AidlServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Srv", "onServiceConnected: connected");
            myService = IAidlService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("Srv", "onServiceDisconnected: disconnected");
            myService = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void bindToService() {
        serviceConnection = new AidlServiceConnection();
        Intent i = new Intent();
        i.setClassName("com.example.aidlservice", "com.example.aidlservice.AidlService");
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendCommand() throws RemoteException {
        myService.sendCommand("hello!");
    }

    @Override
    protected void onStop() {
        unbindService(serviceConnection);
        serviceConnection = null;
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindToService();
    }
}
