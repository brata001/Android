package com.poc.evault.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
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
    private ViewFlipper imageCarouselContainer;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        TextView txtLogin=(TextView)findViewById(R.id.already_have_an_account_label);
        txtLogin.setOnClickListener(this);

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
        btnSignup.setOnClickListener(this);

        LinearLayout btnGoogleLogin=(LinearLayout)findViewById(R.id.google_login);
        LinearLayout btnFacebookLogin=(LinearLayout)findViewById(R.id.facebook_login);
        LinearLayout btnEVaultLogin=(LinearLayout)findViewById(R.id.evault_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //.requestScopes(new Scope(Scopes.PROFILE))
                //.requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestProfile()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.addApi(Plus.API)
                .build();
        btnGoogleLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnEVaultLogin.setOnClickListener(this);

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

    */


    }

    @Override
    protected void onResume() {
        super.onResume();
        imageCarouselContainer.setInAnimation(inFromRightAnimation());
        imageCarouselContainer.setOutAnimation(outToLeftAnimation());
        imageCarouselContainer.startFlipping();
    }

    @Override
    protected void onPause() {
        imageCarouselContainer.stopFlipping();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Intent intent=new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("URL",acct.getPhotoUrl().toString());
            intent.putExtra("NAME",acct.getDisplayName());
            intent.putExtra("EMAIL",acct.getEmail());
            startActivity(intent);
            finish();
        } else {
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup:
                startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                break;
            case R.id.already_have_an_account_label:
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
            case R.id.google_login:
                signIn();
                break;
            case R.id.evault_login:
                startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                break;
            case R.id.facebook_login:
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

    @Override
    public void onBackPressed() {
        if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }
}
