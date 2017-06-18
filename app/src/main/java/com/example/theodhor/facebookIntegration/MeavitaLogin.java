package com.example.theodhor.facebookIntegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MeavitaLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meavita_login);
    }

    public void open_register_page(View v){
        Intent temp = new Intent(MeavitaLogin.this, Register_Page.class);
        startActivity(temp);
    }

    public void open_dashboard(View v){
        Intent temp = new Intent(MeavitaLogin.this, MainActivity.class);
        temp.putExtra("user_id","UNDEFINED");
        startActivity(temp);
        finish();
    }

    public void forgot_password(View v){
        Intent temp = new Intent(MeavitaLogin.this, Forgot_Password.class);
        startActivity(temp);
    }
}
