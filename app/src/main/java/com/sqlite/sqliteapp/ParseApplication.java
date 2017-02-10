package com.sqlite.sqliteapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "cSMb5B1Yob7iSIyMv8KaFn3odTgAQdBwWx9mNcWD";
    public static final String YOUR_CLIENT_KEY = "0dWn9WVTrFj5laRyvxboYSftoCByWnWw22QLaq06";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
    //    ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
