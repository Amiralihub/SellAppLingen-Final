package sellapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sellapplingen.R;

import java.util.concurrent.CompletableFuture;

import sellapp.models.NetworkManager;
import sellapp.models.SetPassword;

/**
 * This activity allows users to change their password.
 * Users can enter their old password and set a new password.
 * The new password is sent to the server via the NetworkManager class.
 */

public class ChangePasswordActivity extends AppCompatActivity
    {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;

    /**
     * Initializes the activity and sets the layout.
     *
     * @param savedInstanceState The saved state bundle, if available.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);


        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        Button changePasswordButton = findViewById(R.id.btnChangePassword);
        Button cancelButton = findViewById(R.id.btnCancel);

        cancelButton.setOnClickListener(v -> finish());


        changePasswordButton.setOnClickListener(v ->
                                                    {
                                                    String oldPassword = oldPasswordEditText.getText().toString();
                                                    String newPassword = newPasswordEditText.getText().toString();
                                                    if (oldPassword.isEmpty() || newPassword.isEmpty())
                                                        {
                                                        Toast.makeText(
                                                                ChangePasswordActivity.this, "Bitte füllen Sie beide Felder aus",
                                                                Toast.LENGTH_SHORT
                                                                      ).show();
                                                        }
                                                    else
                                                        {
                                                        setPassword(oldPassword, newPassword);
                                                        }
                                                    });
        }

    /**
     * Sends a request to the server to change the password.
     *
     * @param oldPassword The user's old password.
     * @param newPassword The new password to be set.
     */

    private void setPassword(String oldPassword, String newPassword)
        {
        SetPassword setPasswordObject = new SetPassword();
        setPasswordObject.setOldPassword(oldPassword);
        setPasswordObject.setNewPassword(newPassword);


        try
            {
            CompletableFuture<String> setPasswordFuture = NetworkManager.sendPostRequest(
                    NetworkManager.APIEndpoints.SET_PASSWORD.getUrl(), setPasswordObject);
            setPasswordFuture.thenAccept(result ->
                                             {
                                             if (result != null && result.equals(
                                                     "Password updated"))
                                                 {
                                                 runOnUiThread(() ->
                                                                   {
                                                                   Toast.makeText(
                                                                           ChangePasswordActivity.this,
                                                                           "Passwort erfolgreich geändert!",
                                                                           Toast.LENGTH_LONG
                                                                                 ).show();
                                                                   finish();
                                                                   });
                                                 }
                                             else
                                                 {
                                                 runOnUiThread(() -> Toast.makeText(
                                                         ChangePasswordActivity.this,
                                                         "Das alte Passwort ist nicht korrekt",
                                                         Toast.LENGTH_SHORT
                                                               ).show());
                                                 }
                                             });
            } catch (Exception e)
            {
            e.printStackTrace();
            }
        }
    }
