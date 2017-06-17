package com.example.theodhor.facebookIntegration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONObject;
import java.io.InputStream;

import static android.util.Log.e;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        Bundle inBundle = getIntent().getExtras();
        String Uid = inBundle.get("user_id").toString();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        String imageUrl = inBundle.get("imageUrl").toString();
        String location = inBundle.get("location").toString();

        TextView nameView = (TextView)findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);

        TextView locationView = (TextView)findViewById(R.id.location);
        locationView.setText(location);

        TextView logout = (TextView)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
        new MainActivity.DownloadImage((ImageView)findViewById(R.id.profileImage)).execute(imageUrl);

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

                            TextView friendsView = (TextView)findViewById(R.id.number_friends);
                            friendsView.setText(Html.fromHtml("<b>" + number_friends + "</b>" +  "<br />" +
                                    "<small>Friends </small>"));
//                            Log.e("custom",number_friends);
                        }

                        catch (Exception e){
                            Log.e("custom",e.toString());
                        }

                    }
                }
        ).executeAsync();
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage) {
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

}
