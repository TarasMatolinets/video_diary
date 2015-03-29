package com.mti.videodiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.mti.videodiary.activity.BaseActivity;
import com.mti.videodiary.activity.CreateVideoNoteActivity;
import com.mti.videodiary.activity.MenuActivity;
import com.mti.videodiary.data.manager.DataBaseManager;
import com.mti.videodiary.data.dao.Note;
import com.mti.videodiary.data.manager.NoteDataManager;
import com.mti.videodiary.utils.Constants;

import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 29.03.15.
 */
public class NoteAdapter extends RecyclerSwipeAdapter<NoteAdapter.ViewHolder> implements View.OnClickListener {

    public static final String KEY_POSITION_NOTE = "com.mti.position.key.note";

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
        Note video = mListNotes.get(position);

        holder.delete.setTag(position);
        holder.share.setTag(position);

        holder.tvTitle.setText(video.getTitle());
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
                Note note = mListNotes.get(position);

                NoteDataManager noteManager = (NoteDataManager) DataBaseManager.getInstanceDataManager().getCurrentManager(DataBaseManager.DataManager.NOTE_MANAGER);
                noteManager.deleteNoteById(note.getId());

                mListNotes.remove(note);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mListNotes.size());

                Intent intent = new Intent(Constants.UPDATE_ADAPTER_INTENT);
                intent.putExtra(Constants.UPDATE_ADAPTER_NOTE, true);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                break;
            case R.id.flMain:
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                int orientation = mContext.getResources().getConfiguration().orientation;

                Intent activityIntent = new Intent(mContext, CreateVideoNoteActivity.class);

                activityIntent.putExtra(KEY_POSITION_NOTE, position).
                        putExtra(BaseActivity.PACKAGE + ".orientation", orientation).
                        putExtra(BaseActivity.PACKAGE + ".left", screenLocation[0]).
                        putExtra(BaseActivity.PACKAGE + ".top", screenLocation[1]).
                        putExtra(BaseActivity.PACKAGE + ".width", v.getWidth()).
                        putExtra(BaseActivity.PACKAGE + ".height", v.getHeight());

                ((MenuActivity) mContext).startActivityForResult(activityIntent, MenuActivity.UPDATE_VIDEO_ADAPTER);

                ((Activity) mContext).overridePendingTransition(0, 0);
                break;
            case R.id.ivShare:
                Note videoForShare = mListNotes.get(position);

                break;
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return 0;
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
            cardView = (CardView) itemLayoutView.findViewById(R.id.cardViewCreateVideo);

            delete.setOnClickListener(NoteAdapter.this);
            share.setOnClickListener(NoteAdapter.this);
        }
    }
}
