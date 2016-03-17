package com.yackeen.newsapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mohamed Yasser on 3/10/2016.
 */
public class NewsProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataBaseHandler mOpenHelper;

    private final static String LOG_TAG = NewsProvider.class.getSimpleName();
    final static int NEWS_DIR = 100;
    final static int NEWS_ITEM = 101;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DataBaseHandler(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher(){
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher  uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = NewsContract.CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // NewsContract to help define the types to the UriMatcher.
        uriMatcher.addURI(authority, NewsContract.PATH_NEWS_TABLE, NEWS_DIR);
        uriMatcher.addURI(authority, NewsContract.PATH_NEWS_TABLE + "/*", NEWS_ITEM);

        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case NEWS_DIR:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(NewsContract.NEWS_TABLE_ENTRY.TABLE_NAME, projection, selection
                                , selectionArgs, null, null, sortOrder);
                break;

            case NEWS_ITEM:
                retCursor = getSelectedItem(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NEWS_DIR:
                //All or multiple records are required
                return NewsContract.NEWS_TABLE_ENTRY.CONTENT_TYPE;

            case NEWS_ITEM:
                //only one record is required
                return NewsContract.NEWS_TABLE_ENTRY.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case NEWS_DIR:
                long _id = db.insert(NewsContract.NEWS_TABLE_ENTRY.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = NewsContract.NEWS_TABLE_ENTRY.addNewRecord(_id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowUpdated=0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();


        switch(sUriMatcher.match(uri)) {
            case NEWS_DIR:
                rowUpdated = db.update(NewsContract.NEWS_TABLE_ENTRY.TABLE_NAME,values,
                        selection,selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported Uri"+uri);
        }

        if(rowUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case NEWS_DIR:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
//                        normalizeDate(value);
                        long _id = db.insert(NewsContract.NEWS_TABLE_ENTRY.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getSelectedItem(Uri uri,String[] projection,String sortOrder){
        String iurl = uri.getLastPathSegment();
        String selection = NewsContract.NEWS_TABLE_ENTRY.IURL + " =?";
        String[] selectionArgs = new String[] {iurl};
        return getContext().getContentResolver().query(NewsContract.NEWS_TABLE_ENTRY.CONTENT_URI
                ,projection,selection,selectionArgs,sortOrder);
    }
}
