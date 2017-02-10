package com.sqlite.sqliteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sqlite.sqliteapp.Views.MenuFly;

import java.util.ArrayList;
import java.util.List;

public class BookDetails extends AppCompatActivity {
    MenuFly root;

    TextView textTitle, textAuthor, textEdition, textYear, textDeposit, textISBN;
    Button contactOwner, issueRequestButton, wishlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (MenuFly) this.getLayoutInflater().inflate(R.layout.activity_book_details, null);
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


        textTitle = (TextView)findViewById(R.id.title);
        textAuthor = (TextView)findViewById(R.id.author);
        textEdition = (TextView)findViewById(R.id.edition);
        textYear = (TextView)findViewById(R.id.year);
        contactOwner = (Button)findViewById(R.id.contactOwner);
        textISBN = (TextView)findViewById(R.id.isbn);
        textDeposit = (TextView)findViewById(R.id.deposit);
        issueRequestButton = (Button)findViewById(R.id.issuerequest);
        wishlist = (Button)findViewById(R.id.wishlist);
        final String objectId = getIntent().getExtras().getString("oid");
        TextView tx = (TextView) findViewById(R.id.Title);
        Typeface cd = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
        tx.setTypeface(cd);
        viewAll(objectId);

        final ParseImageView imageView = (ParseImageView) findViewById(R.id.icon);

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UploadBooks");
        query2.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseFile image = object.getParseFile("image");
                    imageView.setParseFile(image);
                    if (object.getBoolean("Issued")) {
                        issueRequestButton.setVisibility(View.GONE);
                    }
                    if((object.getParseObject("Owner1").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))){
                        issueRequestButton.setVisibility(View.GONE);
                        wishlist.setVisibility(View.GONE);
                    }
                    imageView.loadInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            // The image is loaded and displayed!
                        }
                    });
                }
            }
        });

        final Button contactOwner = (Button)findViewById(R.id.contactOwner);
        contactOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                contactOwner(objectId);
            }
        });
        final Button wishlist = (Button)findViewById(R.id.wishlist);
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                wishlist(objectId);
            }
        });

        issueRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                issueRequest(objectId);
            }
        });

    }

    private void create(ParseUser u){
        Intent intent = new Intent(this, ViewActivity.class);
        try {
            intent.putExtra("MESSAGE", u.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private void contactOwner(String objectId) {
        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UploadBooks");
        query2.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseUser u = object.getParseUser("Owner1");
                    create(u);
                }
            }
        });
    }

    public void viewAll(final String oid) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UploadBooks");
        query.getInBackground(oid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    textTitle.setText(object.getString("Title"));
                    //textTitle.setEnabled(false);
                    textAuthor.setText("by " + object.getString("Author"));
                    // textAuthor.setEnabled(false);
                    if (object.getString("Edition") != null) {
                        textEdition.setText("Edition - " + object.getString("Edition"));
                    }
                    if (object.getString("Year_Of_Publication") != null) {
                        textYear.setText("Year of Publication - " + object.getString("Year_Of_Publication"));
                    }
                    if (object.getString("ISBN") != null) {
                        textISBN.setText("ISBN - " + object.getString("ISBN"));
                    }
                    if (object.getString("Deposit_Amount") != null) {
                        textDeposit.setText("Deposit Amount - " + object.getString("Deposit_Amount"));
                    }

                } else {
                    textTitle.setText(e.toString());

                }
                if (object.getBoolean("Issued")) {
                    issueRequestButton.setClickable(false);
                }
            }
        });

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


  /*  public void issueRequest(final String oid){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UploadBooks");
        Log.d("tag", "inside");
        query.getInBackground(oid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseObject issuedBooks = new ParseObject("IssuedBooks");
                    issuedBooks.put("UserObject", ParseUser.getCurrentUser());
                    issuedBooks.put("BookObject", object);
                    try {
                        issuedBooks.save();
                        Toast.makeText(getApplicationContext(), "Issued.",
                                Toast.LENGTH_LONG).show();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }*/
    public void issueRequest(final String oid){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UploadBooks");
        Log.d("tag", "inside");
        query.getInBackground(oid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // ParseObject issuedBooks = new ParseObject("IssuedBooks");
                    //object.put("Issued_By", ParseUser.getCurrentUser());

                    ArrayList<ParseUser> issuers = (ArrayList<ParseUser>) object.get("Issued_Request_List");

//                    String s = object.getString("Issued_By");
                    if (issuers==null || issuers.isEmpty()) {
                        issuers = new ArrayList<ParseUser>();
                        issuers.add(ParseUser.getCurrentUser());
                        object.put("Issued_Request_List", issuers);
                    }else{
                        issuers.add(ParseUser.getCurrentUser());
                        object.put("Issued_Request_List", issuers);
                    }
                   // object.put("Issued", true);
                    try {
                        object.save();
                        Toast.makeText(getApplicationContext(), "Issued",
                                Toast.LENGTH_LONG).show();
                        ParsePush parsePush = new ParsePush();
                        ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query
                        pQuery.whereEqualTo("channels", object.getParseObject("Owner1").getObjectId()); // <-- you'll probably want to target someone that's not the current user, so modify accordingly
                        parsePush.sendMessageInBackground(ParseUser.getCurrentUser().get("ActualName") + " has requested a book. Tap to see...", pQuery);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    public void wishlist(final String oid){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UploadBooks");
        Log.d("tag", "inside");
        query.getInBackground(oid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //  ParseObject wishedbooks = new ParseObject("wishedbooks");
                    object.put("wished_by", ParseUser.getCurrentUser());
                    // wishedbooks.put("BookObject", object);
                    //  wishedbooks.put("Title", object.get("Title"));
                    try {
                        object.save();
                        Toast.makeText(getApplicationContext(), "Added to wishlist!",
                                Toast.LENGTH_LONG).show();
                     /*   ParsePush parsePush = new ParsePush();
                        ParseQuery query = new ParseQuery("User");
                        query.whereEqualTo("username", object.getParseUser("Owner1").getUsername());
                        pQuery.whereMatchesQuery("", query);
                        parsePush.sendMessageInBackground("Only for special people", pQuery);*/
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
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

    public void myBooks(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("My Books")){
            Intent intent = new Intent(this, my_book.class);
            startActivity(intent);
        }
    }
    public void myIssueRequests(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("My Issue Requests")){
            Intent intent = new Intent(this, IssueRequest.class);
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

    public void wishBook(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Wish Book")){
            Intent intent = new Intent(this, wish.class);
            startActivity(intent);
        }
    }

    public void logOut(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Logout")){
            ParseUser.logOut();
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);
            /*ParseUser.logOut();
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
