package com.example.theodhor.facebookIntegration;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static android.R.attr.data;
import static android.R.attr.duration;
import static android.R.attr.name;
import static android.util.Log.e;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private String firstName,lastName,email,birthday,location;
    private URL profilePicture;
    private String userId;
    private String TAG = "LoginActivity";

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        Bundle inBundle = getIntent().getExtras();
        String Uid;

        try {
            Uid = inBundle.get("user_id") + "";
        }

        catch (Exception e){
            Uid = "UNDEFINED";
        }

        if(Uid.equals("UNDEFINED")){
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback < LoginResult > () {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.e(TAG,object.toString());
                            Log.e(TAG,response.toString());

                            try {
                                userId = object.getString("id");
                                JSONObject temp = new JSONObject(object.getString("location"));
                                location = temp.getString("name");
                                profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");

                                if(object.has("first_name"))
                                    firstName = object.getString("first_name");
                                if(object.has("last_name"))
                                    lastName = object.getString("last_name");
                                if (object.has("email"))
                                    email = object.getString("email");
                                if (object.has("birthday"))
                                    birthday = object.getString("birthday");

                                Intent main = new Intent(MainActivity.this,MainActivity.class);
                                main.putExtra("user_id",userId);
                                main.putExtra("name",firstName);
                                main.putExtra("surname",lastName);
                                main.putExtra("location",location);
                                main.putExtra("imageUrl",profilePicture.toString());
                                startActivity(main);
                                finish();

                                Toast.makeText(getApplicationContext(), "Facebook Login is successful!", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, first_name, last_name, email, birthday, gender, location");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Intent main = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(main);
                    finish();
                }

                @Override
                public void onError(FacebookException e) {
                    e.printStackTrace();
                }
            });
        }

        String name = "", surname = "", imageUrl = "", location = "";

        try {
            name = inBundle.get("name") + "";
            surname = inBundle.get("surname") + "";
            imageUrl = inBundle.get("imageUrl") + "";
            location = inBundle.get("location") + "";
        }

        catch (Exception e){
            name = "null";
        }

        if (name.equals("null")){
            name = "NA";
            surname = "";
            location = "NA";
        }

        else {
            new MainActivity.DownloadImage((ImageView) findViewById(R.id.profileImage)).execute(imageUrl);
        }

        TextView nameView = (TextView) findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);

        TextView locationView = (TextView) findViewById(R.id.location);
        locationView.setText(location);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Uid + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject temp = new JSONObject(response.getJSONObject().getString("summary"));
                            String number_friends = temp.getString("total_count");

                            TextView friendsView = (TextView) findViewById(R.id.number_friends);
                            friendsView.setText(Html.fromHtml("<b>" + number_friends + "</b>" + "<br />" +
                                    "<small>Friends </small>"));
                            //                            Log.e("custom",number_friends);
                        } catch (Exception e) {
                            Log.e("custom", e.toString());
                        }

                    }
                }
        ).executeAsync();
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void facebook_login(View temp){
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile == null) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("email", "user_birthday","user_posts","user_location","user_friends")
            );
        } else {
            AlertDialog diaBox = FacebookLogout();
            diaBox.show();
        }
    }

    public void meavita_logout(View temp){
        AlertDialog diaBox = MeaVitalogout();
        diaBox.show();
    }

    private AlertDialog MeaVitalogout()
    {
        AlertDialog log_out_dialog =new AlertDialog.Builder(this)
                .setTitle("MeaVita Logout")
                .setMessage("Are you sure you want to logout?")
                .setIcon(R.drawable.app_icon)

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent temp = new Intent(MainActivity.this, MeavitaLogin.class);
                        dialog.dismiss();
                        startActivity(temp);
                        finish();

                        Toast.makeText(getApplicationContext(), "You have been logged out!", Toast.LENGTH_SHORT).show();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return log_out_dialog;
    }

    private AlertDialog FacebookLogout()
    {
        AlertDialog log_out_dialog =new AlertDialog.Builder(this)
                .setTitle("Facebook Logout")
                .setMessage("Are you sure you want to logout?")
                .setIcon(R.drawable.com_facebook_favicon_blue)

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        LoginManager.getInstance().logOut();
                        Intent main = new Intent(MainActivity.this,MainActivity.class);
                        main.putExtra("user_id","UNDEFINED");
                        startActivity(main);
                        finish();

                        Toast.makeText(getApplicationContext(), "Facebook Logout is successful!", Toast.LENGTH_SHORT).show();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return log_out_dialog;
    }
}
