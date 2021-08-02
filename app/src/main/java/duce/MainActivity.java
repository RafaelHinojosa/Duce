package duce;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.duce.databinding.MainActivityBinding;
import duce.fragments.ChatsFragment;
import duce.fragments.FinderFragment;
import duce.fragments.ProfileMainFragment;
import duce.models.CustomUser;

import com.duce.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private ParseUser profileUser;
    private boolean goToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomUser mUser = new CustomUser(ParseUser.getCurrentUser());
        mUser.setOnline(true);
        mUser.getCustomUser().saveInBackground();

        MainActivityBinding binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        goToProfile = false;

        // Comes from FoundUsersAdapter to see a users profile
        profileUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if (profileUser == null) {
            profileUser = ParseUser.getCurrentUser();
        } else {
            goToProfile = true;
        }

        BottomNavigationView navView = binding.navView;
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                int itemId = item.getItemId();
                if (goToProfile) {
                    itemId = R.id.profile_navigation;
                }

                switch (itemId) {
                    case R.id.finder_action:
                        fragment = new FinderFragment();
                        break;
                    case R.id.profile_navigation:
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", Parcels.wrap(profileUser));
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
        if (goToProfile) {
            navView.setSelectedItemId(R.id.profile_navigation);
            goToProfile = false;
            profileUser = ParseUser.getCurrentUser();
        } else {
            navView.setSelectedItemId(R.id.chats_navigation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ParseUser.getCurrentUser() == null) {
            return;
        }

        CustomUser mUser = new CustomUser(ParseUser.getCurrentUser());

        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        Date lastConnection = new Date(year - 1900, month, day, 0, 0, 0);
        mUser.setLastConnection(lastConnection);
        mUser.setOnline(false);
        mUser.getCustomUser().saveInBackground();
    }
}