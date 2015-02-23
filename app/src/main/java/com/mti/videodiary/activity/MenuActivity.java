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

        addSection(newSection(getString(R.string.menu_records), R.drawable.ic_videocam_black, new VideoFragment()).setSectionColor(getResources().getColor(R.color.blue)));
        addSection(newSection(getString(R.string.menu_notes), R.drawable.ic_create_black, new NoteFragment()).setSectionColor(getResources().getColor(R.color.blue)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //close drawer on start
        if (isDrawerOpen())
            closeDrawer();
    }

}
