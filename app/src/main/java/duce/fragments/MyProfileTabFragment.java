package duce.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.duce.databinding.MyProfileTabFragmentBinding;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import duce.StartActivity;
import duce.models.CustomUser;
import duce.models.Languages;
import duce.models.Messages;
import duce.models.UserLanguages;

import static com.duce.R.layout.chats_fragment;
import static com.duce.R.layout.mtrl_alert_select_dialog_item;
import static com.duce.R.layout.my_profile_tab_fragment;

public class MyProfileTabFragment extends Fragment {

    private static final String TAG = "MyProfileTabFragment";
    public final static int PICK_PHOTO_CODE = 100;
    private final String photoFileName = "photo.png";

    private boolean isMe;
    private ImageView mIvProfilePicture;
    private ImageButton mIbEditProfile;
    private TextView mTvUsername;
    private EditText mEtUsername;
    private TextView mTvAge;
    private EditText mEtAge;
    private TextView mTvSelfDescription;
    private EditText mEtSelfDescription;
    private TextView mTvAddLanguage;
    private TextView mTvAddInterest;
    private com.google.android.flexbox.FlexboxLayout mFlMyLanguages;
    private com.google.android.flexbox.FlexboxLayout mFlMyInterests;
    private Button mBtnLogOut;
    private CustomUser mUser;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private File photoFile;

    public static MyProfileTabFragment newInstance() {
        MyProfileTabFragment fragment = new MyProfileTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(my_profile_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receives the user to display
        Bundle bundle = this.getArguments();
        assert bundle != null;
        mUser = new CustomUser(Parcels.unwrap(bundle.getParcelable("user")));

        isMe = mUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId());

        mIvProfilePicture = view.findViewById(R.id.ivProfilePicture);

        // The Current User enters to an edit mode
        if (isMe) {
            mIbEditProfile = view.findViewById(R.id.ibEditProfilePicture);
            mEtUsername = view.findViewById(R.id.etUsername);
            mEtAge = view.findViewById(R.id.etAge);
            mEtSelfDescription = view.findViewById(R.id.etDescription);

            mIbEditProfile.setVisibility(View.VISIBLE);
            mEtUsername.setVisibility(View.VISIBLE);
            mEtAge.setVisibility(View.VISIBLE);
            mEtSelfDescription.setVisibility(View.VISIBLE);

            setAgeClickListener();
        } else {
            mTvUsername = view.findViewById(R.id.tvUsername);
            mTvAge = view.findViewById(R.id.tvAge);
            mTvSelfDescription = view.findViewById(R.id.tvDescription);

            mTvUsername.setVisibility(View.VISIBLE);
            mTvAge.setVisibility(View.VISIBLE);
            mTvSelfDescription.setVisibility(View.VISIBLE);
        }

        mFlMyLanguages = view.findViewById(R.id.flMyLanguages);
        mFlMyInterests = view.findViewById(R.id.flMyInterests);
        mBtnLogOut = view.findViewById(R.id.btnLogOut);

        mBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomUser mUser = new CustomUser(ParseUser.getCurrentUser());

                Calendar today = Calendar.getInstance();
                int year = today.get(Calendar.YEAR);
                int month = today.get(Calendar.MONTH);
                int day = today.get(Calendar.DAY_OF_MONTH);

                Date lastConnection = new Date(year - 1900, month, day, 0, 0, 0);
                mUser.setLastConnection(lastConnection);
                mUser.setOnline(false);
                mUser.getCustomUser().saveInBackground();

                ParseUser.logOut();
                Intent toStartActivity = new Intent(getActivity(), StartActivity.class);
                startActivity(toStartActivity);
            }
        });

       bind();
    }

    private void bind() {
        Glide.with(getContext())
             .load(mUser.getProfilePicture().getUrl())
             .centerCrop()
             .transform(new CircleCrop())
             .into(mIvProfilePicture);

        // Set age
        String age = "";
        Date birthdate = mUser.getBirthdate();
        if (birthdate != null) {
            int year = birthdate.getYear() + 1900;
            int month = birthdate.getMonth() + 1;
            int day = birthdate.getDate();
            age = getAge(year, month, day);
        }

        if (isMe) {
            mEtUsername.setText(mUser.getUsername());
            mEtAge.setText(age);
            mEtSelfDescription.setText(mUser.getSelfDescription());

            mEtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView tvUsername, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String username = tvUsername.getText().toString();
                        updateUsername(username);
                        return false;
                    }
                    return false;
                }
            });
            mEtSelfDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView tvDescription, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        mUser.setSelfDescription(tvDescription.getText().toString());
                        mUser.getCustomUser().saveInBackground();
                        Toast.makeText(getContext(), "Self description updated correctly!", Toast.LENGTH_SHORT).show();
                        return false;   // This makes the keyboard hide
                    }
                    return false;
                }
            });
            mIbEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPickPhoto();
                }
            });
        } else {
            mTvUsername.setText(mUser.getUsername());
            mTvAge.setText(age);
            mTvSelfDescription.setText(mUser.getSelfDescription());
        }

        setLanguages();
    }

    public void updateUsername(String username) {
        ParseQuery<ParseUser> findUser = ParseUser.getQuery();
        findUser.whereEqualTo("username", username);

        findUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                if (objects.size() != 0 && !username.equals(mUser.getUsername())) {
                    mEtUsername.setText(R.string.invalid_username);
                } else {
                    mUser.getCustomUser().setUsername(username);
                    mUser.getCustomUser().saveInBackground();

                    Toast.makeText(getContext(), "Username updated correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setAgeClickListener() {
        mEtAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                int year = today.get(Calendar.YEAR);
                int month = today.get(Calendar.MONTH);
                int day = today.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog birthdatePicker = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog,
                        mDateSetListener,
                        year,
                        month,
                        day
                );
                birthdatePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                birthdatePicker.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String age = getAge(year, month, dayOfMonth);
                mEtAge.setText(age);

                if (!age.equals("Invalid birthdate")) {
                    Date birthDate = new Date(year - 1900, month, dayOfMonth, 0, 0, 0);
                    mUser.setBirthdate(birthDate);
                    mUser.getCustomUser().saveInBackground();
                }
            }
        };
    }

    public void setLanguages() {
        if (isMe) {
            setAddLanguageTV();
        }

        ParseQuery<UserLanguages> userLanguagesQuery = ParseQuery.getQuery("UserLanguages");
        userLanguagesQuery.whereEqualTo("userId", mUser.getCustomUser());
        userLanguagesQuery.include("languageId");
        userLanguagesQuery.addDescendingOrder("createdAt");

        userLanguagesQuery.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                for (UserLanguages userLanguage : userLanguages) {
                    Log.i(TAG, userLanguage.toString());
                    TextView language = setLanguageTV(userLanguage.getLanguage().getLanguageName());
                    TextView languageCopy = setLanguageTV(userLanguage.getLanguage().getLanguageName());

                    if (userLanguage.getMyLanguage()) {
                        mFlMyLanguages.addView(language);
                    }

                    if (userLanguage.getInterestedIn()) {
                        mFlMyInterests.addView(languageCopy);
                    }
                }
            }
        });
    }

    public TextView setLanguageTV(String languageName) {
        TextView textView = new TextView(getContext());
        textView.setText(languageName);
        textView.setIncludeFontPadding(true);
        textView.setPadding(20,10,20,10);
        textView.setCompoundDrawablePadding(5);
        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.language_box));
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(params);

        return textView;
    }

    @SuppressLint("ResourceAsColor")
    public void setAddLanguageTV() {
        mTvAddLanguage = new TextView(getContext());

        mTvAddLanguage.setText(R.string.add);
        mTvAddLanguage.setIncludeFontPadding(true);
        mTvAddLanguage.setPadding(20,10,20,10);
        mTvAddLanguage.setCompoundDrawablePadding(5);
        mTvAddLanguage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.add_language_box));
        mTvAddLanguage.setTextColor(R.color.add_blue);

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        mTvAddLanguage.setLayoutParams(params);

        mFlMyLanguages.addView(mTvAddLanguage);

        mTvAddLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "me diste on click");
                // Call the dialog...
                // With the given languages, add them to the database
            }
        });
    }

    public void onPickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if(Build.VERSION.SDK_INT > 27){
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);

            // Get image path
            photoFile = getPhotoFileUri(photoFileName);

            // Convert bitmap to bytes array
            ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
            // Compress selected image and assigns it to bytesStream
            selectedImage.compress(Bitmap.CompressFormat.PNG, 50, bytesStream);
            byte[] byteImage = bytesStream.toByteArray();
            selectedImage.recycle();

            // Convert bytes array into Parse File
            ParseFile profileFile = new ParseFile(byteImage);
            mUser.setProfilePicture(profileFile);
            mUser.getCustomUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error: " + e.getMessage());
                        Toast.makeText(getContext(), "Could not change the profile picture", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getContext(), "Profile picture changed successfully!", Toast.LENGTH_SHORT).show();
                    Glide.with(getContext())
                        .load(profileFile.getUrl())
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(mIvProfilePicture);
                }
            });
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // Returns the age of the user based on its birthdate
    public String getAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        birthdate.set(year, month, day);

        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            return "Invalid birthdate";
        }

        Integer ageNumber = new Integer(age);
        String ageString = ageNumber.toString() + " years old";
        return ageString;
    }
}