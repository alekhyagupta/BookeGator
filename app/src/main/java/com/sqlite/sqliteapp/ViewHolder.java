package com.sqlite.sqliteapp;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Pratyoush on 08-12-2015.
 */
public class ViewHolder {
    TextView name;
    CheckBox checkBox;

    ViewHolder(TextView name, CheckBox check){
        this.name = name;
        this.checkBox = check;
    }
    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTextView() {
        return name;
    }

    public void setTextView(TextView textView) {
        this.name = textView;
    }
}
