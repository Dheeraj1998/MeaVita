package com.example.theodhor.facebookIntegration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        String Uid = inBundle.get("user_id") + "";

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
                }

                @Override
                public void onError(FacebookException e) {
                    e.printStackTrace();
                }
            });
        }

        String name = inBundle.get("name") + "";
        String surname = inBundle.get("surname") + "";
        String imageUrl = inBundle.get("imageUrl") + "";
        String location = inBundle.get("location") + "";

        TextView nameView = (TextView) findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);

        TextView locationView = (TextView) findViewById(R.id.location);
        locationView.setText(location);

        new MainActivity.DownloadImage((ImageView) findViewById(R.id.profileImage)).execute(imageUrl);

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
            LoginManager.getInstance().logOut();
            Intent main = new Intent(MainActivity.this,MainActivity.class);
            main.putExtra("user_id","UNDEFINED");
            startActivity(main);
        }
    }
}
