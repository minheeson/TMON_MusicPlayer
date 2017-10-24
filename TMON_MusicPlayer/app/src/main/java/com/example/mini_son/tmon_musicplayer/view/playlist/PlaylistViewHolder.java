package com.example.mini_son.tmon_musicplayer.view.playlist;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.realm.Playlist;
import com.example.mini_son.tmon_musicplayer.model.realm.RealmHelper;
import com.example.mini_son.tmon_musicplayer.MusicApplication;
import com.example.mini_son.tmon_musicplayer.view.activity.MusicPlayerActivity;
import com.example.mini_son.tmon_musicplayer.view.listener.OnClickPlaylistListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by sonminhee on 2017. 7. 24..
 */

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    private final Uri ImgUri = Uri.parse("content://media/external/audio/albumart");
    private long mImgId;

    private Realm mRealm;
    private RealmHelper mRealmHelper;

    //private OnClickPlaylistListener mListener;

    public ImageButton mBtnDrag;
    private ImageView mImgAlbum;
    private TextView mTxtTitle;
    private TextView mTxtArtist;
    public ImageButton mImgMoreOption;

    public TextView mDuration;
    public TextView mTxtFooterTitle;
    public TextView mTxtFooterArtist;
    public ImageView mImgFooter;


    private int mPosition;

    public Context mContext;
    private List<Playlist> mPlaylists = new ArrayList<>();

    private OnClickPlaylistListener mListener;

    public PlaylistViewHolder(final Context context, final View itemView, List<Playlist> playlist) {
        super(itemView);
        Log.i("TEST", "TEST MENU::: PlaylistViewHolder");
        mContext = context;
        mPlaylists = playlist;

        Realm.init(itemView.getContext());
        mRealm = Realm.getDefaultInstance();
        mRealmHelper = new RealmHelper(mRealm);

        mBtnDrag = (ImageButton) itemView.findViewById(R.id.btn_drag);
        mImgAlbum = (ImageView) itemView.findViewById(R.id.img_album);
        mTxtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        mTxtArtist = (TextView) itemView.findViewById(R.id.txt_artist);
        mImgMoreOption = (ImageButton) itemView.findViewById(R.id.btn_moreOption);

        mDuration = (TextView) ((MusicPlayerActivity) mContext).findViewById(R.id.txt_durationTime);
        mTxtFooterTitle = (TextView) ((MusicPlayerActivity) mContext).findViewById(R.id.txt_footer_title);
        mTxtFooterArtist = (TextView) ((MusicPlayerActivity) mContext).findViewById(R.id.txt_footer_artist);
        mImgFooter = (ImageView) ((MusicPlayerActivity) mContext).findViewById(R.id.img_footer);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Long> musicIds = getMusicId(mPlaylists);

                MusicApplication.getInstance().getServiceInterface().setPlaylist(musicIds);
                MusicApplication.getInstance().getServiceInterface().play(mPosition);

                //mListener.onClickPlaylist();
            }
        });
    }

    public void setPlaylist(Playlist item, int position) {
        mTxtTitle.setText(item.getTitle());
        mTxtArtist.setText(item.getArtist());
        mImgId = item.getAlbumId();
        mPosition = position;

        Uri albumImgUri = ContentUris.withAppendedId(ImgUri, item.getAlbumId());
        Picasso.with(itemView.getContext()).load(albumImgUri).error(R.drawable.ic_audiotrack_black_24dp).into(mImgAlbum);
    }

    private List<Long> getMusicId(List<Playlist> playlists) {
        List<Long> musicIds = new ArrayList<>();
        for (int i = 0; i < playlists.size(); i++) {
            musicIds.add(playlists.get(i).getId());
        }
        return musicIds;
    }

    public void setOnTouch(OnClickPlaylistListener listener){
        mListener = listener;

    }
}
