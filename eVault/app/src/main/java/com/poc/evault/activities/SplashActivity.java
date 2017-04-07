package com.poc.evault.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.poc.evault.R;

public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private ImageView page1, page2, page3, page4;
    private ImageView indicator1, indicator2, indicator3, indicator4;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private ViewFlipper imageCarouselContainer;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        TextView txtLogin = (TextView) findViewById(R.id.already_have_an_account_label);
        txtLogin.setOnClickListener(this);

        page1 = (ImageView) findViewById(R.id.page1);
        page2 = (ImageView) findViewById(R.id.page2);
        // page3 = (ImageView) findViewById(R.id.page3);
        // page4 = (ImageView) findViewById(R.id.page4);

        //indicator1 = (ImageView) findViewById(R.id.dot1);
        //indicator2 = (ImageView) findViewById(R.id.dot2);
        //indicator3 = (ImageView) findViewById(R.id.dot3);
        //indicator4 = (ImageView) findViewById(R.id.dot4);

        imageCarouselContainer = (ViewFlipper) findViewById(R.id.imageCarouselContainer);
        //imageCarouselContainer.addOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);

        Button btnSignup = (Button) findViewById(R.id.signup);
        btnSignup.setOnClickListener(this);

        LinearLayout btnGoogleLogin = (LinearLayout) findViewById(R.id.google_login);
        LinearLayout btnFacebookLogin = (LinearLayout) findViewById(R.id.facebook_login);
        LinearLayout btnEVaultLogin = (LinearLayout) findViewById(R.id.evault_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        btnGoogleLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnEVaultLogin.setOnClickListener(this);

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
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("URL", acct.getPhotoUrl().toString());
            intent.putExtra("NAME", acct.getDisplayName());
            intent.putExtra("EMAIL", acct.getEmail());
            startActivity(intent);
            finish();
        } else {
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                finish();
                break;
            case R.id.already_have_an_account_label:
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
            case R.id.google_login:
                signIn();
                break;
            case R.id.evault_login:
                startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                finish();
                break;
            case R.id.facebook_login:
                break;
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

  /*  View.OnLayoutChangeListener onLayoutChangeListener_viewFlipper = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (imageCarouselContainer.getCurrentView() == page1) {
                indicator1.setImageResource(R.drawable.page_selected_indicator);
                indicator2.setImageResource(R.drawable.page_nonselected_indicator);
                // indicator3.setImageResource(R.drawable.page_nonselected_indicator);
                // indicator4.setImageResource(R.drawable.page_nonselected_indicator);
            } else if (imageCarouselContainer.getCurrentView() == page2) {
                indicator1.setImageResource(R.drawable.page_nonselected_indicator);
                indicator2.setImageResource(R.drawable.page_selected_indicator);
                //indicator3.setImageResource(R.drawable.page_nonselected_indicator);
                //indicator4.setImageResource(R.drawable.page_nonselected_indicator);
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
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //imageCarouselContainer.removeOnLayoutChangeListener(onLayoutChangeListener_viewFlipper);
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
