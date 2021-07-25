package duce;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.duce.databinding.MainActivityBinding;
import duce.fragments.ChatsFragment;
import duce.fragments.FinderFragment;
import duce.fragments.ProfileMainFragment;

import com.duce.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityBinding binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        BottomNavigationView navView = binding.navView;
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.finder_action:
                        fragment = new FinderFragment();
                        break;
                    case R.id.profile_navigation:
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", Parcels.wrap(ParseUser.getCurrentUser()));
                        fragment = new ProfileMainFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.chats_navigation:
                    default:
                        fragment = new ChatsFragment();
                        break;
                }
                // Replace the flContainer with the selected fragment layout
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection in bottom menu
        navView.setSelectedItemId(R.id.chats_navigation);
    }
}