package com.sqlite.sqliteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sqlite.sqliteapp.Views.MenuFly;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ViewActivity extends Activity{

    private EditText nameValue, emailValue, phoneValue;
    private TextView nameText, emailText, phoneText;
    private ParseUser rateThisUser;
    private RatingBar ratingBar;
    private Button editDetails, submitDetails;
    private ParseFile parseFile = null;
    private Bitmap bm;
    private String selectedImagePath = "";

    MenuFly root;
    ParseImageView imageView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (MenuFly) this.getLayoutInflater().inflate(R.layout.activity_view, null);
        setContentView(root);
        final ImageButton b = (ImageButton)this.findViewById(R.id.buttonCall);
        final ImageButton sendEmail = (ImageButton)this.findViewById(R.id.buttonEmail);

        Intent intent = getIntent();
        String EXTRA_MESSAGE = "MESSAGE";
        final String userName = intent.getStringExtra(EXTRA_MESSAGE);


        imageView = (ParseImageView) findViewById(R.id.icon);
        final ParseUser u = ParseUser.getCurrentUser();

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        editDetails = (Button) findViewById(R.id.btnEdit);
        submitDetails = (Button) findViewById(R.id.btnSubmit);

        nameValue = (EditText) findViewById(R.id.nameV);
        emailValue = (EditText) findViewById(R.id.emailV);
        phoneValue = (EditText) findViewById(R.id.phoneV);

        nameText = (TextView) findViewById(R.id.nameText);
        emailText = (TextView) findViewById(R.id.emailText);
        phoneText = (TextView) findViewById(R.id.phoneText);

        if (userName.equals(u.getUsername())) {
            editDetails.setVisibility(View.VISIBLE);

            nameValue.setText(String.valueOf(u.getString("ActualName")));
            emailValue.setText(String.valueOf(u.getEmail()));
            phoneValue.setText(String.valueOf(u.getString("PhoneNumber")));

            editDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    submitDetails.setVisibility((View.VISIBLE));
                    nameValue.setVisibility(View.VISIBLE);
                    emailValue.setVisibility(View.VISIBLE);
                    phoneValue.setVisibility(View.VISIBLE);

                    nameText.setVisibility(View.GONE);
                    emailText.setVisibility(View.GONE);
                    phoneText.setVisibility(View.GONE);

                    imageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            selectImage();
                            return false;
                        }
                    });
                }
            });

            submitDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    u.put("ActualName", nameValue.getText().toString());
                    u.setEmail(emailValue.getText().toString());
                    u.put("PhoneNumber", phoneValue.getText().toString());
                    if (parseFile != null) {
                        u.put("image", parseFile);
                    }
                    u.saveEventually();

                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra("MESSAGE", userName);
                    startActivity(intent);
                }
            });
        }



        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                try {
                    if (e == null) {
                        rateThisUser = objects.get(0);

                        String existing_rating = rateThisUser.getString("Rating");
                        ParseFile image = rateThisUser.getParseFile("image");
                        imageView.setParseFile(image);
                        imageView.loadInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                // The image is loaded and displayed!
                            }
                        });

                        int num_shared = 0;

                        Number d = rateThisUser.getNumber("BooksNum");
                        if (d != null) {
                            num_shared = (int) d;
                        }
                        int max = 0;
                        query.orderByDescending("BooksNum");

                        float newRating = (float) 0.0;
                        ParseUser u = query.find().get(0);
                        Number z =  u.getNumber("BooksNum");
                        if (z != null) {
                            max = (int) z;
                        }
                        if (max != (float) 0.0) {
                            newRating = (5) * (num_shared / max);
                        }
                        newRating = (float) (newRating * 0.6);
                        emailText.setText(String.valueOf(rateThisUser.getEmail()));
                        nameText.setText(String.valueOf(rateThisUser.getString("ActualName")));
                        phoneText.setText(String.valueOf(rateThisUser.getString("PhoneNumber")));
 					b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel: " + rateThisUser.getString("PhoneNumber")));
                                startActivity(callIntent);
                            }
                        });

                        sendEmail.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{rateThisUser.getEmail()});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Request");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "I want to share this book");
                                emailIntent.setType("application/octet-stream");
                                startActivity(emailIntent);
                            }
                        });

                        if (existing_rating != null) {
                            ratingBar.setRating(Float.parseFloat(existing_rating));
                        } else {
                            existing_rating = "0.0";
                            ratingBar.setRating(Float.parseFloat(Float.toString(newRating)));
                        }

                        final float finalNewRating = newRating;
                        final String finalExisting_rating = existing_rating;
                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                float rate = (float) ((((Float.parseFloat(finalExisting_rating) + rating) / 2) * 0.5) + finalNewRating);
                                ratingBar.setRating(rating);
                                rateThisUser.put("Rating", String.valueOf(rate));
                                rateThisUser.saveEventually();
                            }
                        });
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };

        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public void toggleMenu(View v){
        this.root.toggleMenu();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
           if (requestCode == 2) {
                selectedImagePath = getAbsolutePath(data.getData());
                bm = decodeFile(selectedImagePath);
                if(bm != null) {
                    imageView.setImageBitmap(bm);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    byte[] imageData = bytes.toByteArray();
                    parseFile = new ParseFile("image.jpg", imageData);
                    try {
                        parseFile.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
           }
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
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

    public void logOut(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Logout")){
            ParseUser.logOut();
            Intent intent = new Intent(this, Main2Activity.class);
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
    public void wishBook(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Wish Book")){
            Intent intent = new Intent(this, wish.class);
            startActivity(intent);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
