package com.sqlite.sqliteapp;

import android.app.ListActivity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sqlite.sqliteapp.Views.MenuFly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IssueRequest extends ListActivity {
    MenuFly root;
    ArrayList<String> objectid = new ArrayList<String>();
    ArrayAdapter<String> adapter ;
    private Planet[] planets = null;
    // TextView bookTitle;
    //MyAdapter listAdapter;
    //  private ListView mainListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (MenuFly) this.getLayoutInflater().inflate(R.layout.activity_issue_request, null);
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

        // final TextView bookTitle = (TextView)findViewById(R.id.bookTitle);
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleIssue);
        toggle.setTextOff("Available");

        toggle.setTextOn("Issued");
        TextView tx = (TextView) findViewById(R.id.Title);
        Typeface cd = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
        tx.setTypeface(cd);
        final String objectId = getIntent().getExtras().getString("oid");
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("UploadBooks");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ParseObject object = null;
        try {
            object = query.get(objectId);
            ArrayList<ParseUser> issuers = (ArrayList<ParseUser>) object.get("Issued_Request_List");
        //    Log.d("ParseUser", issuers.get(0).getObjectId());
            final ParseObject pobject = object;
            Boolean flag = pobject.getBoolean("Issued");

            toggle.setChecked(flag);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        pobject.put("Issued", true);
                    } else {
                        ArrayList<ParseUser> p1 = new ArrayList<ParseUser>();
                        pobject.put("Issued", false);
                        pobject.put("Issued_Request_List", p1);
                        ParseUser pu = pobject.getParseUser("wished_by");
                        // Log.d("Parse User ", pu.getObjectId());
                        if (pu != null) {
                            Log.d("PARSE USER : ", pu.getObjectId());
                            ParsePush parsePush = new ParsePush();
                            ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query
                            pQuery.whereEqualTo("channels", pu.getObjectId());
                          //  String name =  pu.getString("ActualName");
                            // <-- you'll probably want to target someone that's not the current user, so modify accordingly
                            parsePush.sendMessageInBackground("Hey, a book from your wishlist is now available.", pQuery);
                        }
                    }
                    try {
                        pobject.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            if(issuers != null) {
                for (int i = 0; i < issuers.size(); i++) {
                    String userId = issuers.get(i).getObjectId();
                    final ParseQuery<ParseObject> query1 = ParseQuery.getQuery("_User");

                    query1.getInBackground(userId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            String name = object.getString("ActualName");
                            adapter.add(name);
                        }
                    });
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //, new GetCallback<ParseObject>() {
        //@Override
      //  public void done(ParseObject object, ParseException e) {
            // bookTitle.setText(object.getString("Title"));


        //});
        getListView().setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_issue_request, menu);
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
