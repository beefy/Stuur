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

    private class ViewHolder {
        TextView txtValue;
        TextView txtDesc;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if(view == null) {
            view = inflater.inflate(R.layout.list_single, parent, false);
            holder = new ViewHolder();
            holder.txtValue = (TextView)view.findViewById(R.id.val);
            holder.txtDesc = (TextView)view.findViewById(R.id.desc);
        } else
            holder = (ViewHolder)view.getTag();

        view.setTag(holder);
        holder.txtValue.setText(value[position]);
        holder.txtDesc.setText(description[position]);
        return view;
    }
}
