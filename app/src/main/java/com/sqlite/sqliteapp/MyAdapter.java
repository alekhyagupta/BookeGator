package com.sqlite.sqliteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Planet> {

    private LayoutInflater inflater;
    ViewHolder viewHolder;

    public MyAdapter(Context context, ArrayList<Planet> planetList) {
        super(context, R.layout.simplerow, R.id.rowTextView, planetList);
        inflater = LayoutInflater.from(context);
    }

  /*  public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }*/

   /* @Override
    public int getViewTypeCount() {
        return 1;
    }*/

    public View getView(final int position, View convertView, ViewGroup parent) {
        Planet planet = (Planet) this.getItem(position);
        CheckBox checkBox;
        TextView name;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.my_custom_list_layout, null);

            name=(TextView) convertView.findViewById(R.id.list_item_string);
            checkBox=(CheckBox) convertView.findViewById(R.id.CheckBox01);
            convertView.setTag(new ViewHolder(name, checkBox));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Planet planet = (Planet) cb.getTag();
                    planet.setChecked(cb.isChecked());
                }
            });
        } else {

            ViewHolder viewHolder = (ViewHolder) convertView
                    .getTag();
            checkBox = viewHolder.getCheckBox();
            name = viewHolder.getTextView();
        }

        checkBox.setTag(planet);
        checkBox.setChecked(planet.isChecked());
        name.setText(planet.getName());
        return convertView;
    }
}