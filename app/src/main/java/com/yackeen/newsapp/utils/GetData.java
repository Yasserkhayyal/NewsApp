package com.yackeen.newsapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yackeen.newsapp.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Mohamed Yasser on 3/16/2016.
 */
public class GetData extends AsyncTask<String,Void,String>{
    Context mContext;
    private final String LOG_TAG = GetData.class.getSimpleName();

    public GetData(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String resultString = null;
        String selection = strings[0];
        try {
            HttpURLConnection urlConnection;
            BufferedReader reader;
            Uri builtUri;
            builtUri = Uri.parse("http://api.nytimes.com/svc/topstories/v1/"+
                    selection+".json?")
                    .buildUpon()
                    .appendQueryParameter("api-key", "012cd6cab57fb1b909bd4e03b6d533ad:4:74316630")
                    .build();

            URL url = null;
            url = new URL(builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                resultString = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                resultString = null;
            }
            resultString = buffer.toString();



        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            mContext.sendBroadcast(new Intent("error_happened"));
        }

        return resultString;
    }


    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
        ContentValues newsValues;
        JSONObject dataElement;
        JSONArray multimedia;
        String iurl = "", section = "", title = "";
        String published_date = "";


        if (jsonString != null && !jsonString.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                section = jsonObject.getString("section");
                JSONArray data = jsonObject.getJSONArray("results");
                Vector<ContentValues> cVVector = new Vector<>(data.length());
                for (int index = 0; index < data.length(); index++) {
                    dataElement = (JSONObject) data.get(index);
                    if (dataElement.has("multimedia")) {
                        try {
                            multimedia = dataElement.getJSONArray("multimedia");
                            for (int index1 = 0; index1 < multimedia.length(); index1++) {
                                JSONObject image_item = (JSONObject) multimedia.get(index1);
                                if (image_item.getString("format").equals("superJumbo")) {
                                    iurl = image_item.getString("url");// news_item image url
                                }
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    if (!iurl.isEmpty()) {
                        // to exclude news that don't have super jumbo poster image
                        title = dataElement.getString("title");
                        published_date = dataElement.getString("published_date").substring(0, 10);


                        newsValues = new ContentValues();
                        newsValues.put(NewsContract.NEWS_TABLE_ENTRY.SECTION, section);
                        newsValues.put(NewsContract.NEWS_TABLE_ENTRY.TITLE, title);
                        newsValues.put(NewsContract.NEWS_TABLE_ENTRY.PUBLISHED_DATE, published_date);
                        newsValues.put(NewsContract.NEWS_TABLE_ENTRY.IURL, iurl);

                        cVVector.add(newsValues);


                    }
                }

                int inserted;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(
                            NewsContract.NEWS_TABLE_ENTRY.CONTENT_URI,
                            cvArray);
                    Log.v(LOG_TAG,inserted+"");
                }

                if(data.length()!=0) {
                    mContext.sendBroadcast(new Intent("com.yackeen.newsapp.DATABASE_CHANGED"));
                }else{
                    Toast.makeText(mContext, "No news under this category",
                            Toast.LENGTH_SHORT).show();
                    mContext.sendBroadcast(new Intent("error_happened"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mContext.sendBroadcast(new Intent("error_happened"));
            }
        }

    }
}
