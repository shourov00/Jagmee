package com.jagmee.app.customcrash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jagmee.app.R;
import com.jagmee.app.Splash_A;

public class CustomCrashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_crash);

        findViewById(R.id.restart_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomCrashActivity.this, Splash_A.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
