package com.example.theodhor.facebookIntegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.theodhor.facebookIntegration.R;

public class Register_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__page);
    }

    public void open_meavita_login(View v){
        Intent temp = new Intent(Register_Page.this, MeavitaLogin.class);
        startActivity(temp);
    }
}
