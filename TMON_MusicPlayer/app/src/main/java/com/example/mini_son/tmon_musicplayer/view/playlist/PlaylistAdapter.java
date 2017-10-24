package com.example.mini_son.tmon_musicplayer.view.playlist;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.realm.Playlist;
import com.example.mini_son.tmon_musicplayer.model.realm.RealmHelper;
import com.example.mini_son.tmon_musicplayer.view.activity.MusicPlayerActivity;
import com.example.mini_son.tmon_musicplayer.view.listener.ItemTouchHelperListener;
import com.example.mini_son.tmon_musicplayer.view.listener.OnStartDragListener;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.ShuffleStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

/**
 * Created by sonminhee on 2017. 7. 24..
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> implements ItemTouchHelperListener {

    private List<Playlist> mPlaylists = new ArrayList<>();
    private OnStartDragListener mStartDragListener;

    private Realm mRealm;
    private RealmHelper mRealmHelper;
    private static Context mContext;

    public PlaylistAdapter(Context context, List<Playlist> playlists, OnStartDragListener startDragListener) {
        Log.i("TEST", "TEST MENU::: PlaylistAdapter");
        mContext = context;

        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = new RealmHelper(mRealm);
        mPlaylists = playlists;

        if (MusicPlayerActivity.shuffleStatus == ShuffleStatus.SHUFFLE_OFF) {
            Collections.sort(mPlaylists, new SortPlaylist());
        } else if (MusicPlayerActivity.shuffleStatus == ShuffleStatus.SHUFFLE_ON) {
            Collections.shuffle(mPlaylists);
        }

        mStartDragListener = startDragListener;

        for (int i = 0; i < mPlaylists.size(); i++) {
            Log.i("TEST POPUP", "TEST MENU::: adapter ::: " + mPlaylists.get(i).getTitle());
        }
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("TEST", "TEST MENU::: onCreateViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        Log.i("TEST", "TEST adapter ::: " + mContext);
        return new PlaylistViewHolder(mContext, v, mPlaylists);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, final int position) {
        Log.i("TEST", "TEST MENU::: onBindViewHolder");

        final Playlist playlist = mPlaylists.get(position);
        Log.i("TEST POPUP", "TEST MENU::: bindHolder::: position " + position + " ::: title ::: " + mPlaylists.get(position).getTitle());
        holder.setPlaylist(playlist, position);

        holder.mBtnDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    Log.i("TEST", "TEST CALLBACK ::: setOnTouchListener");
                    mStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });


        holder.mImgMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popupMenu = new PopupMenu(mContext, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_delete_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove_playlist:
                                mPlaylists.remove(position);
                                mRealmHelper.deletePlaylist(playlist.getTitle());

                                notifyItemRemoved(position);
                                break;
                            case R.id.delete_music:
                                //TODO :: MediaStore에서 삭제
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.i("TEST", "TEST MENU::: getItemCount ::: " + mPlaylists.size());
        return mPlaylists.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            mRealmHelper.swapDownMusicIndex(fromPosition, toPosition);
        } else {
            mRealmHelper.swapUpMusicIndex(fromPosition, toPosition);
        }
        Collections.swap(mPlaylists, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
    }

}
