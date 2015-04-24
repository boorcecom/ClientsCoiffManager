package com.boorce.clientscoiffmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ThumbnailsAdapter extends BaseAdapter {
    private Context ctx;
    private List<Photo> photos;

    public ThumbnailsAdapter(Context ctx, List<Photo> photos ) {
        this.ctx=ctx;
        this.photos=photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =(LayoutInflater)
                ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View gridView;

        if(convertView == null)
        {
            gridView = inflater.inflate(R.layout.idandimagelayout, null);

            TextView textView = (TextView) gridView
                    .findViewById(R.id.photoUid);
            textView.setText(String.valueOf(photos.get(position).getUid()));
            ((TextView) gridView.findViewById(R.id.photoFile)).setText(photos.get(position).getFilename());

            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.photoThumbnail);

            imageView.setImageBitmap(photos.get(position).getBitmapThumbnail());
        } else {
            gridView =convertView;
        }

       return gridView;
    }

}
