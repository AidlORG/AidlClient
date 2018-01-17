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

public class MainActivity extends AppCompatActivity {

    public IAidlService myService;

    class AidlServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Srv", "onServiceConnected: connected");
            myService = IAidlService.Stub.asInterface((IBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent();
        i.setClassName("com.example.aidlservice", "AidlService");
        AidlServiceConnection serviceConnection = new AidlServiceConnection();
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);

        try {
            sendCommand();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCommand() throws RemoteException {
        myService.sendCommand("hello!");
    }
}
