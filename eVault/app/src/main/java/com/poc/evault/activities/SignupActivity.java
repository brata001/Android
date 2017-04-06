package com.poc.evault.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.poc.evault.R;
import com.poc.evault.utils.SharedPreferenceUtil;

/**
 * Created by DASP2 on 4/1/2017.
 */
public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText edtEmail=(EditText)findViewById(R.id.email);
        final EditText edtPassword=(EditText)findViewById(R.id.password);
        final EditText edtConfirmPassword=(EditText)findViewById(R.id.confirm_password);
        Button btnSignup=(Button)findViewById(R.id.signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edtEmail.getText().toString().trim();
                String password=edtPassword.getText().toString().trim();
                String confirmPassword=edtConfirmPassword.getText().toString().trim();

                if(email.equals("")){
                    edtEmail.setError("Please enter valid email");
                }else if(password.equals("")){
                    edtPassword.setError("Please enter valid password");
                }else if(confirmPassword.equals("")){
                    edtConfirmPassword.setError("Please enter valid confirm password");
                }else if(!password.equals(confirmPassword)){
                    edtPassword.setError("Please enter same password in both password fields");
                    edtConfirmPassword.setError("Please enter same password in both password fields");
                }else{
                    SharedPreferenceUtil.setEmail(email);
                    SharedPreferenceUtil.setPassword(password);
                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignupActivity.this, SplashActivity.class));
        finish();
    }
}
