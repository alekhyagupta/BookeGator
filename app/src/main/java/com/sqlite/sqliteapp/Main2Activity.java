package com.sqlite.sqliteapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.SocketFactory;

import java.security.GeneralSecurityException;
import java.util.List;


public class Main2Activity extends AppCompatActivity {
    LDAPConnection ldapConnection = null;
    ParseUser cuser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        TextView tx = (TextView)findViewById(R.id.Title);
        Typeface cd = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
        tx.setTypeface(cd);
        Button login = (Button)findViewById(R.id.login);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText test = (EditText)findViewById(R.id.test);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                username.setText("");
                password.setText("");
            }
        }, intentFilter);

         login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            try {
                                final SocketFactory socketFactory;
                                final SSLUtil sslUtil = new SSLUtil(null,new TrustAllTrustManager());
                                socketFactory = sslUtil.createSSLSocketFactory();
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                ldapConnection = new LDAPConnection(socketFactory,"128.227.9.22", 636);
                                final String name = username.getText().toString();
                                final String name2 = password.getText().toString();
                                BindRequest bindRequest = new SimpleBindRequest("uid=" + name, name2);

                                BindResult bindResult = ldapConnection.bind(bindRequest);
                               if (bindResult.getResultCode() == ResultCode.SUCCESS) {
                                   ParseUser p = ParseUser.getCurrentUser();
                                   if ( p!=null)
                                   p.logOut();

                                   ParseQuery<ParseUser> query = ParseUser.getQuery();
                                   query.whereEqualTo("username", name);
                                   List<ParseUser> objects = query.find();
                                   if(objects != null && objects.size() != 0)
                                       cuser = objects.get(0);

                                   if (cuser == null) {
                                       Intent intent = new Intent(v.getContext(), MainActivity.class);
                                       intent.putExtra("ufid", name);
                                       startActivityForResult(intent, 0);
                                   } else {
                                       ParseUser.logInInBackground(name + "",
                                               "213" + "", new LogInCallback() {
                                                   @Override
                                                   public void done(ParseUser user, ParseException e) {
                                                   }
                                               });
                                       Intent intent = new Intent(v.getContext(), find_books.class);
                                       startActivityForResult(intent, 0);
                                   }





/*
                                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                                    intent.putExtra("ufid", name);
                                    startActivityForResult(intent, 0);*/
                                    ldapConnection.close();
                                    // }
                                }


                            }
                            catch (LDAPException e) {
                                test.setText("Incorrect Username/Password.\nPlease try again!");
                                test.setEnabled(false);
                                //test.append(e.toString());
                                if(ldapConnection != null)
                                ldapConnection.close();

                            }
                            catch(GeneralSecurityException exception) {
                                //test.setText(exception.toString());
                                ldapConnection.close();
                            }catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }

                    }

        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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



