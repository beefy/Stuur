package com.stuur.stuur;

/**
 * Created by Nathaniel on 2/28/2016.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] value;
    private final String[] description;

    public CustomList(Activity context, String[] value, String[] description) {
        super(context, R.layout.list_single, value);
        this.context = context;
        this.value = value;
        this.description = description;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtValue = (TextView) rowView.findViewById(R.id.val);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.desc);

        txtValue.setText(value[position]);
        txtDesc.setText(description[position]);

        return rowView;
    }
}
