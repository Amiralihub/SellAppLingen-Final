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

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                setPassword(oldPassword, newPassword);
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
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
