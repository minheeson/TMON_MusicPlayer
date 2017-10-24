package com.example.mini_son.tmon_musicplayer.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.realm.Playlist;
import com.example.mini_son.tmon_musicplayer.model.realm.RealmHelper;
import com.example.mini_son.tmon_musicplayer.view.listener.ItemTouchHelperCallback;
import com.example.mini_son.tmon_musicplayer.view.listener.OnStartDragListener;
import com.example.mini_son.tmon_musicplayer.view.playlist.PlaylistAdapter;
import com.example.mini_son.tmon_musicplayer.view.playlist.PlaylistViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by mini_son on 2017-07-28.
 */

public class PlaylistFragment extends Fragment {

    private RecyclerView mPlaylistRecyclerView;
    private List<Playlist> mPlaylist;
    private RealmHelper mRealmHelper;
    private Realm mRealm;

    private LinearLayoutManager mPlaylistLlm;

    OnStartDragListener mListener;
    private ItemTouchHelper itemTouchHelper;
    private PlaylistAdapter mPlaylistAdapter;

    public PlaylistFragment() {
        Log.i("TEST", "TEST FRAGMENT ::: PlaylistFragment() ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TEST", "TEST FRAGMENT ::: PlaylistFragment :: onCreateView()");
        View playlistView = inflater.inflate(R.layout.fragment_playlist, container, false);
        initRealm(playlistView);
        initPlaylist();

        mListener = new OnStartDragListener() {
            @Override
            public void onStartDrag(PlaylistViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        };

        mPlaylistLlm = new LinearLayoutManager(playlistView.getContext());
        mPlaylistAdapter = new PlaylistAdapter(playlistView.getContext(), mPlaylist, mListener);
        setPlaylist();

        return playlistView;
    }

    private void initRealm(View view) {
        Realm.init(view.getContext());
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = new RealmHelper(mRealm);
        mPlaylistRecyclerView = (RecyclerView) view.findViewById(R.id.playlistRecyclerView);
    }

    public void initPlaylist() {
        mPlaylist = new ArrayList<>();
        mPlaylist = mRealmHelper.retrievePlaylist(); //재생목록 저장
    }

    private void setPlaylist() {
        mPlaylistRecyclerView.setFocusable(true);
        mPlaylistRecyclerView.setLayoutManager(mPlaylistLlm);

        ItemTouchHelperCallback mCallback = new ItemTouchHelperCallback(mPlaylistAdapter);
        itemTouchHelper = new ItemTouchHelper(mCallback);

        itemTouchHelper.attachToRecyclerView(mPlaylistRecyclerView);
        mPlaylistRecyclerView.setAdapter(mPlaylistAdapter);
    }
}
