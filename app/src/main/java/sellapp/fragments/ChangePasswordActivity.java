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
        setContentView(R.layout.change_password);  // Assuming the layout file is named change_password.xml

        System.out.println("Waaaaas");


        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);  // Replace with the actual ID
        newPasswordEditText = findViewById(R.id.newPasswordEditText);  // Replace with the actual ID
        Button changePasswordButton = findViewById(R.id.btnChangePassword);  // Replace with the actual ID

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
        setPasswordObject.setToken("Joooooo");  // Replace with the actual method to get the token

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
