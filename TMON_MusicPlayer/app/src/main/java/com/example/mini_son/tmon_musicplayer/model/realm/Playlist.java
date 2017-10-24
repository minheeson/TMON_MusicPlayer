package com.example.mini_son.tmon_musicplayer.model.realm;

import io.realm.RealmObject;

/**
 * Created by sonminhee on 2017. 7. 24..
 */

public class Playlist extends RealmObject{

    private String title = null;
    private String artist = null;
    private long albumId = 0;
    public int index = 0;
    public long id = 0;
    public long duration = 0;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getArtist(){
        return artist;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public long getAlbumId(){
        return albumId;
    }

    public void setAlbumId(long albumId){
        this.albumId = albumId;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getDuration(){
        return duration;
    }

    public void setDuration(long duration){
        this.duration = duration;
    }


}
