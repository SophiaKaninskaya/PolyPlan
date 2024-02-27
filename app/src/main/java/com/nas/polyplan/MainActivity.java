package com.nas.polyplan;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Thread timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new Thread() {

            @Override
            public void run() {
                try {
                    synchronized (this) {

                        wait(5000);

                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();

                } finally {

                    Intent intent = new Intent(MainActivity.this, Authorization.class);
                    startActivity(intent);
                    finish();

                }
            }
        };

        timer.start();

    }
}