package com.example.mini_son.tmon_musicplayer.view.playlist;

import com.example.mini_son.tmon_musicplayer.model.realm.Playlist;

import java.util.Comparator;

/**
 * Created by mini_son on 2017-07-26.
 */

public class SortPlaylist implements Comparator<Playlist> {
    @Override
    public int compare(Playlist playlist, Playlist nextPlaylist) {
        if(playlist.getIndex()>nextPlaylist.getIndex())
            return 1;
        else if(playlist.getIndex()<nextPlaylist.getIndex())
            return -1;
        else
            return 0;
    }
}
