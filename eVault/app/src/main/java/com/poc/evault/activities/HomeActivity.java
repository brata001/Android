package com.poc.evault.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.poc.evault.R;
import com.poc.evault.utils.SharedPreferenceUtil;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DASP2 on 4/1/2017.
 */
public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String name="";
        String email="";
        String photoUrl="";
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            name=bundle.getString("NAME");
            email=bundle.getString("EMAIL");
            photoUrl=bundle.getString("URL");
        }

        ImageView imgAdd=(ImageView)findViewById(R.id.fab);
        ImageView imgLogout=(ImageView)findViewById(R.id.logout);
        TextView txtName=(TextView)findViewById(R.id.name);
        txtName.setText(name);
        TextView txtEmail=(TextView)findViewById(R.id.email);
        txtEmail.setText(email);
        CircleImageView imgUserPhoto=(CircleImageView)findViewById(R.id.user_photo);

        DownloadImageTask task=new DownloadImageTask(imgUserPhoto);
        task.execute(photoUrl);

        imgAdd.setOnClickListener(this);
        imgLogout.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                        //.requestScopes(new Scope(Scopes.PROFILE))
                        //.requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestProfile()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fab:
                break;
            case R.id.logout:
                signOut();
                startActivity(new Intent(HomeActivity.this,SplashActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }
}
