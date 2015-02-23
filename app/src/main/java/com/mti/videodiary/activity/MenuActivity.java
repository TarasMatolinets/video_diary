package com.mti.videodiary.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mti.videodiary.fragment.NoteFragment;
import com.mti.videodiary.fragment.VideoFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import mti.com.videodiary.R;


public class MenuActivity extends MaterialNavigationDrawer {


    @Override
    public void init(Bundle bundle) {

        // create and set the header
        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer_header, null);
        setDrawerHeaderCustom(view);

        addSection(newSection(getString(R.string.menu_records), R.drawable.ic_videocam_black, new VideoFragment()));
        addSection(newSection(getString(R.string.menu_notes), R.drawable.ic_create_black, new NoteFragment()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //close drawer on start
        if (isDrawerOpen())
            closeDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
