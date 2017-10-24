package com.example.mini_son.tmon_musicplayer.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.mini_son.tmon_musicplayer.view.listener.IOnShowingListener;
import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.view.activity.MusicPlayerActivity;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.RepeatStatus;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.ShuffleStatus;
import com.squareup.picasso.Picasso;

/**
 * Created by mini_son on 2017-07-28.
 */

public class AlbumFragment extends Fragment {

    private ImageButton mBtnShuffle;
    private ImageButton mBtnRepeat;

    public IOnShowingListener listener;
    public ImageView mImgFooter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (IOnShowingListener) context;
        } catch (Exception e) {
        }
    }

    public AlbumFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TEST", "TEST FRAGMENT ::: AlbumFragment :: onCreateView()");

        //MusicPlayerActivity.shuffleStatus = ShuffleStatus.SHUFFLE_OFF;
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        mImgFooter = (ImageView) rootView.findViewById(R.id.album_art_viewpager);
        mBtnRepeat = (ImageButton) rootView.findViewById(R.id.btn_repeat);
        mBtnShuffle = (ImageButton) rootView.findViewById(R.id.btn_shuffle);

        if (MusicPlayerActivity.shuffleStatus == ShuffleStatus.SHUFFLE_OFF) { //셔플켜고
            Log.i("TEST", "TEST SHUFFLE ::: AlbumFragment :: OFF");
            mBtnShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
        } else if (MusicPlayerActivity.shuffleStatus == ShuffleStatus.SHUFFLE_ON) {
            Log.i("TEST", "TEST SHUFFLE ::: AlbumFragment :: ON");
            mBtnShuffle.setImageResource(R.drawable.ic_shuffle_tmon_24dp);
        }

        if(MusicPlayerActivity.repeatStatus == RepeatStatus.REPEAT_OFF){
            mBtnRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
        }else if(MusicPlayerActivity.repeatStatus == RepeatStatus.REPEAT_ON){
            mBtnRepeat.setImageResource(R.drawable.ic_repeat_tmon_24dp);
        }

        mBtnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TEST", "TEST FRAGMENT ::: AlbumFragment :: mBtnRepeat");
                if (MusicPlayerActivity.repeatStatus == RepeatStatus.REPEAT_OFF) {
                    Log.i("TEST", "TEST REPEAT ::: AlbumFragment :: OFF");
                    MusicPlayerActivity.repeatStatus = RepeatStatus.REPEAT_ON;
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_tmon_24dp);
                } else if (MusicPlayerActivity.repeatStatus == RepeatStatus.REPEAT_ON) {
                    Log.i("TEST", "TEST REPEAT ::: AlbumFragment :: ON");
                    MusicPlayerActivity.repeatStatus = RepeatStatus.REPEAT_OFF;
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                }
            }
        });

        mBtnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TEST", "TEST FRAGMENT ::: AlbumFragment :: mBtnShuffle");
                if (MusicPlayerActivity.shuffleStatus == ShuffleStatus.SHUFFLE_OFF) {
                    Log.i("TEST", "TEST FRAGMENT ::: AlbumFragment :: mBtnShuffle :: ON");
                    MusicPlayerActivity.shuffleStatus = ShuffleStatus.SHUFFLE_ON;
                    mBtnShuffle.setImageResource(R.drawable.ic_shuffle_tmon_24dp);
                } else if (MusicPlayerActivity.shuffleStatus == ShuffleStatus.SHUFFLE_ON) {
                    Log.i("TEST", "TEST FRAGMENT ::: AlbumFragment :: mBtnShuffle :: OFF");
                    MusicPlayerActivity.shuffleStatus = ShuffleStatus.SHUFFLE_OFF;
                    mBtnShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null)
            listener.isShowing();
    }

    @Override
    public boolean getUserVisibleHint() {
        android.util.Log.v("", "TEST TEST getUserVisibleHint");
        return super.getUserVisibleHint();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        android.util.Log.v("", "TEST TEST setUserVisibleHint");
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void setImgFooter(String uri) {
        Log.i("TEST", "TEST TEST MENU::: setImgFooter ");
        if (mImgFooter != null)
            Picasso.with(getActivity()).load(Uri.parse(uri)).error(R.drawable.ic_audiotrack_black_24dp).into(mImgFooter);
    }

}
