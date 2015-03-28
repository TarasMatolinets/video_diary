package com.mti.videodiary.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mti.videodiary.activity.MenuActivity;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.03.15.
 */
public class TakePictureDialog extends DialogFragment implements View.OnClickListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_choice_picture);
        dialog.setTitle(R.string.select_image_gallery);

        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setTextColor(getActivity().getResources().getColor(R.color.black));
        title.setTypeface(null, Typeface.NORMAL);
        title.setTextSize(20);
        title.setBackground(new ColorDrawable(Color.WHITE));

        Button btOk = (Button) dialog.findViewById(R.id.btOkay);
        btOk.setOnClickListener(this);

        Button btCancel = (Button) dialog.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(this);

        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);

        View divider = dialog.findViewById(dividerId);
        if (divider != null) {
            divider.setVisibility(View.GONE);
        }

        return dialog;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btOkay:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i, MenuActivity.RESULT_LOAD_IMAGE);
                break;
            case R.id.btCancel:
                dismiss();
                break;
        }
    }
}