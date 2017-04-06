package com.poc.evault.activities;

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
public class RegisterEmailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        final EditText edtEmail=(EditText)findViewById(R.id.email);
        final Button btnRegister=(Button)findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edtEmail.getText().toString().trim();

                if(email.equals("")){
                    edtEmail.setError("Please enter valid email");
                }else{
                    String buttonTitle=btnRegister.getText().toString();
                    if(buttonTitle.equalsIgnoreCase("Register")){
                    edtEmail.setEnabled(false);
                    btnRegister.setText("Edit");
                    }else{
                        edtEmail.setEnabled(true);
                        btnRegister.setText("Register");
                    }
                }
            }
        });
    }
}
