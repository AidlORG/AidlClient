package com.example.aidlclient;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlservice.IAidlService;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private IAidlService myService;
    private AidlServiceConnection serviceConnection;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.txtSpeech);
    }

    public void userDidSpeak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            makeToast(getString(R.string.speech_not_supported));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String command = result.get(0);
                    Log.i("MainActivity", "onActivityResult: "+command);
                    textView.setText(command);
                    sendCommand(command);
                } else {
                    makeToast(getString(R.string.error_happened));
                }
                break;
            }
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

    private void bindToService() {
        serviceConnection = new AidlServiceConnection();
        Intent i = new Intent();
        i.setClassName("com.example.aidlservice", "com.example.aidlservice.AidlService");
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendCommand(String command) {
        String messageFromService = "";
        try {
            messageFromService = myService.sendCommand(command);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        textView.setText(messageFromService);
        makeToast(messageFromService);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
