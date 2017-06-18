package com.example.theodhor.facebookIntegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.theodhor.facebookIntegration.R;

public class Forgot_Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password);
    }

    public void forgot_password(View v){
        Intent temp = new Intent(Forgot_Password.this, MeavitaLogin.class);
        startActivity(temp);
    }
}
