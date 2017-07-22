package com.example.mini_son.tmon_musicplayer.model;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by mini_son on 2017-07-17.
 */

public class MusicItem {
    public long mId; // 오디오 고유 ID
    public long mAlbumId; // 오디오 앨범아트 ID
    public String mTitle; // 타이틀 정보
    public String mArtist; // 아티스트 정보
    public String mAlbum; // 앨범 정보
    public long mDuration; // 재생시간
    public String mDataPath; // 실제 데이터위치


    public static MusicItem bindCursor(Cursor cursor){
        MusicItem musicItem = new MusicItem();
        musicItem.mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
        musicItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
        musicItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
        musicItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
        musicItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
        musicItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
        musicItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
        return musicItem;
    }
}
