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
public class SigninActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final EditText edtEmail=(EditText)findViewById(R.id.email);
        final EditText edtPassword=(EditText)findViewById(R.id.password);
        Button btnSignin=(Button)findViewById(R.id.signin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edtEmail.getText().toString().trim();
                String password=edtPassword.getText().toString().trim();
                String storedEmail=SharedPreferenceUtil.getEmail();
                String storedPassword=SharedPreferenceUtil.getPassword();

                if(email.equals("")){
                    edtEmail.setError("Please enter valid email");
                }else if(password.equals("")){
                    edtPassword.setError("Please enter valid password");
                }else if(!storedEmail.equals(email)){
                    edtEmail.setError("Please enter valid email");
                }else if(!storedPassword.equals(password)){
                    edtPassword.setError("Please enter valid password");
                }else{
                    startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                }
            }
        });
    }
}
