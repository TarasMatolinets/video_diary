package com.mti.videodiary.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mti.videodiary.data.dao.Note;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.manager.NoteDataManager;
import com.mti.videodiary.fragment.BaseFragment;
import com.mti.videodiary.utils.Constants;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 29.03.15.
 */
public class CreateNoteActivity extends BaseActivity implements TextWatcher {
    private static final int DEFAULT_ITEM_POSITION = -1;

    private EditText mEtTitle;
    private EditText mEtDescription;
    private boolean isShowSave;
    private ActionBar mActionBar;
    private boolean isEditNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.activity_create_note);

        initViews();
        initListeners();
        initActionBar();
        fillData();
    }

    private void fillData() {
        int position = getIntent().getIntExtra(Constants.KEY_POSITION, -1);

        if (position != DEFAULT_ITEM_POSITION) {
            isEditNote = true;

            NoteDataManager noteDataManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);

            Note note = noteDataManager.getNoteByPosition(position);

            mEtDescription.setText(note.getDescription());
            mEtTitle.setText(note.getTitle());
        }
    }


    private void initActionBar() {
        mActionBar = getSupportActionBar();

        int position = getIntent().getIntExtra(Constants.KEY_POSITION, -1);

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
        mEtDescription.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_note, menu);

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
                saveNote();
                break;
        }
        onBackPressed();

        return false;
    }

    private void saveNote() {
        if (!isEditNote) {
            createNewNoteDaily();
        } else
            updateNoteDaily();

        setResult(RESULT_OK, null);
    }

    private void updateNoteDaily() {
        int position = getIntent().getIntExtra(Constants.KEY_POSITION, -1);
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
        boolean isTitleFill = mEtTitle.getText().length() > 0;
        boolean isDescriptionFill = mEtDescription.getText().length() > 0;

        if (isTitleFill && isDescriptionFill)
            isShowSave = true;
        else
            isShowSave = false;

        invalidateOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
