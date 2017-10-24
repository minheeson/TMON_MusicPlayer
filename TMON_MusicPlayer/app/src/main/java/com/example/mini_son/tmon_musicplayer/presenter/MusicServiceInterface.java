package com.example.mini_son.tmon_musicplayer.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.mini_son.tmon_musicplayer.model.MusicItem;
import com.example.mini_son.tmon_musicplayer.view.music.MusicAdapter;

import java.util.List;

/**
 * Created by mini_son on 2017-07-26.
 */

public class MusicServiceInterface {
    /**
     * MusicService에서 정의한 함수들을 사용하기 위해서는
     * Service에 Bind 해야 직접 접근이 가능
     * -> MusicServiceInterface : MusicService와 직접 바인딩하고 접근할 수 있게 도와줌
     * -> 객체 생성시에 BindService를 통해서 MusicService와 직접 연결
     * -> MusicService 객체를 mService에 할당하고, 이후에 함수를 호출하는 데 사용함
     */
    private ServiceConnection mServiceConnection;
    private MusicService mService;

    public MusicServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("TEST", "TESTTEST SERVICE IF ::: MusicServiceInterface :: onServiceConnected");
                mService = ((MusicService.MusicServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("TEST", "TESTTEST SERVICE IF ::: MusicServiceInterface :: onServiceDisconnected");
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, MusicService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlaylist(List<Long> musicIds) {
        Log.i("TEST", "TEST ORDER :::  SERVICE IF ::: MusicServiceInterface :: setPlaylist : 재생목록 음악 갯수 : " + musicIds.size());
        if (mService != null) {
            mService.setPlaylist(musicIds);
        }
        for (int i = 0; i < musicIds.size(); i++) {
            Log.i("TEST", "TEST ORDER :::  SERVICE IF ::: MusicServiceInterface :: setPlaylist : 재생목록 음악  : " + musicIds.get(i));
        }
    }

    public MusicItem getMusicItem() {
        Log.i("TEST", "TESTTEST SERVICE IF ::: getMusicItem :: ");
        if (mService != null) {
            return mService.getMusicItem();
        }
        return null;
    }

    public MediaPlayer getMusicPlayer() {
        Log.i("TEST", "TESTTEST SERVICE IF ::: getMusicPlayer :: ");
        if (mService != null) {
            return mService.getMusicPlayer();
        }
        return null;
    }


    public void play(int position) {
        Log.i("TEST", "TEST ORDER SERVICE IF ::: play 함수 :: 내가 누른 위치 : " + position);
        if (mService != null) {
            mService.play(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            Log.i("TEST", "TESTTEST SERVICE IF ::: togglePlay :: stop playing");
            mService.pause();
        } else {
            Log.i("TEST", "TESTTEST SERVICE IF ::: togglePlay :: restart playing");
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlayling();
        }
        return false;
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }


}
