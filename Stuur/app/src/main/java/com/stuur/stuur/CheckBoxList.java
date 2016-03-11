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

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.check_box_single, null, true);
        TextView txtValue = (TextView) rowView.findViewById(R.id.settings_val);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        checkBox.setChecked(checked[position]);

        txtValue.setText(value[position]);

        return rowView;
    }
}
