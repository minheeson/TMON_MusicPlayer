package com.example.mini_son.tmon_musicplayer.view.music;

import android.content.ContentUris;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.MusicItem;
import com.example.mini_son.tmon_musicplayer.model.realm.RealmHelper;
import com.example.mini_son.tmon_musicplayer.MusicApplication;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

/**
 * Created by mini_son on 2017-07-17.
 */

public class MusicViewHolder extends RecyclerView.ViewHolder {
    private final Uri ImgUri = Uri.parse("content://media/external/audio/albumart");
    private ImageView mImgAlbum;
    private long mImgId;
    private long mId;
    private long mDuration;
    private TextView mTxtTitle;
    private TextView mTxtArtist;
    private ImageButton mImgButton;
    private Realm mRealm;
    private RealmHelper mRealmHelper;
    private int mIndex = 0;

    public MusicViewHolder(final View itemView) {
        super(itemView);
        //final MusicItem musicItem = MusicApplication.getInstance().getServiceInterface().getMusicItem();

        mImgAlbum = (ImageView) itemView.findViewById(R.id.img_album);
        mTxtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        mTxtArtist = (TextView) itemView.findViewById(R.id.txt_artist);

        Realm.init(itemView.getContext());
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = new RealmHelper(mRealm);

        mImgButton = (ImageButton) itemView.findViewById(R.id.btn_popup);
        mImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_popup) {
                    PopupMenu p = new PopupMenu(v.getContext(), v);
                    MenuInflater inflater = p.getMenuInflater();
                    Menu menu = p.getMenu();
                    inflater.inflate(R.menu.menu_play_option, menu);
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.add_playlist:
                                    Log.i("TEST POPUP", "TEST INSERT 1 ::: " + mTxtTitle.getText() + " /// " + mTxtArtist.getText());
                                    mRealmHelper.insertPlaylist(mId, mImgId, mTxtTitle.getText().toString(), mTxtArtist.getText().toString(), mRealmHelper.retrievePlaylist().size(), mDuration);
                                    break;
                                case R.id.delete_music:
                                    Log.i("TEST POPUP", "TITLE ::: " + mTxtTitle.getText());
                            }
                            return false;
                        }
                    });
                    p.show();
                }
            }
        });
    }

    public void setMusicItem(MusicItem item) {
        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            final MusicItem musicItem = MusicApplication.getInstance().getServiceInterface().getMusicItem();
            Log.i("TEST", "TEST SONG CORRECT setMusicItem :::::::::::::::::: now Playing :: " +musicItem.mTitle);
            Log.i("TEST", "TEST SONG CORRECT setMusicItem :::::::::::::::::: item 제목 :: " +item.mTitle);
            if (musicItem.mTitle.equals(item.mTitle)){
                Log.i("TEST", "TEST SONG CORRECT " + ":: " + musicItem.mTitle);
                Log.i("TEST", "TEST SONG CORRECT " + ":: " + item.mTitle);
                Log.i("TEST", "TEST SONG CORRECT ");
            }
        }

        mTxtTitle.setText(item.mTitle);
        mTxtArtist.setText(item.mArtist);
        mImgId = item.mAlbumId;
        mId = item.mId;
        mDuration = item.mDuration;
        Log.i("TEST POPUP", "ORDERORDER 1 ::: ");
        Uri albumImgUri = ContentUris.withAppendedId(ImgUri, item.mAlbumId);
        Picasso.with(itemView.getContext()).load(albumImgUri).error(R.mipmap.ic_album_default).into(mImgAlbum);
    }


}
