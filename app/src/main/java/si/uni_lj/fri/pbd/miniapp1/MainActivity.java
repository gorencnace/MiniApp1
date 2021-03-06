/*
 * MAIN ACTIVITY
 *
 * It's all downhill from here!
 *
 */

package si.uni_lj.fri.pbd.miniapp1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.Contact;

public class MainActivity extends AppCompatActivity {

    // Configuration option for NavigationUI methods (for Toolbar)
    private AppBarConfiguration mAppBarConfiguration;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int PERMISSION_REQUEST_CODE = 1;
    // Mind that this ArrayList of contacts also stores the state of contact (if contact was
    // selected for further use), because we don't have to store selected contacts separately.
    private ArrayList<Contact> contactList;

    private String key = "contacts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // permission checkup
        if (!checkPermission()) {
            requestPermission();
        }
        // restores contactList if it was not initialized yet
        if (savedInstanceState == null || !savedInstanceState.containsKey(key)) {
            setContactList();
        } else {
            contactList = savedInstanceState.getParcelableArrayList(key);
        }

        setContentView(R.layout.activity_main);

        // sets a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout allows interactive "drawer" views to be pulled out from edges of the window
        // sets a drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_contacts, R.id.nav_message)
                .setDrawerLayout(drawer)
                .build();
        // finds a NavController given the id of a View and its containing Activity
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // sets up the ActionBar returned by AppCompactActivity (this activity) for use with a NavController
        // AppBarConfiguration controls how the navigation button is displayed
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // sets up a NavigationView for use with a NavController
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    // saves a contactList on screen rotations (onDestroy)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(key, contactList);
        super.onSaveInstanceState(outState);
    }

    // this method is called whenever the user chooses to navigate UP within your application's
    // activity hierarchy from the action bar (to handle left up button)
    // We also set profile photo here. Why? I tried to do this in onCreate and in onResume and
    // it turns out that ImageView is not initialized there yet so I needed to find another
    // solution for this problem.
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        setProfilePhoto();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /*
     *  PERMISSION CHECKUP
     *
     *  Based on https://www.androidauthority.com/send-sms-messages-app-development-856280/
     *
     */

    // checks if permissions were already obtained
    public boolean checkPermission() {
        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS);
        int CameraPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);
        return ContactPermissionResult == PackageManager.PERMISSION_GRANTED &&
                CameraPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    // requests permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CAMERA
                }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean ReadContactsPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean CameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (ReadContactsPermission && CameraPermission) {
                    Toast.makeText(MainActivity.this, "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*
     *  SETTING & GETTING CONTACT LIST
     *
     *  Based on https://stackoverflow.com/questions/26804387/android-fetch-all-contact-list-name-email-phone-takes-more-then-a-minute-for/26820544
     *
     */

    public void setContactList() {
        // we use 2 arrays for extra speed
        this.contactList = new ArrayList<Contact>();
        LongSparseArray<Contact> array = new LongSparseArray<Contact>();
        // cursor setup
        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;
        // we could use Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
        Uri uri = ContactsContract.Data.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIdx);
            Contact contact = array.get(id);
            // creates contact if it is null
            if (contact == null) {
                contact = new Contact();
                contact.setContactName(cursor.getString(nameIdx));
                array.put(id, contact);
                contactList.add(contact);
            }
            String data = cursor.getString(dataIdx);
            String mimeType = cursor.getString(mimeTypeIdx);
            // gets email address or phone number based on mimeType (one of selectionArgs in cursor setup)
            if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                if (contact.getContactEmailAddress() == null) {
                    contact.setContactEmailAddress(data);
                }
            } else {
                if (contact.getContactPhoneNumber() == null) {
                    contact.setContactPhoneNumber(data);
                }
            }
        }
        cursor.close();
    }

    // used to pass data to fragments
    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    /*
     * PROFILE PHOTO
     *
     * In this section, we have methods for taking and setting profile photo.
     * Code is based on professor's code from lectures.
     *
     */

    public void takePhoto(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check if it allows me to take a picture
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // We put imageBitmap in SharedPreferences
            SharedPreferences sharedPreferences = getApplicationContext()
                    .getSharedPreferences("profile_image", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profile_image", BitMapToString(imageBitmap));
            editor.apply();
            // We get profile photo back from shared preferences
            setProfilePhoto();
        }
    }

    public void setProfilePhoto() {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("profile_image", Context.MODE_PRIVATE);
        ImageView profilePhoto = (ImageView) findViewById(R.id.imageProfile);
        String photoString = sharedPreferences.getString("profile_image", null);
        if (photoString != null) {
            profilePhoto.setImageBitmap(StringToBitMap(photoString));
        }
    }

    /*
     * BITMAP <=> STRING CONVERSION
     *
     * The code below (2 methods) is taken from http://androidtrainningcenter.blogspot.com/2012/03/how-to-convert-string-to-bitmap-and.html
     *
     */

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
