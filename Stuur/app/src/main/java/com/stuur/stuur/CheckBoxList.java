package com.stuur.stuur;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Evan on 3/9/2016.
 */
public class CheckBoxList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] value;
    private final Boolean[] checked;

    public CheckBoxList(Activity context, String[] value, Boolean[] checked) {
        super(context, R.layout.check_box_single, value);
        this.context = context;
        this.value = value;
        this.checked = checked;
    }

    private class ViewHolder {
        TextView txtValue;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if(view == null) {
            view = inflater.inflate(R.layout.check_box_single, parent, false);
            holder = new ViewHolder();
            holder.txtValue = (TextView)view.findViewById(R.id.settings_val);
            holder.checkBox = (CheckBox)view.findViewById(R.id.checkbox);
        } else
            holder = (ViewHolder)view.getTag();

        view.setTag(holder);
        holder.txtValue.setText(value[position]);
        holder.checkBox.setChecked(checked[position]);
        return view;
    }
}
