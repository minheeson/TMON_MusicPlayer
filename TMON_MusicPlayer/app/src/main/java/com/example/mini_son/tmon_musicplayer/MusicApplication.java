package com.example.mini_son.tmon_musicplayer;

import android.app.Application;
import android.util.Log;

import com.example.mini_son.tmon_musicplayer.presenter.MusicServiceInterface;

/**
 * Created by mini_son on 2017-07-26.
 */

public class MusicApplication extends Application {
    private static MusicApplication mInstance;
    private MusicServiceInterface mInterface;

    @Override
    public void onCreate() {
        Log.i("TEST", "TESTTEST ::: MusicApplication :: onCreate");
        super.onCreate();
        mInstance = this;
        mInterface = new MusicServiceInterface(getApplicationContext());
    }

    /**
     * 어떤 위치에서도 접근할 수 있도록
     */
    public static MusicApplication getInstance(){
        Log.i("TEST", "TESTTEST ::: MusicApplication :: getInstance");
        return mInstance;
    }

    public MusicServiceInterface getServiceInterface(){
        Log.i("TEST", "TESTTEST ::: MusicApplication :: getServiceInterface");
        return mInterface;
    }
}
