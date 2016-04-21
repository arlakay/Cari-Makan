package com.carimakan.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.carimakan.R;
import com.carimakan.app.MyApplication;
import com.carimakan.util.FontCache;

import java.util.List;

public class SwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Restaurant> restaurantList;
    private Context context;
    private Typeface fontLemonMilk, fontHabibi;

    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public SwipeListAdapter(Activity activity, List<Restaurant> restaurantList) {
        this.activity = activity;
        this.restaurantList = restaurantList;
        context       = activity.getApplicationContext();
        fontLemonMilk = FontCache.get(this.context, "LemonMilk");
        fontHabibi    = FontCache.get(this.context, "Habibi-Regular");
    }

    @Override
    public int getCount() {
        return restaurantList.size();
    }

    @Override
    public Object getItem(int location) {
        return restaurantList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_restaurant, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

        NetworkImageView pic = (NetworkImageView) convertView.findViewById(R.id.photo);
        TextView title = (TextView) convertView.findViewById(R.id.name);
        TextView alamat = (TextView) convertView.findViewById(R.id.itemDescription);

        title.setTypeface(fontLemonMilk);
        alamat.setTypeface(fontHabibi);

        pic.setImageUrl(restaurantList.get(position).urlpic, imageLoader);
        title.setText(restaurantList.get(position).title);
        alamat.setText(restaurantList.get(position).alamat);

        return convertView;
    }

}