package com.yackeen.newsapp;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.yackeen.newsapp.data.NewsContract;
import com.yackeen.newsapp.utils.ListCursorAdapter;
import com.yackeen.newsapp.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    public Spinner spinner;
    public TabLayout tabLayout;
    int spinner_selection;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout.Tab tab;

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setOnTabSelectedListener(this);


        tabLayout.addTab(tabLayout.newTab().setText(R.string.list_view_tab), 0, false);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.grid_view_tab), 1, false);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        spinner = (Spinner) findViewById(R.id.header_chooser);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.headers_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tag;
                Bundle bundle;
                bundle = new Bundle();
                bundle.putString("spinner_item", (String) adapterView.getSelectedItem());
                if(getSupportFragmentManager().findFragmentByTag("list_view")!=null) {
                    ListViewFragment listViewFragment = new ListViewFragment();
                    listViewFragment.setArguments(bundle);
                    tag = "list_view";
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            listViewFragment, tag)
                            .commit();

                }else {
                    GridViewFragment gridViewFragment = new GridViewFragment();
                    gridViewFragment.setArguments(bundle);
                    tag = "grid_view";

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            gridViewFragment, tag)
                            .commit();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        if(savedInstanceState!=null){
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("list_view");
            if(fragment!=null){
                tab = tabLayout.getTabAt(0);
                if(tab!=null) {
                    tab.select();
                }
            }else{
                tab = tabLayout.getTabAt(1);
                if(tab!=null) {
                    tab.select();
                }
            }

        }else{
            TabLayout.Tab tab1 =  tabLayout.getTabAt(0);
            if(tab1!=null){
                tab1.select();
            }
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.v(LOG_TAG,"onTabSelected is called");

        int index = tab.getPosition();
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag,spinner_item = "";
        Bundle bundle=null;

        if(spinner!=null) {
            spinner_item = (String) spinner.getSelectedItem();
        }

        if(!spinner_item.isEmpty()){
            bundle = new Bundle();
            bundle.putString("spinner_item",spinner_item);
        }
        switch (index){
            case 0:
                tag = "list_view";
                ListViewFragment listViewFragment = new ListViewFragment();
                if(bundle!=null){
                    listViewFragment.setArguments(bundle);
                }

                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        listViewFragment, tag)
                        .commit();
                break;

            case 1:
                tag = "grid_view";
                GridViewFragment gridViewFragment = new GridViewFragment();
                if(bundle!=null){
                    gridViewFragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        gridViewFragment, tag)
                        .commit();
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
