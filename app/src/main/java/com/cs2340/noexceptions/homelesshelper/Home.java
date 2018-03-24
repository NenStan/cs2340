package com.cs2340.noexceptions.homelesshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void toLoginScreen(View v) {
        Intent toLogin = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(toLogin);
    }

    public void toRegistrationScreen(View v) {
        Intent toRegistration = new Intent(getBaseContext(), RegistrationActivity.class);
        startActivity(toRegistration);
    }
}
