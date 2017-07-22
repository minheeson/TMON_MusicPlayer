package com.example.mini_son.tmon_musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mini_son.tmon_musicplayer.model.MusicItem;
import com.example.mini_son.tmon_musicplayer.view.recyclerview.CursorRecyclerViewAdapter;

/**
 * Created by mini_son on 2017-07-17.
 */

public class MusicAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final int ITEM_LIST = 0;
    private static final int ITEM_CARD = 1;
    private static final int viewType = 0;

    public MusicAdapter(Context context, Cursor cursor, int itemType) {
        super(context, cursor, itemType);
    }

    @Override
    public int getItemCount() {
        Log.i("MusicAdapter -- ", "getItemCount() ");
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("MusicAdapter -- ", "getItemViewType() :::" + position);
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewType = super.getItemType();
        switch (viewType) {
            case ITEM_LIST:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_music, parent, false);
                return new MusicViewHolder(v2);
            case ITEM_CARD:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_music, parent, false);
                return new MusicViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        Log.i("MusicAdapter -- ", "onBindViewHolder() " + cursor.getPosition());

        MusicItem musicItem = MusicItem.bindCursor(cursor);
        ((MusicViewHolder) viewHolder).setMusicItem(musicItem, cursor.getPosition());

    }
}
