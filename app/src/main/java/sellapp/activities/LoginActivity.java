package sellapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sellapp.models.LogInData;
import com.example.sellapplingen.R;

public class LoginActivity extends AppCompatActivity
{
    private EditText usernameEditText;
    private EditText passwordEditText;
    private LogInData loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView loginImageView = findViewById(R.id.loginImageView);
        ImageView logobigImageView = findViewById(R.id.logobigImageView);
        Button loginButton = findViewById(R.id.buttonLogin);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);

        loginManager = LogInData.getInstance(getApplicationContext());
        loginButton.setOnClickListener(v ->
        {
            login();
        });
    }

    private void login()
    {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
        {
            // Behandlung von leerem Benutzernamen oder Passwort
            Toast.makeText(this, "Benutzername und Passwort sind erforderlich", Toast.LENGTH_SHORT).show();
            return;
        }

        LogInData loginData = new LogInData(username, password);

        if (loginManager.sendPost(loginData))
        {
            Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_SHORT).show();
            goToScannerFragment();
        } else
        {
            Toast.makeText(this, "Anmeldung fehlgeschlagen. Benutzername oder Passwort ist falsch", Toast.LENGTH_SHORT).show();
        }
    }


//refactor, use gotofragment methode from fragmentmanagerhelper
    private void goToScannerFragment()
    {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}