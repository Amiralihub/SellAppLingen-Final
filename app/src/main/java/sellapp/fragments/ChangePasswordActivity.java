package sellapp.fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sellapplingen.R;
import java.util.concurrent.CompletableFuture;
import sellapp.models.NetworkManager;
import sellapp.models.SetPassword;


public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);


        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        Button changePasswordButton = findViewById(R.id.btnChangePassword);
        Button cancelButton = findViewById(R.id.btnCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hier können Sie die Aktionen definieren, die beim Klicken auf den "Abbrechen"-Button ausgeführt werden sollen.
                // Zum Beispiel, die Aktivität schließen oder zurück zur vorherigen Ansicht wechseln.
                finish(); // Dies schließt die Aktivität (geht zurück zur vorherigen Ansicht).
            }
        });


        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Bitte füllen Sie beide Felder aus", Toast.LENGTH_SHORT).show();
                } else {
                    setPassword(oldPassword, newPassword);
                }
            }
        });
    }

    private void setPassword(String oldPassword, String newPassword) {
        SetPassword setPasswordObject = new SetPassword();
        setPasswordObject.setOldPassword(oldPassword);
        setPasswordObject.setNewPassword(newPassword);


        try {
            CompletableFuture<String> setPasswordFuture = NetworkManager.sendPostRequest(NetworkManager.APIEndpoints.SET_PASSWORD.getUrl(), setPasswordObject);
            setPasswordFuture.thenAccept(result -> {
                if (result != null && result.equals("Password updated")) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePasswordActivity.this, "Passwort erfolgreich geändert!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePasswordActivity.this, "Das alte Passwort ist nicht korrekt", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
