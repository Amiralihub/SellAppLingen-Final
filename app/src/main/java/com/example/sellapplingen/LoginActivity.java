// LoginActivity.java
package com.example.sellapplingen;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);


        // Verwende den ApplicationContext für den LoginManager
        loginManager = LoginManager.getInstance(getApplicationContext());

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String enteredUsername = usernameEditText.getText().toString();
        String enteredPassword = passwordEditText.getText().toString();

        loginManager.username = enteredUsername; // Setze die Benutzernamen und Passwort
        loginManager.password = enteredPassword;

        try {
            // Sende die Benutzerdaten an den Server
            loginManager.sendPost(this::goToScannerFragment);

        } catch (NullPointerException e) {
            // Handle NullPointerException
            Toast.makeText(this, "Name or password is null", Toast.LENGTH_SHORT).show();
            return;
        } catch (IllegalArgumentException e) {
            // Handle IllegalArgumentException
            Toast.makeText(this, "Name or password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void goToScannerFragment() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish(); // Schließt die LoginActivity, damit der Benutzer nicht dorthin zurückkehren kann
    }

}