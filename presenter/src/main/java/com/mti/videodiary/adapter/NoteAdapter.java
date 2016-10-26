package com.mti.videodiary.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mti.videodiary.mvp.activity.CreateNoteActivity;
import com.mti.videodiary.mvp.activity.MenuActivity;
import com.mti.videodiary.data.dao.Note;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.manager.NoteDataManager;
import com.mti.videodiary.dialog.DeleteItemDialogFragment;
import com.mti.videodiary.interfaces.OnDialogClickListener;
import com.mti.videodiary.utils.Constants;

import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 29.03.15.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements View.OnClickListener, OnDialogClickListener {
    private Context mContext;
    private List<Note> mListNotes;
    private View view;

    public NoteAdapter(Context context, List<Note> listVideoNotes) {
        mContext = context;
        mListNotes = listVideoNotes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_note_view, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Note note = mListNotes.get(position);

        holder.delete.setTag(position);
        holder.share.setTag(position);
        holder.cardView.setTag(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvDescription.setText(note.getDescription());
    }

    @Override
    public int getItemCount() {
        return mListNotes.size();
    }

    public void setListNotes(List<Note> list) {
        mListNotes = list;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

        switch (v.getId()) {
            case R.id.ivDelete:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_POSITION_NOTE_ADAPTER, position);

                DeleteItemDialogFragment fragment = new DeleteItemDialogFragment();
                fragment.setDialogClickListener(this);
                fragment.setArguments(bundle);
                fragment.show(((MenuActivity) mContext).getSupportFragmentManager(), null);
                break;
            case R.id.cardViewCreateNote:
                Intent activityIntent = new Intent(mContext, CreateNoteActivity.class);
                activityIntent.putExtra(Constants.KEY_POSITION, position);

                ((MenuActivity) mContext).startActivityForResult(activityIntent, Constants.UPDATE_NOTE_ADAPTER);
                break;
            case R.id.ivShare:
                Note noteForShare = mListNotes.get(position);

                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, noteForShare.getDescription());
                mContext.startActivity(Intent.createChooser(intent2, mContext.getResources().getString(R.string.share_text_note)));
                break;
        }
    }

    @Override
    public void dialogWithDataClick(Object object) {
        int position = (Integer) object;

        Note note = mListNotes.get(position);

        NoteDataManager noteManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
        noteManager.deleteNoteById(note.getId());

        mListNotes.remove(note);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mListNotes.size());

        Intent intent = new Intent(Constants.UPDATE_ADAPTER_INTENT);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void dialogClick() {
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView delete;
        public ImageView share;
        public CardView cardView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvDescription = (TextView) itemLayoutView.findViewById(R.id.etDescription);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.etTitle);
            delete = (ImageView) itemLayoutView.findViewById(R.id.ivDelete);
            share = (ImageView) itemLayoutView.findViewById(R.id.ivShare);
            cardView = (CardView) itemLayoutView.findViewById(R.id.cardViewCreateNote);

            cardView.setOnClickListener(NoteAdapter.this);
            delete.setOnClickListener(NoteAdapter.this);
            share.setOnClickListener(NoteAdapter.this);
        }
    }
}
