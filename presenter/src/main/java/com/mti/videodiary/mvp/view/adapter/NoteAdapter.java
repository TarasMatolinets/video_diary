package com.mti.videodiary.mvp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mti.videodiary.mvp.view.fragment.NoteFragment.DeleteItem;
import com.mti.videodiary.mvp.view.fragment.NoteFragment.UpdateNote;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.NoteDomain;
import mti.com.videodiary.R;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;

/**
 * Created by Taras Matolinets on 29.03.15.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context mContext;
    private List<NoteDomain> mListNotes = new ArrayList<>();

    public NoteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elem_note_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        NoteDomain note = mListNotes.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvDescription.setText(note.getDescription());
    }

    @Override
    public int getItemCount() {
        return mListNotes.size();
    }

    public void setListNotes(List<NoteDomain> list) {
        mListNotes = list;
        notifyDataSetChanged();
    }

    public void removeNote(int id) {
        mListNotes.remove(id);
        notifyItemRemoved(id);
        notifyItemRangeChanged(id, mListNotes.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.etDescription) TextView tvDescription;
        @BindView(R.id.et_title) TextView tvTitle;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvDescription = (TextView) itemLayoutView.findViewById(R.id.etDescription);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.et_title);

            ButterKnife.bind(this, itemLayoutView);
        }

        @OnClick(R.id.cardViewCreateNote)
        public void createNote() {
            NoteDomain noteDomain = mListNotes.get(getAdapterPosition());

            UpdateNote note = new UpdateNote();
            note.setNoteId(noteDomain.getId());

            EventBus.getDefault().post(note);
        }

        @OnClick(R.id.ivDelete)
        public void deleteNote() {
            NoteDomain noteDomain = mListNotes.get(getAdapterPosition());
            DeleteItem deleteItem = new DeleteItem();
            deleteItem.setNotePosition(getAdapterPosition());
            deleteItem.setId(noteDomain.getId());

            EventBus.getDefault().post(deleteItem);
        }

        @OnClick(R.id.ivShare)
        public void shareNote() {
            NoteDomain noteForShare = mListNotes.get(getAdapterPosition());

            Intent intent2 = new Intent();
            intent2.setAction(ACTION_SEND);
            intent2.setType("text/plain");
            intent2.putExtra(EXTRA_TEXT, noteForShare.getDescription());
            mContext.startActivity(Intent.createChooser(intent2, mContext.getResources().getString(R.string.share_text_note)));
        }
    }

}
