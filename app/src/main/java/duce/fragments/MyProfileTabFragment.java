package duce.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.google.android.flexbox.FlexboxLayout;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import duce.MatchAlgorithm;
import duce.StartActivity;
import duce.adapters.LanguagesAdapter;
import duce.models.CustomUser;
import duce.models.Languages;
import duce.models.UserLanguages;
import es.dmoral.toasty.Toasty;

import static com.duce.R.layout.my_profile_tab_fragment;

public class MyProfileTabFragment extends Fragment {

    private static final String TAG = "MyProfileTabFragment";
    public final static int PICK_PHOTO_CODE = 100;
    private final String photoFileName = "photo.png";

    private ImageView mIvProfilePicture;
    private ImageButton mIbEditProfile;
    private EditText mEtUsername;
    private EditText mEtAge;
    private EditText mEtSelfDescription;
    private TextView mTvAddLanguage;
    private TextView mTvAddInterest;
    private TextView mTvDeleteLanguage;
    private TextView mTvDeleteInterest;
    private Button mBtnLogOut;
    private com.google.android.flexbox.FlexboxLayout mFlMyLanguages;
    private com.google.android.flexbox.FlexboxLayout mFlMyInterests;
    private CustomUser mUser;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private File photoFile;
    private LanguagesAdapter mLanAdapter;
    private String mLanSelected;
    private AlertDialog.Builder mLanSelector;
    private Languages mLanguageSelected;


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

        mIvProfilePicture = view.findViewById(R.id.ivProfilePicture);
        mLanSelector = new AlertDialog.Builder(getContext());
        mLanAdapter = new LanguagesAdapter();
        mLanSelected = "";

        mIbEditProfile = view.findViewById(R.id.ibEditProfilePicture);
        mEtUsername = view.findViewById(R.id.etUsername);
        mEtAge = view.findViewById(R.id.etAge);
        mEtSelfDescription = view.findViewById(R.id.etDescription);

        mIbEditProfile.setVisibility(View.VISIBLE);
        mEtUsername.setVisibility(View.VISIBLE);
        mEtAge.setVisibility(View.VISIBLE);
        mEtSelfDescription.setVisibility(View.VISIBLE);

        setAgeClickListener();

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

        if (birthdate == null || !age.equals("-1")) {
            mEtAge.setText(getString(R.string.set_birthday));
        }

        mEtUsername.setText(mUser.getUsername());
        mEtSelfDescription.setText(mUser.getSelfDescription());

        mEtSelfDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEtSelfDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEtSelfDescription.setMaxLines(2);

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
                }
            }
        });
    }

    // EDIT AGE METHODS
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

    public String getAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        birthdate.set(year, month, day);

        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 15) {
            return "Invalid birthdate";
        }

        Integer ageNumber = new Integer(age);
        String ageString = ageNumber.toString() + " years old";
        return ageString;
    }

    // EDIT PROFILE PICTURE METHODS
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
                        Toasty.normal(
                            getContext(),
                            R.string.profile_pic_error,
                            Toast.LENGTH_SHORT
                            )
                            .show();
                        return;
                    }
                    Glide.with(getContext())
                            .load(profileFile.getUrl())
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(mIvProfilePicture);
                }
            });
        }
    }

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

    // LANGUAGES METHODS
    public void setLanguages() {
        mFlMyLanguages.removeAllViews();
        mFlMyInterests.removeAllViews();

        setAddLanguageTV();
        setAddInterestTV();
        setDeleteLanguageTV();
        setDeleteInterestTV();

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

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        mTvAddLanguage.setLayoutParams(params);

        mFlMyLanguages.addView(mTvAddLanguage);

        mTvAddLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog("myLanguage", "add");
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void setAddInterestTV() {
        mTvAddInterest = new TextView(getContext());

        mTvAddInterest.setText(R.string.add);
        mTvAddInterest.setIncludeFontPadding(true);
        mTvAddInterest.setPadding(20,10,20,10);
        mTvAddInterest.setCompoundDrawablePadding(5);
        mTvAddInterest.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.add_language_box));

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        mTvAddInterest.setLayoutParams(params);

        mFlMyInterests.addView(mTvAddInterest);

        mTvAddInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog("interest", "add");
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void setDeleteLanguageTV() {
        mTvDeleteLanguage = new TextView(getContext());

        mTvDeleteLanguage.setText(R.string.delete_language);
        mTvDeleteLanguage.setIncludeFontPadding(true);
        mTvDeleteLanguage.setPadding(20,10,20,10);
        mTvDeleteLanguage.setCompoundDrawablePadding(5);
        mTvDeleteLanguage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.delete_language_box));

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        mTvDeleteLanguage.setLayoutParams(params);

        mFlMyLanguages.addView(mTvDeleteLanguage);

        mTvDeleteLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog("myLanguage", "delete");
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void setDeleteInterestTV() {
        mTvDeleteInterest = new TextView(getContext());

        mTvDeleteInterest.setText(R.string.delete_language);
        mTvDeleteInterest.setIncludeFontPadding(true);
        mTvDeleteInterest.setPadding(20,10,20,10);
        mTvDeleteInterest.setCompoundDrawablePadding(5);
        mTvDeleteInterest.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.delete_language_box));

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        mTvDeleteInterest.setLayoutParams(params);

        mFlMyInterests.addView(mTvDeleteInterest);

        mTvDeleteInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog("interest", "delete");
            }
        });
    }

    public void setupDialog(String languageType, String action) {
        mLanSelector.setTitle(R.string.select_language);
        mLanSelector.setCancelable(false);

        String[] languages = (String[]) mLanAdapter.getLanguages();

        // Arguments: (List, index of selection, click listener)
        mLanSelector.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i >= 0 && i < languages.length) {
                    mLanSelected = languages[i];
                    mLanguageSelected = mLanAdapter.getLanguageObject(i);
                }
            }
        });

        mLanSelector.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (action.equals("add")) {
                    addLanguage(mLanguageSelected, languageType);
                } else if (action.equals("delete")) {
                    deleteLanguage(mLanguageSelected, languageType);
                }
                dialogInterface.dismiss();
            }
        });

        mLanSelector.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLanSelected = "";
                dialog.dismiss();
            }
        });

        mLanSelector.show();
    }

    private void addLanguage(Languages mLanguageSelected, String type) {
        ParseQuery<UserLanguages> userLanguages = ParseQuery.getQuery("UserLanguages");
        userLanguages.whereEqualTo("userId", ParseUser.getCurrentUser());
        userLanguages.whereEqualTo("languageId", mLanguageSelected);

        userLanguages.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguagesList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                Log.i(TAG, String.valueOf(userLanguagesList.size()));

                // IF the association already exists
                if (userLanguagesList.size() > 0) {
                    if (type.equals("myLanguage")) {
                        if (!userLanguagesList.get(0).getMyLanguage()) {
                            userLanguagesList.get(0).setMyLanguage(true);
                            userLanguagesList.get(0).saveInBackground();
                        } else {
                            return; // the language already is registered as myLanguage
                        }
                    } else {
                        if (!userLanguagesList.get(0).getInterestedIn()) {
                            userLanguagesList.get(0).setInterestedIn(true);
                            userLanguagesList.get(0).saveInBackground();
                        } else {
                            return; // the language is already is registered as interest
                        }
                    }
                } else {
                    UserLanguages userLanguages = new UserLanguages();
                    userLanguages.setUser(ParseUser.getCurrentUser());
                    userLanguages.setLanguage(mLanguageSelected);
                    if (type.equals("myLanguage")) {
                        userLanguages.setMyLanguage(true);
                    } else {
                        userLanguages.setInterestedIn(true);
                    }
                    userLanguages.saveInBackground();
                }

                // Reload the languages lists
                setLanguages();
            }
        });
    }

    private void deleteLanguage(Languages mLanguageSelected, String type) {
        ParseQuery<UserLanguages> userLanguages = ParseQuery.getQuery("UserLanguages");
        userLanguages.whereEqualTo("userId", ParseUser.getCurrentUser());
        userLanguages.whereEqualTo("languageId", mLanguageSelected);

        userLanguages.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguagesList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                Log.i(TAG, String.valueOf(userLanguagesList.size()));

                // If the element does not exist, there is nothing to delete
                if (userLanguagesList.size() == 0) {
                    return;
                } else {
                    if (type.equals("myLanguage")) {
                        // Associated? then disassociate
                        if (userLanguagesList.get(0).getMyLanguage()) {
                            userLanguagesList.get(0).setMyLanguage(false);
                            userLanguagesList.get(0).saveInBackground();
                        } else {
                            return; // the language already is not registered as myLanguage
                        }
                    } else {
                        // The interest stops being true and is now false
                        if (userLanguagesList.get(0).getInterestedIn()) {
                            userLanguagesList.get(0).setInterestedIn(false);
                            userLanguagesList.get(0).saveInBackground();
                        } else {
                            return; // the language is already is not registered as interest
                        }
                    }
                }

                // Reload the languages lists
                setLanguages();
            }
        });
    }
}