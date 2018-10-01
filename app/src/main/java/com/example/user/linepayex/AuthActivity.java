package com.example.user.linepayex;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();

        int index = data.toString().indexOf("transactionId=");
        String transactionId = data.toString().substring(index + 14);
        MainActivity.transactionId = transactionId;

        final ConfirmApiRequest confirmApiRequest = new ConfirmApiRequest(1, "TWD", transactionId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String body = confirmApiRequest.connectApi();
                ConfirmApiRequest.Response response = new Gson().fromJson(body, ConfirmApiRequest.Response.class);
                MainActivity.regKey = response.getInfo().getRegKey();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.wtf("AuthActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.wtf("AuthActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.wtf("AuthActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("AuthActivity", "onDestroy");
    }
}
