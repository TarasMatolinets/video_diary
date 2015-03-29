package com.mti.videodiary.activity;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.mti.videodiary.data.dao.Note;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.manager.NoteDataManager;
import com.mti.videodiary.fragment.VideoFragment;

import java.io.File;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 29.03.15.
 */
public class CreateNoteActivity extends BaseActivity implements TextWatcher {
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;
    public static final int DURATION = 1000;
    public static final int DEFAULT_ITEM_POSITION = -1;
    public static final int DURATION_FADE_IN = 600;

    private EditText mEtTitle;
    private EditText mEtDescription;
    private boolean isShowSave;
    private ActionBar mActionBar;
    private boolean isEditNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_note);

        initViews();
        initListeners();
        initActionBar();
    }


    private void initActionBar() {
        mActionBar = getSupportActionBar();

        int position = getIntent().getIntExtra(VideoFragment.KEY_POSITION, -1);

        if (position == DEFAULT_ITEM_POSITION)
            mActionBar.setTitle(R.string.create_note);
        else
            mActionBar.setTitle(R.string.edit_note);

        mActionBar.show();
    }

    private void initViews() {
        mEtTitle = (EditText) findViewById(R.id.etTitle);
        mEtDescription = (EditText) findViewById(R.id.etDescription);
    }

    private void initListeners() {
        mEtTitle.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_video_note, menu);

        MenuItem item = menu.getItem(0);
        item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.getItem(0);
        if (isShowSave)
            item.setVisible(true);
        else
            item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                saveVideoNote();
                break;
            case android.R.id.home:
                if (!isEditNote)
                    deleteVideoFile();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
            deleteVideoFile();
                finish();
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

    private void deleteVideoFile() {
        String videoFilePath = getIntent().getStringExtra(VideoFragment.KEY_VIDEO_PATH);

        if (videoFilePath != null) {
            File file = new File(videoFilePath);
            if (file.exists())
                file.delete();
        }
    }

    private void saveVideoNote() {
        if (!isEditNote) {
            createNewNoteDaily();
        } else
            updateNoteDaily();

        setResult(MenuActivity.UPDATE_VIDEO_ADAPTER, null);
    }

    private void updateNoteDaily() {
        int position = getIntent().getIntExtra(VideoFragment.KEY_POSITION, -1);
        NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);

        Note video = noteDataManager.getNoteByPosition(position);
        video.setDescription(mEtDescription.getText().toString());
        video.setTitle(mEtTitle.getText().toString());

        noteDataManager.updateNoteList(video);
    }

    private void createNewNoteDaily() {
            Note video = new Note();

            video.setTitle(mEtTitle.getText().toString());
            video.setDescription(mEtDescription.getText().toString());

            NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
            noteDataManager.createNote(video);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEtTitle.getText().length() > 0)
            isShowSave = true;
        else
            isShowSave = false;

        invalidateOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
