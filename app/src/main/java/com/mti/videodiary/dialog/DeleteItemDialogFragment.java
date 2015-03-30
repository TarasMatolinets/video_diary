package com.mti.videodiary.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mti.videodiary.interfaces.OnDialogClickListener;
import com.mti.videodiary.utils.Constants;

import mti.com.videodiary.R;

/**
 * Created by taras on 30.03.15.
 */
public class DeleteItemDialogFragment extends DialogFragment implements View.OnClickListener {
    public OnDialogClickListener mDialogClick;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_choice);
        dialog.setTitle(R.string.delete_item);

        TextView titleDesc = (TextView) dialog.findViewById(R.id.tvDesc);
        titleDesc.setVisibility(View.GONE);

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

    public void setDialogClickListener(OnDialogClickListener listener) {
        mDialogClick = listener;
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
                int position = getArguments().getInt(Constants.KEY_POSITION_NOTE_ADAPTER);

                if (mDialogClick != null)
                    mDialogClick.dialogWithDataClick(position);
                dismiss();
                break;
            case R.id.btCancel:
                dismiss();
                break;
        }
    }


}
