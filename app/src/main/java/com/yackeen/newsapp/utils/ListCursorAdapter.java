package com.yackeen.newsapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yackeen.newsapp.MainActivity;
import com.yackeen.newsapp.R;

/**
 * Created by Mohamed Yasser on 3/5/2016.
 */
public class ListCursorAdapter extends CursorAdapter {
    //tabIndex is used to choose the proper layout to inflate (list_item or grid_item)
    int width,height;
    DisplayMetrics displayMetrics;


    public ListCursorAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int layoutId ;
        MainActivity activity = (MainActivity)context;
        int index = activity.tabLayout.getSelectedTabPosition();

        if(index ==0){
            layoutId = R.layout.list_item;
        }else{
            layoutId = R.layout.grid_item;

        }
        View view = LayoutInflater.from(context).inflate(layoutId,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    public static class ViewHolder{
        ImageView news_Image;
        TextView news_title,news_published_date;

        public ViewHolder(View view){
            news_Image = (ImageView) view.findViewById(R.id.news_image);
            news_title = (TextView) view.findViewById(R.id.news_title);
            news_published_date = (TextView) view.findViewById(R.id.news_date);
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

//        displayMetrics = context.getResources().getDisplayMetrics();
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            if(dpWidth<600) {
//                width = 200;
//                height = 200;
//            }else if(dpWidth>=600 && dpWidth<720){
//                width = 400;
//                height = 400;
//            }else{
//                width = 800;
//                height = 800;
//            }
//
//        }else{
//            if(dpWidth<600) {
//                width = 300;
//                height = 300;
//            }else if(dpWidth>=600 && dpWidth<720){
//                width = 600;
//                height = 600;
//            }else{
//                width = 1300;
//                height = 1300;
//            }
//        }



        Glide.with(context)
                .load(cursor.getString(4))
                .fitCenter()
                .centerCrop()
                .into(viewHolder.news_Image);

        viewHolder.news_title.setText(cursor.getString(2));
        viewHolder.news_published_date.setText(cursor.getString(3));

    }

}
