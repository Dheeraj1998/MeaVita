package com.example.theodhor.facebookIntegration;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
        final String username = ((EditText) findViewById(R.id.user_id)).getText().toString();
        final String password = ((EditText) findViewById(R.id.password)).getText().toString();
        final Button submit = (Button) findViewById(R.id.login_button);
        final Button new_user = (Button) findViewById(R.id.sign_up_button);

        final Intent temp = new Intent(MeavitaLogin.this, MainActivity.class);

        //The URL to which GET request is sent
        String LOGIN_URL = ("<WEBSERVICE URL>?username='" + Uri.encode(username) + "'&password=\"" + Uri.encode(password) + "\"");

        //Check if any of the fields are empty
        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(MeavitaLogin.this, "The fields have not been completed!", Toast.LENGTH_SHORT).show();
        } else {
            //Contact the server to add the data from the user
            StringRequest stringRequest = new StringRequest(Request.Method.GET, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Check if the entered password is correct
                            if (response.equals("Success")) {
                                temp.putExtra("user_id", "UNDEFINED");
                                startActivity(temp);
                                finish();
                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
                                submit.setEnabled(true);
                                new_user.setEnabled(true);
                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong combination of username & password!", Toast.LENGTH_LONG).show();
                                submit.setEnabled(true);
                                new_user.setEnabled(true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MeavitaLogin.this, error.toString(), Toast.LENGTH_LONG).show();
                            submit.setEnabled(true);
                            new_user.setEnabled(true);
                        }
                    });

            //Add the server request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

            //Disable the button until further activity
            submit.setEnabled(false);
            new_user.setEnabled(false);

//            temp.putExtra("user_id", "UNDEFINED");
//            startActivity(temp);
//            finish();
        }
    }

    public void forgot_password(View v){
        Intent temp = new Intent(MeavitaLogin.this, Forgot_Password.class);
        startActivity(temp);
    }
}
