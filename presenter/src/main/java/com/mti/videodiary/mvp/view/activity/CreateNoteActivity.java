package com.mti.videodiary.mvp.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mti.videodiary.di.IHasComponent;
import com.mti.videodiary.di.component.ActivityComponent;
import com.mti.videodiary.mvp.presenter.CreateNotePresenter;
import com.mti.videodiary.mvp.view.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.NoteDomain;
import mti.com.videodiary.R;

import static com.mti.videodiary.utils.Constants.KEY_POSITION;

/**
 * Created by Taras Matolinets on 29.03.15.
 * Activity for create or edit note
 */
public class CreateNoteActivity extends BaseActivity implements TextWatcher, IHasComponent<ActivityComponent> {
    public static final int DEFAULT_VALUE = -1;

    @BindView(R.id.et_title) EditText mEtTitle;
    @BindView(R.id.etDescription) EditText mEtDescription;

    @Inject CreateNotePresenter mPresenter;

    private ActivityComponent mComponent;
    private boolean isShowSave;
    private boolean isEditNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_create_note);

        setComponent();
        ButterKnife.bind(this);

        mPresenter.setView(this);
        initListeners();
        initActionBar();
        getNoteByPosition();
    }

    @Override
    public void setComponent() {
        mComponent = getActivityComponent();
        mComponent.inject(this);
    }

    private void getNoteByPosition() {
        int position = getIntent().getIntExtra(KEY_POSITION, DEFAULT_VALUE);

        if (position != DEFAULT_VALUE) {
            isEditNote = true;
            mPresenter.getNoteByPosition(position);
        }
    }

    public void fillNoteByPosition(NoteDomain note) {
        mEtDescription.setText(note.getDescription());
        mEtTitle.setText(note.getTitle());
    }

    private void initActionBar() {
        int position = getIntent().getIntExtra(KEY_POSITION, DEFAULT_VALUE);

        if (getSupportActionBar() != null) {
            if (position == DEFAULT_VALUE) {
                getSupportActionBar().setTitle(R.string.create_note);
            } else {
                getSupportActionBar().setTitle(R.string.edit_note);
            }
            getSupportActionBar().show();
        }
    }

    private void initListeners() {
        mEtTitle.addTextChangedListener(this);
        mEtDescription.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_note, menu);

        int defValue = 0;
        MenuItem item = menu.getItem(defValue);
        item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        int defValue = 0;
        MenuItem item = menu.getItem(defValue);

        if (isShowSave) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                saveEditNote();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return false;
    }

    private void saveEditNote() {
        if (!isEditNote) {
            createNewNoteDaily();
        } else {
            updateNoteDaily();
        }
    }

    private void updateNoteDaily() {
        int position = getIntent().getIntExtra(KEY_POSITION, DEFAULT_VALUE);

        String description = mEtDescription.getText().toString();
        String title = mEtTitle.getText().toString();

        mPresenter.updateNoteList(description, title, position);
    }

    private void createNewNoteDaily() {
        String description = mEtDescription.getText().toString();
        String title = mEtTitle.getText().toString();

        mPresenter.createNotePresenter(description, title);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean isTitleFill = mEtTitle.getText().length() > 0;
        boolean isDescriptionFill = mEtDescription.getText().length() > 0;

        isShowSave = isTitleFill && isDescriptionFill;
        invalidateOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public ActivityComponent getComponent() {
        return mComponent;
    }
}
