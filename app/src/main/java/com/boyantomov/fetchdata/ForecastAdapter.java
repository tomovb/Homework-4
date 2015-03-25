package com.boyantomov.fetchdata;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by admin on 16.3.2015 г..
 */
public class ForecastAdapter extends BaseAdapter {

    ArrayList<TopicList> myList = new ArrayList<TopicList>();
    LayoutInflater inflater;
    Context context;

    public ForecastAdapter(Context context, ArrayList<TopicList> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public TopicList getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder myViewHolder;
        int type = getItemViewType(position);

        if (convertView == null) {
            if (type == 0) {
                convertView = inflater.inflate(R.layout.layout_list_item, null);
            } else {
                convertView = inflater.inflate(R.layout.layout_list_item_odd, null);
            }

            myViewHolder = new MyViewHolder();
            convertView.setTag(myViewHolder);

        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();

        }

        myViewHolder.tvMultiplication = detail(convertView, R.id.tvMultiplication, myList.get(position).getTopicTitle());
        myViewHolder.tvResult = detail(convertView, R.id.tvResult, myList.get(position).getUser().getUserName());
        myViewHolder.ivAvatar = avatarDetail(convertView, R.id.ivDrawable, myList.get(position).getUser().getAvatarTemplate());


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private TextView detail(View v, int resID, String text) {
        TextView tv = (TextView) v.findViewById(resID);
        tv.setText(text);
        return tv;
    }

    private ImageView avatarDetail (View v, int resID, String text){
        text = "http://frm.hackafe.org" + text;
        String fullAvatarURL = text.replace("{size}", "30x30");
        ImageView iv = (ImageView) v.findViewById(resID);
        try {
            URL avatar_u = new URL(fullAvatarURL);
            Drawable avatar_d = Drawable.createFromStream(avatar_u.openStream(), "src");
            iv.setImageDrawable(avatar_d);
            return iv;
        }
        catch (Throwable t) {
            Log.e("Sunshine", t.getMessage(), t);
            return null;
        }
    }

    private class MyViewHolder {
        TextView tvMultiplication, tvResult;
        ImageView ivAvatar;
    }
}
