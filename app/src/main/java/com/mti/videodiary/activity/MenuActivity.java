package com.mti.videodiary.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.mti.videodiary.fragment.AboutMeFragment;
import com.mti.videodiary.fragment.NoteFragment;
import com.mti.videodiary.fragment.VideoFragment;

import java.io.File;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import mti.com.videodiary.R;


public class MenuActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {

        // create and set the header
        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer_header, null);
        setDrawerHeaderCustom(view);

        int selectedColor = getResources().getColor(R.color.blue);

        addSection(newSection(getString(R.string.menu_records), R.drawable.ic_videocam_black, new VideoFragment()).setSectionColor(selectedColor));
        addSection(newSection(getString(R.string.menu_notes), R.drawable.ic_note_add_black, new NoteFragment()).setSectionColor(selectedColor));
        addSection(newSection(getString(R.string.menu_about_me), R.drawable.ic_person_black, new AboutMeFragment()).setSectionColor(selectedColor));

        addBottomSection(newSection(getString(R.string.menu_settings), R.drawable.ic_settings_black, new SettingFragment()).setSectionColor(selectedColor));

        String videoFolder = BaseActivity.VIDEO_DAILY_DIRECTORY + BaseActivity.VIDEO_DIR;
        String noteFolder = BaseActivity.VIDEO_DAILY_DIRECTORY + BaseActivity.NOTE_DIR;

        createFolder(videoFolder);
        createFolder(noteFolder);

        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_CUSTOM);
    }

    /**
     * create folders for feature files
     *
     * @param nameFolder folder name
     */
    private void createFolder(String nameFolder) {
        File f = new File(Environment.getExternalStorageDirectory(), nameFolder);

        if (!f.exists()) {
            f.mkdirs();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //close drawer on start
        if (isDrawerOpen())
            closeDrawer();
    }


    @Override
    protected MaterialSection backToSection(MaterialSection currentSection) {
        MaterialSection section;
        int sectionPos = currentSection.getPosition();

        switch (sectionPos) {
            case 0:
                section = getSectionAtCurrentPosition(sectionPos);
                Fragment fragment = (Fragment) section.getTargetFragment();

                if (fragment instanceof VideoFragment)
                    ((VideoFragment) fragment).onBackPress();

                break;
            case 1:
                section = getSectionAtCurrentPosition(sectionPos);

                break;
            case 2:
                section = getSectionAtCurrentPosition(sectionPos);

                break;
            default:
                section = super.backToSection(currentSection);
                break;
        }

        return section;
    }
}
