package com.sqlite.sqliteapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.regex.Pattern;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.sqlite.sqliteapp.Views.BookeUser;
import com.sqlite.sqliteapp.Views.MenuFly;
//import com.sqlite.sqliteapp.Views.wish;

//123
public class MainActivity extends AppCompatActivity {
    EditText editName, editPhone, editEmail, editAddress, editUfid;
    Button addDataButton, viewDataButton;
    MenuFly root;

    ImageButton imageButton;
    ImageView im;
    private Bitmap bm;
    private ParseFile parseFile;
    String imgPath;
    private String selectedImagePath = "";
 //   private BookeUser bookUser;

    String name, phone_no, email, ufid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.root = (MenuFly) this.getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(root);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                finish();
            }
        }, intentFilter);

        ufid = getIntent().getExtras().getString("ufid");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.myimage);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        parseFile = new ParseFile("profileImage.png", image);

        imageButton = (ImageButton) findViewById(R.id.imageButton1);
        im = (ImageView)findViewById(R.id.iv);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectImage();
            }
        });
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        TextView tx = (TextView) findViewById(R.id.Title);
        Typeface cd = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
        tx.setTypeface(cd);

        editName = (EditText) findViewById(R.id.name);
        editPhone = (EditText) findViewById(R.id.phone);
        editEmail = (EditText) findViewById(R.id.email);
        editAddress = (EditText) findViewById(R.id.address);
        addDataButton = (Button) findViewById(R.id.submitbutton);
        // viewDataButton = (Button)findViewById(R.id.viewdata);
        addData();

    }

    public void viewAccount(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("My Account")){
            Intent intent = new Intent(this, ViewActivity.class);
            intent.putExtra("MESSAGE", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
        }
    }

    public Bitmap decodeFile(String path) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            final int REQUIRED_SIZE = 70;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                bm = (Bitmap) data.getExtras().get("data");

                if (bm != null) {
                    im.setImageBitmap(bm);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    byte[] imageData = bytes.toByteArray();
                    parseFile = new ParseFile("image.jpg", imageData);
                }
            } else if (requestCode == 2) {
                selectedImagePath = getAbsolutePath(data.getData());
                bm = decodeFile(selectedImagePath);
                if(bm != null) {
                    im.setImageBitmap(bm);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    byte[] imageData = bytes.toByteArray();
                    parseFile = new ParseFile("image.jpg", imageData);
                }
            }
        }
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private void selectImage() {
        final CharSequence[] items = { "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Library")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void toggleMenu(View v){
        this.root.toggleMenu();
    }

    public void openBook(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Add Book")){
            Intent intent = new Intent(this, book_upload.class);
            startActivity(intent);
        }
    }

    public void searchBooks(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Search Books")){
            Intent intent = new Intent(this, find_books.class);
            startActivity(intent);
        }
    }

    public void myBooks(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("My Books")){
            Intent intent = new Intent(this, my_book.class);
            startActivity(intent);
        }
    }

    public void logOut(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Logout")){
            ParseUser.logOut();
            finish();
        }
    }

    public Boolean validate_data(String name, String phone_no, String email){
        Boolean invalid = false;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (name.length() == 0) {
            editName.setError("Name is required!");
            invalid = true;
        }
        if ((phone_no.length() > 0) && (phone_no.length() != 10)) {
            editPhone.setError("Invalid Phone Number!");
            invalid = true;
        }
        if (!pattern.matcher(email).matches()) {
            editEmail.setError("Invalid Email!");
            invalid = true;
        }
        return invalid;
    }

    public void addData(){
        addDataButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        name = editName.getText().toString();
                        phone_no = editPhone.getText().toString();
                        email = editEmail.getText().toString();

                        boolean invalid = validate_data(name, phone_no, email);

                        if (!invalid) {
                            ParseUser userB = new ParseUser();
                            userB.setUsername(ufid);
                            userB.setEmail(email);
                            userB.setPassword("213");
                            userB.put("PhoneNumber", phone_no);
                            userB.put("Address", editAddress.getText().toString());
                            userB.put("ActualName", name);

                            userB.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "Successfully Signed up.",
                                                Toast.LENGTH_LONG).show();
                                        parseFile.saveInBackground(new SaveCallback() {
                                            public void done(ParseException e) {
                                                if (e != null) {
                                                    Toast.makeText(getApplicationContext(), "File could not be added.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                                    currentUser.put("image", parseFile);
                                                    currentUser.saveEventually();

                                                    Toast.makeText(getApplicationContext(),
                                                            "added image to database", Toast.LENGTH_LONG)
                                                            .show();

                                                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                                                    intent.putExtra("MESSAGE", ufid);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Sign up Error", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }
                    }
                }
        );
    }
    public void wishBook(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Wish Book")){
            Intent intent = new Intent(this, wish.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
