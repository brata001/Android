package com.poc.evault.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.poc.evault.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private ImageView page1, page2, page3, page4;
    private ImageView indicator1, indicator2, indicator3, indicator4;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static String FILE = "mnt/sdcard/FirstPdf.pdf";
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    Image image;
    String fileName = "FirstPdf.pdf";
    String path = Environment.getExternalStorageDirectory() + "/" + fileName;
    private ImageView imgBackground;
    private AnimationDrawable animation;
    private ViewFlipper imageCarouselContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //imgBackground=(ImageView)findViewById(R.id.image_background);
        page1 = (ImageView) findViewById(R.id.page1);
        page2 = (ImageView) findViewById(R.id.page2);
        page3 = (ImageView) findViewById(R.id.page3);
        page4 = (ImageView) findViewById(R.id.page4);

        indicator1 = (ImageView) findViewById(R.id.dot1);
        indicator2 = (ImageView) findViewById(R.id.dot2);
        indicator3 = (ImageView) findViewById(R.id.dot3);
        indicator4 = (ImageView) findViewById(R.id.dot4);

        imageCarouselContainer = (ViewFlipper) findViewById(R.id.imageCarouselContainer);
        imageCarouselContainer.addOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);


        Button btnSignup=(Button)findViewById(R.id.signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this,SignupActivity.class));
            }
        });
        /*int readPermissionCheck = ContextCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[0]);
        int writePermissionCheck = ContextCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[1]);
        if (readPermissionCheck != PackageManager.PERMISSION_GRANTED && writePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSIONS_STORAGE[0], PERMISSIONS_STORAGE[1]},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            try {
                initializePDFWriter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mStatusTextView = (TextView) findViewById(R.id.status);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        imageCarouselContainer.setInAnimation(inFromRightAnimation());
        imageCarouselContainer.setOutAnimation(outToLeftAnimation());
        imageCarouselContainer.startFlipping();
      /*  imgBackground.setImageResource(R.drawable.background);
        animation = (AnimationDrawable) imgBackground.getDrawable();
        imgBackground.post(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });

        Animation animation1=new TranslateAnimation(0.0f, 200.0f, 0.0f, 0.0f);
        animation1.setDuration(1500);
        imgBackground.startAnimation(animation1);*/
    }

    @Override
    protected void onPause() {
        imageCarouselContainer.stopFlipping();
      /*  animation.stop();
        imgBackground.setImageDrawable(null);
        recycleMemoryForAnimation(animation);*/
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

      /*  OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private void addImage(Document document) {

        try {

            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
            icon.compress(Bitmap.CompressFormat.JPEG, 70, bytearrayoutputstream);

            byte[] bArray = bytearrayoutputstream.toByteArray();

            image = Image.getInstance(bArray);  ///Here i set byte array..you can do bitmap to byte array and set in image...
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // image.scaleAbsolute(150f, 150f);
        try {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        initializePDFWriter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initializePDFWriter() throws Exception {
        Document document = new Document();

        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        addImage(document);
        document.close();
    }

    private void recycleMemoryForAnimation(AnimationDrawable anim) {
        if (anim != null) {
            anim.stop();
            for (int i = 0; i < anim.getNumberOfFrames(); ++i) {
                Drawable frame = anim.getFrame(i);
                if (frame != null && frame instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) frame).getBitmap();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        //bitmap.recycle();
                        bitmap = null;
                        System.gc();
                    }
                }
                frame.setCallback(null);
            }
            anim.setCallback(null);
        }
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromRight.setDuration(1000);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        outtoLeft.setDuration(1000);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    View.OnLayoutChangeListener onLayoutChangeListener_viewFlipper = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            if (imageCarouselContainer.getCurrentView() == page1) {
                indicator1.setImageResource(R.drawable.page_selected_indicator);
                indicator2.setImageResource(R.drawable.page_nonselected_indicator);
                indicator3.setImageResource(R.drawable.page_nonselected_indicator);
                indicator4.setImageResource(R.drawable.page_nonselected_indicator);
            } else if (imageCarouselContainer.getCurrentView() == page2) {
                indicator1.setImageResource(R.drawable.page_nonselected_indicator);
                indicator2.setImageResource(R.drawable.page_selected_indicator);
                indicator3.setImageResource(R.drawable.page_nonselected_indicator);
                indicator4.setImageResource(R.drawable.page_nonselected_indicator);
            } else if (imageCarouselContainer.getCurrentView() == page3) {
                indicator1.setImageResource(R.drawable.page_nonselected_indicator);
                indicator2.setImageResource(R.drawable.page_nonselected_indicator);
                indicator3.setImageResource(R.drawable.page_selected_indicator);
                indicator4.setImageResource(R.drawable.page_nonselected_indicator);
            } else if (imageCarouselContainer.getCurrentView() == page4) {
                indicator1.setImageResource(R.drawable.page_nonselected_indicator);
                indicator2.setImageResource(R.drawable.page_nonselected_indicator);
                indicator3.setImageResource(R.drawable.page_nonselected_indicator);
                indicator4.setImageResource(R.drawable.page_selected_indicator);
            }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageCarouselContainer.removeOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);
    }
}
