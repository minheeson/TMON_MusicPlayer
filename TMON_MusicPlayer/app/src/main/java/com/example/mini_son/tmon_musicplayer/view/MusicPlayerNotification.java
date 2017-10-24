package com.example.mini_son.tmon_musicplayer.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.CommandActions;
import com.example.mini_son.tmon_musicplayer.presenter.MusicService;
import com.example.mini_son.tmon_musicplayer.view.activity.MusicPlayerActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by sonminhee on 2017. 8. 1..
 */

public class MusicPlayerNotification {
    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private MusicService mMusicService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    public static boolean isForeground;

    /**
     * MusicService를 인자값으로 받으면서
     * 생성과 동시에 NotificationManager 할당
     */
    public MusicPlayerNotification(MusicService service) {
        mMusicService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 음악이 변경되거나 재생상태가 변경될 경우 호출될 예정
     * 알림바에 등록된 Notification을 업데이트하는 역할
     */
    public void updateNotificationPlayer() {
        cancel();
        mNotificationManagerBuilder = new NotificationManagerBuilder();
        mNotificationManagerBuilder.execute();
    }

    /**
     * 사용자가 알림바에서 Notification을 종료하고자 할 때 호출
     * 서비스는 foreground에서 내려 놓는다.
     */
    public void removeNotificationplayer(){
        Log.i("TEST", "TEST NOTI ");
        cancel();
        mMusicService.stopForeground(true);
        isForeground = false;
    }


    public void goForeground(){
        mMusicService.stopForeground(false);
    }




    /**
     * NotificationManagerBuilder(AsyncTask)를 취소하는 역할
     */
    public void cancel(){
        if(mNotificationManagerBuilder != null){
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    /**
     * notification_music_play.xml을 RemoteViews로 만들고
     * 각각의 버튼들에 대한 이벤트를 설정한뒤
     * 알림바에 등록하는 열할과 최초 등록시 서비스를 Foreground로 변경하는 클래스
     */
    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNofiticationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent musicPlayerActivity = new Intent(mMusicService, MusicPlayerActivity.class);
            mMainPendingIntent = PendingIntent.getActivity(mMusicService, 0, musicPlayerActivity, 0);
            mRemoteViews = createRemoteView(R.layout.notifycation_music_player);
            mNofiticationBuilder = new NotificationCompat.Builder(mMusicService);
            mNofiticationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(false)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);

            Notification notification = mNofiticationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendingIntent;
            if(!isForeground){
                isForeground = true; //서비스를 Foreground 상태로 만듬
                mMusicService.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... voids) {
            mNofiticationBuilder.setContent(mRemoteViews);
            mNofiticationBuilder.setContentIntent(mMainPendingIntent);
            mNofiticationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNofiticationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * 생성자로 받은 MusicService와 notification_music_player.xml을 이용하여 RemoteViews 생성
         * 생성한 RemoteViews에 포함된 버튼들에 대한
         */
        private RemoteViews createRemoteView(int layoutId){
            RemoteViews remoteViews = new RemoteViews(mMusicService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            PendingIntent togglePlay = PendingIntent.getService(mMusicService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mMusicService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mMusicService, 0, actionRewind, 0);

            remoteViews.setOnClickPendingIntent(R.id.btn_notification_play, togglePlay);
            remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, rewind);
            remoteViews.setOnClickPendingIntent(R.id.btn_notification_next, forward);

            return remoteViews;
        }

        private void updateRemoteView(RemoteViews remoteViews, Notification notification){
            if(mMusicService.isPlayling()){
                remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.ic_pause_black_24dp);
            }else {
                remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.ic_play_arrow_black_24dp);
            }

            String title = mMusicService.getMusicItem().mTitle;
            String artist = mMusicService.getMusicItem().mArtist;
            remoteViews.setTextViewText(R.id.txt_notification_title, title);
            remoteViews.setTextViewText(R.id.txt_notification_artist, artist);
            Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), mMusicService.getMusicItem().mAlbumId);
            Picasso.with(mMusicService).load(albumUri).error(R.drawable.ic_audiotrack_black_24dp).into(remoteViews, R.id.img_notification_album, NOTIFICATION_PLAYER_ID, notification);
        }


    }
}
