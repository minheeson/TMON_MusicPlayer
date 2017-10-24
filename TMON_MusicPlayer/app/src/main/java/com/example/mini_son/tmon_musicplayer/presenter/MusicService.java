package com.example.mini_son.tmon_musicplayer.presenter;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mini_son.tmon_musicplayer.model.BroadcastActions;
import com.example.mini_son.tmon_musicplayer.model.CommandActions;
import com.example.mini_son.tmon_musicplayer.model.MusicItem;
import com.example.mini_son.tmon_musicplayer.view.activity.MusicPlayerActivity;
import com.example.mini_son.tmon_musicplayer.view.MusicPlayerNotification;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.RepeatStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mini_son on 2017-07-26.
 */


/**
 * 음악이 변경되거나 재생될때, 일시정지할때마다 Broadcast 메시지를 전송하고
 * 이 메시지를 Activity에서 받아서
 * UI에 보여줌
 */
public class MusicService extends Service {


    private final IBinder mBinder = new MusicServiceBinder();
    private List<Long> mMusicIds = new ArrayList<>();
    private MediaPlayer mMusicPlayer;
    private boolean isPrepared;
    private int mCurrentPosition;
    private MusicItem mMusicItem;

    private MusicPlayerNotification mMusicPlayerNotification;


    public class MusicServiceBinder extends Binder {
        //MusicService에서 제공해주는 public함수들을 사용하기 위한 통신채널
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onBind");
        this.startService(intent);
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onCreate");
        super.onCreate();
        mMusicPlayer = new MediaPlayer();
        mMusicPlayerNotification = new MusicPlayerNotification(this);

        mMusicPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMusicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onPrepared :: ");
                isPrepared = true;
                mp.start();
                sendBroadcast(new Intent(BroadcastActions.PREPARED)); //prepared 전송
                updateNotificationPlayer();
            }
        });

        mMusicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onCompletion");
                isPrepared = true;
                //sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
                //mCurrentPosition++;
                //play(mCurrentPosition);
                if (MusicPlayerActivity.repeatStatus == RepeatStatus.REPEAT_OFF) {
                    forward();
                } else if (MusicPlayerActivity.repeatStatus == RepeatStatus.REPEAT_ON) {
                    play(mCurrentPosition);
                }

                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
                updateNotificationPlayer();
            }
        });

        mMusicPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onError");
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
                updateNotificationPlayer();
                return false;
            }
        });

        mMusicPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            if (CommandActions.TOGGLE_PLAY.equals(action)) {
                if (isPlayling()) {
                    pause();

                } else {
                    play();
                }
            } else if (CommandActions.REWIND.equals(action)) {
                rewind();
            } else if (CommandActions.FORWARD.equals(action)) {
                forward();
            } else if (CommandActions.CLOSE.equals(action)) {

                pause();
                removeNotificationPlayer();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onDestroy");
        super.onDestroy();
        if (mMusicPlayer != null) {
            mMusicPlayer.stop();
            mMusicPlayer.release();
            mMusicPlayer = null;
        }
        //removeNotificationPlayer();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onTaskRemoved");
        if (!isPlayling()) {
            Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: onTaskRemoved ::: 정지 ");
            //pause();
            //stopSelf();
            removeNotificationPlayer();
        }
    }

    /**
     * 재생목록 기준으로
     * 사용자로부터 재생할 목록의 position 값을 받아서 현재 재생할 음악에 대한 정보를 불러와서
     * MusicItem에 저장
     */
    private void queryMusicItem(int position) {
        Log.i("TEST", "TEST ORDER ::: SERVICE ::  :: queryMusicItem : " + position);
        mCurrentPosition = position;
        for (int i = 0; i < mMusicIds.size(); i++) {
            Log.i("TEST", "TESTTEST ::: SERVICE ::  :: query :: \n" + mMusicIds.get(i) + "\n");
        }
        long musicId = mMusicIds.get(position);
        Log.i("TEST", "TEST ORDER ::: SERVICE ::  :: position의  musidID " + musicId);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = {String.valueOf(musicId)};
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mMusicItem = MusicItem.bindCursor(cursor);
            }
            cursor.close();
        }
        Log.i("TEST", "TEST ORDER ::: SERVICE :: queryMusicItem ::: " + mMusicItem.mTitle);
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
    }

    /**
     * musicIds 리스크를 받아서 Service 내부 변수에 저장
     */
    public void setPlaylist(List<Long> musicIds) {
        Log.i("TEST", "TEST ORDER :::  MUSIC PLAY ::: MusicService :: setPlaylist" + " 노래 개수 : " + musicIds.size());
        if (!mMusicIds.equals(musicIds)) {
            mMusicIds.clear();
            for (int i = 0; i < musicIds.size(); i++) {
                Log.i("TEST", "TEST ORDER :::  MUSIC ID LIST :: \n" + musicIds.get(i) + "\n");
            }
            mMusicIds.addAll(musicIds);
        }
    }


    public MusicItem getMusicItem() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: getMusicItem ::: " + mMusicItem.mTitle);
        return mMusicItem;
    }

    public MediaPlayer getMusicPlayer() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: getMusicPlayer ::: ");
        return mMusicPlayer;
    }

    public boolean isPlayling() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: isPlaying ::: ");
        return mMusicPlayer.isPlaying();
    }

    /**
     * MusicPlayer를 재생가능한 상태로 만들어줌
     */
    private void prepare() {
        Log.i("TEST", "TEST ORDER ::: SERVICE :: PREPARE ");
        try {
            Log.i("TEST", "TEST ORDER ::: SERVICE :: PREPARE : 현재 재생 :: " + mMusicItem.mTitle);
            mMusicPlayer.setDataSource(mMusicItem.mDataPath);
            mMusicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMusicPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
    }

    /**
     * 음악을 재생할 수 있는 play
     */
    public void play(int position) {
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
        //최초 재생시에 호출하게 될 목적
        //호출됨과 동시에 MusicItem을 셋팅하는 queryMusicItem 함수를 호출하고,
        //기존에 재생하고 있던 음악을 종료하는 stop을 호출한 다음
        //prepare 함수를 호출함
        Log.i("TEST", "TEST ORDER SERVICE ::: position ::: 현재 재생 position : " + position);
        queryMusicItem(position);
        stop();
        prepare();
        //sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
    }

    public void play() {
        //최초 재생이후 prepare가 되어있는 상태의 MusicPlayer를 start하는 용도
        if (isPrepared) {
            mMusicPlayer.start();
            Log.i("TEST", "TEST ORDER SERVICE ::: position ::: play누르고 상태바꿈");
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
            updateNotificationPlayer();
        }
    }

    /**
     * 음악을 일시정지
     */
    public void pause() {
        if (isPrepared) {
            mMusicPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
            updateNotificationPlayer();
            //mMusicPlayerNotification.stopForeground();
            mMusicPlayerNotification.goForeground();
        }
    }

    /**
     * 음악 종료
     */
    private void stop() {
        Log.i("TEST", "TEST ORDER ::: SERVICE :: STOP ");
        mMusicPlayer.stop();
        mMusicPlayer.reset();
        //sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); //재생상태
    }

    /**
     * 다음곡으로 이동
     */
    public void forward() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: forward() ::: ");
        if (mMusicIds.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; //다음 포지션으로 이동
        } else {
            mCurrentPosition = 0; //처음 포지션으로 이동
        }
        play(mCurrentPosition);
    }

    /**
     * 이전곡으로 이동
     */
    public void rewind() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: MusicService :: rewind() ::: ");
        if (mCurrentPosition > 0) {
            mCurrentPosition--; //이전 포지션으로 이동
        } else {
            mCurrentPosition = mMusicIds.size() - 1; //마지막 포지션으로 이동
        }
        play(mCurrentPosition);
    }

    private void updateNotificationPlayer() {
        if (mMusicPlayerNotification != null) {
            mMusicPlayerNotification.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        if (mMusicPlayerNotification != null) {
            mMusicPlayerNotification.removeNotificationplayer();
        }
    }


}
