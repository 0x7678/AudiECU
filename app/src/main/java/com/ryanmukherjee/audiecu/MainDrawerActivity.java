package com.ryanmukherjee.audiecu;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_ENABLE_BT = 1;
    private DrawerLayout mDrawer;
    // Map for storing fragments as the user switches between them
    private Map<Integer, Fragment> mFragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get the fragment that is initialized on startup and place it into our map
        mFragmentMap = new HashMap<>();
        mFragmentMap.put(R.id.nav_terminal, getSupportFragmentManager().findFragmentById(R.id.drawer_content));

        // Attempt to grab the bluetooth adapter, check if the device doesn't have bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        // If the bluetooth adapter is not enabled, then request that it be enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // bluetooth was enabled
                } else {
                    // bluetooth remains disabled
                    Toast newToast = Toast.makeText(this, "Functionality limited due to bluetooth being disabled!", Toast.LENGTH_LONG);
                    newToast.show();
                }
                break;
            default:
                throw new RuntimeException("Unhandled activity request code!");
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Check if we've already initialized this fragment
        Fragment fragment = mFragmentMap.get(id);
        // If we haven't initialized the fragment
        if (fragment == null) {
            // Get the fragment class for the currently selected item
            Class fragmentClass;
            switch (id) {
                case R.id.nav_terminal:
                    fragmentClass = TerminalFragment.class;
                    break;
                case R.id.nav_diagnostics:
                case R.id.nav_logs:
                case R.id.nav_dumpecu:
                default:
                    throw new RuntimeException("Unhandled drawer item selected!");
            }

            // Initialize the fragment and store it in our map
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                mFragmentMap.put(id, fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Replace our content with the selected fragment and commit
        getSupportFragmentManager().beginTransaction().replace(R.id.drawer_content, fragment).commitAllowingStateLoss();

        // Check the navigation drawer item now that it's selected
        item.setChecked(true);
        // Set our title to the drawer item's title
        setTitle(item.getTitle());
        // Close the drawer
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}