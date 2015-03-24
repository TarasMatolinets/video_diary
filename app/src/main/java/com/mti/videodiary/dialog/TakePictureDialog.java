package com.mti.videodiary.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.mti.videodiary.activity.MenuActivity;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.03.15.
 */
public class TakePictureDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_title)
                .setPositiveButton(R.string.dialog_ok, this)
                .setNegativeButton(R.string.dialog_cancel, this);

        int divierId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(divierId);

        if (divider != null) {
            divider.setVisibility(View.GONE);
        }

        return adb.create();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, MenuActivity.RESULT_LOAD_IMAGE);
                break;
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }
}