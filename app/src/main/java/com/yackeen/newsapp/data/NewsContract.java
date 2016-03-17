package com.yackeen.newsapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mohamed Yasser on 3/10/2016.
 */
public class NewsContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.yackeen.newsapp";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //possible path (uniquely is the path of the "news" table)
    public static final String PATH_NEWS_TABLE = "news";


    public static final class NEWS_TABLE_ENTRY implements BaseColumns {

        //news table name
        public static final String TABLE_NAME = "news";

        //"news" table columns' names
        public static final String SECTION = "section";//1
        public static final String TITLE = "title";//2
        public static final String PUBLISHED_DATE = "published_date";//3
        public static final String IURL = "iurl";//4

        // "news" table uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS_TABLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS_TABLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS_TABLE;

        public static Uri addNewRecord(long rowId) {
            return ContentUris.withAppendedId(CONTENT_URI, rowId);
        }

    }
}
