// LoginActivity.java
package com.example.sellapplingen;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView loginImageView = findViewById(R.id.loginImageView);
        ImageView logobigImageView = findViewById(R.id.logobigImageView);
        Button loginButton = findViewById(R.id.buttonLogin);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);

        loginManager = LoginManager.getInstance(getApplicationContext());
        loginButton.setOnClickListener(v ->{
            login();
            Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_SHORT).show();
        });
    }

    private void login() {

        if(TextUtils.isEmpty(usernameEditText.getText()) || TextUtils.isEmpty(passwordEditText.getText())) {
            return;
        }
        LogInData loginData = new LogInData(usernameEditText.getText().toString(), passwordEditText.getText().toString());

        if(loginManager.sendPost(loginData)){
            goToScannerFragment();
        }
    }

    private void goToScannerFragment() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}