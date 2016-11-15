//package com.mti.videodiary.dialog;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.Typeface;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RadioButton;
//import android.widget.TextView;
//
//import com.mti.videodiary.interfaces.OnDialogClickListener;
//import com.mti.videodiary.utils.Constants;
//import com.mti.videodiary.data.storage.VideoDairySharePreferences;
//
//import mti.com.videodiary.R;
//
///**
// * Created by Taras Matolinets on 01.04.15.
// */
//public class DialogAvatarFragment extends DialogFragment implements View.OnClickListener {
//    private static final int REQUEST_IMAGE_FROM_GALLERY = 333;
//    private static final int REQUEST_IMAGE_FROM_CAMERA = 433;
//    private OnDialogClickListener mDialogClick;
//    private RadioButton mRbPhoto;
//    private RadioButton mRbGallery;
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final Dialog dialog = new Dialog(getActivity());
//
//        dialog.setContentView(R.layout.dialog_avatar);
//        dialog.setTitle(R.string.frag_avatar);
//
//        TextView title = (TextView) dialog.findViewById(android.R.id.title);
//        title.setTextColor(getActivity().getResources().getColor(R.color.black));
//        title.setTypeface(null, Typeface.NORMAL);
//        title.setTextSize(20);
//      //  title.setBackground(new ColorDrawable(Color.WHITE));
//
//        mRbGallery = (RadioButton) dialog.findViewById(R.id.rbGallery);
//        mRbPhoto = (RadioButton) dialog.findViewById(R.id.rbPhoto);
//
//        mRbGallery.setOnClickListener(this);
//        mRbPhoto.setOnClickListener(this);
//
//        Button btOk = (Button) dialog.findViewById(R.id.btOkay);
//        btOk.setOnClickListener(this);
//
//        Button btCancel = (Button) dialog.findViewById(R.id.btCancel);
//        btCancel.setOnClickListener(this);
//
//        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//
//        View divider = dialog.findViewById(dividerId);
//        if (divider != null) {
//            divider.setVisibility(View.GONE);
//        }
//
//        return dialog;
//    }
//
//    public void setDialogClickListener(OnDialogClickListener listener) {
//        mDialogClick = listener;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        dismiss();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btOkay:
//                if (mRbPhoto.isChecked()) {
//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                        getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_FROM_CAMERA);
//                    }
//                } else if (mRbGallery.isChecked()) {
//                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    getActivity().startActivityForResult(i, REQUEST_IMAGE_FROM_GALLERY);
//                }
//
//                break;
//            case R.id.btCancel:
//                dismiss();
//                break;
//            case R.id.rbGallery:
//                mRbPhoto.setChecked(false);
//                mRbGallery.setChecked(true);
//                break;
//            case R.id.rbPhoto:
//                mRbPhoto.setChecked(true);
//                mRbGallery.setChecked(false);
//                break;
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == getActivity().RESULT_OK) {
//            switch (requestCode) {
//                case REQUEST_IMAGE_FROM_GALLERY:
//                    Uri selectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    if (picturePath != null) {
//                        if (mDialogClick != null)
//                            mDialogClick.dialogWithDataClick(picturePath);
//                        VideoDairySharePreferences.setDataToSharePreferences(Constants.IMAGE_AVATAR, picturePath, VideoDairySharePreferences.SHARE_PREFERENCES_TYPE.STRING);
//                    } else {
//                    }
//                    break;
//                case REQUEST_IMAGE_FROM_CAMERA:
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//
//                    break;
//            }
//        }
//    }
//
//}