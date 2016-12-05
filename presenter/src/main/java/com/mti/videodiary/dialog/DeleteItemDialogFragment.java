package com.mti.videodiary.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mti.videodiary.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import mti.com.videodiary.R;

/**
 * Created by taras on 30.03.15.
 */
public class DeleteItemDialogFragment extends DialogFragment {

    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_item, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mUnbinder.unbind();
        dismiss();
    }

    @OnClick(R.id.bt_okay)
    public void okClick() {
        DeleteItem item = new DeleteItem();
        int id = getArguments().getInt(Constants.KEY_POSITION_NOTE_ADAPTER);
        item.setId(id);

        EventBus.getDefault().post(item);
        dismiss();
    }

    @OnClick(R.id.bt_cancel)
    public void cancelClick() {
        dismiss();
    }

    public static class DeleteItem {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
