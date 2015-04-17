package com.boorce.clientscoiffmanager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TravauxAdapter extends ArrayAdapter {
    private final Activity activity;
    private final List list;

    public TravauxAdapter(Activity activity, List<Travaux> list) {
        super(activity,R.layout.idandtextlayout, list);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.idandtextlayout, null);

            // Hold the view objects in an object, that way the don't need to be "re-  finded"
            view = new ViewHolder();
            view.uid=(TextView) rowView.findViewById(R.id.uidText);
            view.travail= (TextView) rowView.findViewById(R.id.textText);

            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }

        /** Set data to your Views. */
        Travaux item = (Travaux) list.get(position);
        view.uid.setText(String.valueOf(item.getId()));
        view.travail.setText(item.getDescription());

        return rowView;
    }

    protected static class ViewHolder{
        protected TextView uid;
        protected TextView travail;
    }


}
