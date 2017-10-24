package com.example.mini_son.tmon_musicplayer.view.music;


import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.MusicItem;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.MusicViewType;

/**
 * Created by mini_son on 2017-07-17.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicViewHolder> {

    private Cursor mCursor;
    private boolean mDataValid;
    private MusicViewType mViewType;

    private DataSetObserver mDataSetObserver;

    public MusicAdapter(MusicViewType type) {
        Log.i("TEST", "TEST ORDER MusicAdapter");
        mViewType = type;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("TEST", "TEST ORDER CREATE HOLDER");
        switch (mViewType) {
            case LIST:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_music, parent, false);
                return new MusicViewHolder(v2);
            case CARD:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_music, parent, false);
                return new MusicViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        MusicItem musicItem = MusicItem.bindCursor(mCursor);
        holder.setMusicItem(musicItem);
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
