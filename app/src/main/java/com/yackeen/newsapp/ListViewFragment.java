package com.yackeen.newsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.yackeen.newsapp.data.NewsContract;
import com.yackeen.newsapp.utils.GetData;
import com.yackeen.newsapp.utils.ListCursorAdapter;
import com.yackeen.newsapp.utils.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    ListCursorAdapter cursorAdapter;
    DatabaseChangedReceiver receiver;
    private final String LOG_TAG = ListViewFragment.class.getSimpleName();
    private final int LIST_LOADER =1;
    private String spinner_selection;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        cursorAdapter = new ListCursorAdapter(getActivity(),null,0);
        listView.setAdapter(cursorAdapter);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()!=null){
            spinner_selection = getArguments().getString("spinner_item");
            if(!spinner_selection.isEmpty()){
                getLoaderManager().initLoader(LIST_LOADER,null,this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), NewsContract.NEWS_TABLE_ENTRY.CONTENT_URI,
                null, NewsContract.NEWS_TABLE_ENTRY.SECTION + "=?",new String[]{spinner_selection}
                ,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "onLoadFinished is called!!");
        if(!cursor.moveToFirst()){
            if(Utility.isNetworkAvailable(getActivity())){
                progressBar.setVisibility(View.VISIBLE);
                new GetData(getActivity()).execute(spinner_selection);
            }else{
                Toast.makeText(getActivity(), "Check your internet connection!!"
                        , Toast.LENGTH_LONG).show();
            }
        }else{
            if(progressBar.getVisibility() == View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
            cursorAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    @Override
    public void onResume() {
        super.onResume();
        receiver = new DatabaseChangedReceiver();
        IntentFilter intentFilter  = new IntentFilter("com.yackeen.newsapp.DATABASE_CHANGED");
        intentFilter.addAction("error_happened");
        getActivity().registerReceiver(receiver,intentFilter);
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }


    private class DatabaseChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.yackeen.newsapp.DATABASE_CHANGED")) {
                getLoaderManager().restartLoader(LIST_LOADER, null, ListViewFragment.this);
            }else{
                if(progressBar.getVisibility()==View.VISIBLE)
                progressBar.setVisibility(View.GONE);
            }
        }

    }


    public class SpinnerListener implements Spinner.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spinner_selection = (String) adapterView.getItemAtPosition(i);
            getLoaderManager().initLoader(LIST_LOADER,null,ListViewFragment.this);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

}
