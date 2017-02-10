package com.sqlite.sqliteapp;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sqlite.sqliteapp.Views.MenuFly;
//import com.sqlite.sqliteapp.Views.wish;

import java.util.ArrayList;
import java.util.List;

public class find_books extends ListActivity {
    MenuFly root;
    SearchView search;
    ArrayAdapter<String> adapter;
    ArrayList<String> objectid = new ArrayList<String>();

    ArrayList<String> names = new ArrayList<String>();
    boolean searchByAutor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(root);
        this.root = (MenuFly) this.getLayoutInflater().inflate(R.layout.activity_find_books, null);
        setContentView(root);

        TextView tx = (TextView) findViewById(R.id.Title);
        Typeface cd = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
        tx.setTypeface(cd);

        search = (SearchView)findViewById(R.id.searchView);
        search.setIconifiedByDefault(false);
        search.setQueryHint("Search For Book Here...");


        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UploadBooks");
                //final ParseQuery<ParseObject> query3 = ParseQuery.getQuery("UploadBooks");

                if(searchByAutor == true){
                    Log.d("Author", " seach by author is " + searchByAutor );
                    query2.whereContains("AuthorToLowerCase", search.getQuery().toString().toLowerCase());
                }else if (searchByAutor == false){
                    Log.d("Author", " seach by author is " + searchByAutor );
                    query2.whereContains("TitleToLowerCase", search.getQuery().toString().toLowerCase());
                }else{
                    Log.d("Author", " seach by author is else case " + searchByAutor );
                    query2.whereContains("AuthorToLowerCase", search.getQuery().toString().toLowerCase());
                }

                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            int i = 0;
                            names.clear();
                            objectid.clear();
                            for (ParseObject nameObject : objects) {
                                String name = nameObject.get("Title").toString();
                                String author = nameObject.get("Author").toString();
                                String disp = name + " By " + author;
                                Log.d("Title", name);
                                names.add(i, disp);
                                objectid.add(nameObject.getObjectId());
                                Log.d("Test   ", nameObject.getObjectId());
                                i++;
                            }

                            adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, names);
                            getListView().setAdapter(adapter);
                            getListView().setOnItemClickListener(
                                    new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(parent.getContext(), BookDetails.class);
                                            Log.d("adapter", objectid.get(position));
                                            intent.putExtra("oid", objectid.get(position));
                                            startActivityForResult(intent, 0);

                                        }
                                    }
                            );

                        } else {
                            Log.d("Author", "Error: " + e.getMessage());
                        }

                    }
                });

                names.clear();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //textView3.setText("no text");
                //textView3.getText().length();
                return false;
            }


        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.seachByAuthor:
                if (checked)
                    Log.d("Title", "Searching by author");
                    searchByAutor = true;
                    names.clear();
                    adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, names);
                    getListView().setAdapter(adapter);
                break;
            case R.id.searchByTitle:
                if (checked)
                    Log.d("Title", "Searching by title");
                    searchByAutor = false;
                    names.clear();
                    adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, names);
                    getListView().setAdapter(adapter);
                break;
        }
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
    public void logOut(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("Logout")){
            ParseUser.logOut();
            Intent intent = new Intent(this, Main2Activity.class);
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
    public void myBooks(View view){
        String button_text;
        button_text = ((Button) view).getText().toString();
        if(button_text.equals("My Books")){
            Intent intent = new Intent(this, my_book.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_books, menu);
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
