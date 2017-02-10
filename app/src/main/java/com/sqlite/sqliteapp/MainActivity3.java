package com.sqlite.sqliteapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity3 extends AppCompatActivity {
    TextView textMessage;
    Button buttonlogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        String message = getIntent().getExtras().getString("message");
        Typeface wb = Typeface.createFromAsset(getAssets(), "fonts/Walkway_Bold.ttf");
        textMessage =(TextView) findViewById((R.id.message));
        textMessage.setText("Thank you " + message + " for entering your details!");
        textMessage.setTypeface(wb);
        //textMessage.setEnabled(false);
        buttonlogout = (Button)findViewById(R.id.logout);

        buttonlogout.setOnClickListener(
                new View.OnClickListener(){

                    public void onClick(View v){
                        Intent intent = new Intent(v.getContext(), Main2Activity.class);
                        startActivityForResult(intent, 0);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity3, menu);
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
