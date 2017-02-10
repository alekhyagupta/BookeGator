package com.sqlite.sqliteapp;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sqlite.sqliteapp.Views.MenuFly;

import java.util.ArrayList;
import java.util.List;

public class wish extends ListActivity {
    MenuFly root;
    ArrayList<String> objectid = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.root = (MenuFly) this.getLayoutInflater().inflate(R.layout.activity_wish, null);
        setContentView(root);

        TextView tx = (TextView) findViewById(R.id.Title);
        Typeface cd = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
        tx.setTypeface(cd);

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UploadBooks");
        ParseUser u = ParseUser.getCurrentUser();
        query2.whereEqualTo("wished_by", u );
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ArrayList<String> names = new ArrayList<String>();
                    int i = 0;
                    for (ParseObject nameObject : objects) {

                        String name = nameObject.get("Title").toString();
                        // Log.d("Title", name);
                        names.add(i, name);
                        objectid.add(nameObject.getObjectId());

                        i++;
                    }
                    adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, names);
                    getListView().setAdapter(adapter);
                    /*getListView().setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(parent.getContext(), BookDetails.class);
                                    intent.putExtra("oid", objectid.get(position));
                                    startActivityForResult(intent, 0);

                                }
                            }
                    );
*/

                } else {
                    // Log.d("Author", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void toggleMenu(View v){
        this.root.toggleMenu();
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
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wish, menu);
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