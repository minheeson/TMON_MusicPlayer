package com.example.mini_son.tmon_musicplayer.model.realm;

import android.util.Log;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by sonminhee on 2017. 7. 24..
 */

public class RealmHelper {

    Realm mRealm;

    public RealmHelper(Realm realm) {
        this.mRealm = realm;
    }

    public void insertPlaylist(long id, long albumId, String title, String artist, int index, long duration) {
        mRealm.beginTransaction();
        Playlist playlist = mRealm.createObject(Playlist.class);
        playlist.setId(id);
        playlist.setAlbumId(albumId);
        playlist.setTitle(title);
        playlist.setArtist(artist);
        playlist.setIndex(index);
        playlist.setDuration(duration);
        mRealm.commitTransaction();
    }

    public void deletePlaylist(String title){
        Log.i("TEST", "TEST MENU::: RealmHelper " + title);
        mRealm.beginTransaction();
        RealmResults<Playlist> deleteResult = mRealm.where(Playlist.class).equalTo("title", title).findAll();
        deleteResult.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    public List<Playlist> retrievePlaylist() {
        List<Playlist> userPlaylist = new ArrayList<>();
        RealmResults<Playlist> allPlaylist = mRealm.where(Playlist.class).findAll();
        for (Playlist playlist : allPlaylist) {
            userPlaylist.add(playlist);
        }
        return userPlaylist;
    }

    public List<Long> getMusicIds(){
        List<Long> musicIds = new ArrayList<>();
        RealmResults<Playlist> allPlaylist = mRealm.where(Playlist.class).findAll();
        for(Playlist playlist : allPlaylist){
            musicIds.add(playlist.getId());
            Log.i("TEST", "TESTTEST 램에서 가져오는거 목록 ::: "+ playlist.getTitle());
        }
        return musicIds;
    }

    public List<Integer> getMusicIndex(){
        List<Integer> musicIndex = new ArrayList<>();
        RealmResults<Playlist> allPlaylist = mRealm.where(Playlist.class).findAll();
        for(Playlist playlist : allPlaylist){
            musicIndex.add(playlist.getIndex());
        }
        return musicIndex;
    }
/*

    public int getMusicIndex(String title) {
        Playlist indexResult = mRealm.where(Playlist.class).equalTo("title", title).findFirst();
        return indexResult.getIndex();
    }
*/

    public String getMusicTitle(int index) {
        Playlist result = mRealm.where(Playlist.class).equalTo("index", index).findFirst();
        return result.getTitle();
    }

    public void swapDownMusicIndex(int fromIndex, int toIndex){
        String title = mRealm.where(Playlist.class).equalTo("index", toIndex).findFirst().getTitle();
        mRealm.beginTransaction();
        Playlist fromTitle = mRealm.where(Playlist.class).equalTo("index", fromIndex).findFirst();
        fromTitle.setIndex(toIndex);

        RealmResults<Playlist> results= mRealm.where(Playlist.class).equalTo("index", toIndex).findAll();
        for (int i=0; i<results.size(); i++){
            if(results.get(i).getTitle().equals(title)){
                results.get(i).setIndex(fromIndex);
            }
        }
        mRealm.commitTransaction();
    }

    public void swapUpMusicIndex(int fromIndex, int toIndex){
        String title = mRealm.where(Playlist.class).equalTo("index", toIndex).findFirst().getTitle();
        mRealm.beginTransaction();
        Playlist toTitle = mRealm.where(Playlist.class).equalTo("index", fromIndex).findFirst();
        toTitle.setIndex(toIndex);

        RealmResults<Playlist> results= mRealm.where(Playlist.class).equalTo("index", toIndex).findAll();
        for (int i=0; i<results.size(); i++){
            if(results.get(i).getTitle().equals(title)){
                results.get(i).setIndex(fromIndex);
            }
        }
        mRealm.commitTransaction();
    }

    public void shuffleMusicIndex(){
        List<Integer> shuffleIndex = getMusicIndex();
        Collections.shuffle(shuffleIndex);

        mRealm.beginTransaction();
        RealmResults<Playlist> allPlaylist = mRealm.where(Playlist.class).findAll();
        for(int i=0; i<allPlaylist.size(); i++){
            Playlist playlist = mRealm.where(Playlist.class).equalTo("index", i).findFirst();
            playlist.setIndex(shuffleIndex.get(i));
        }
        mRealm.commitTransaction();
    }




}
