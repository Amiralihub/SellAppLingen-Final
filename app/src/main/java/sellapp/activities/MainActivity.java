package sellapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sellapp.models.LogInData;
import sellapp.models.Order;
import com.example.sellapplingen.R;
import com.example.sellapplingen.databinding.ActivityMainBinding;

import sellapp.fragments.OrderHistoryFragment;
import sellapp.fragments.ScannerFragment;
import sellapp.fragments.SettingFragment;

public class MainActivity extends AppCompatActivity
{
    ActivityMainBinding binding;
    private Order currentOrder;
    private LogInData loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Verwende den ApplicationContext für den LoginManager
        loginManager = LogInData.getInstance(getApplicationContext());

        if (loginManager.isLoggedIn())
        {
            // Wenn der Benutzer nicht angemeldet ist, starte die LoginActivity
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Setze das Layout und den Navigation Listener nur wenn der Benutzer eingeloggt ist
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null)
        {
            // Erstelle ein neues Order-Objekt und speichere es in der MainActivity
            Order order = new Order();
            setCurrentOrder(order);

            // Ersetze das Fragment durch das ScannerFragment und übergebe das Order-Objekt
            replaceFragment(ScannerFragment.newInstance(order));
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.action_scanner:
                    replaceFragment(ScannerFragment.newInstance(getCurrentOrder()));
                    break;
                case R.id.action_orders:
                    replaceFragment(new OrderHistoryFragment());
                    break;
                case R.id.action_settings:
                    replaceFragment(new SettingFragment());
                    break;
            }
            return true;
        });
    }


    //refactor, use gotofragment methode from fragmentmanagerhelper
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void setCurrentOrder(Order order)
    {
        currentOrder = order;
    }

    public Order getCurrentOrder()
    {
        return currentOrder;
    }

}