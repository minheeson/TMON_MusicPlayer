package com.example.mini_son.tmon_musicplayer.view.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.mini_son.tmon_musicplayer.view.listener.IOnShowingListener;
import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.model.BroadcastActions;
import com.example.mini_son.tmon_musicplayer.model.MusicItem;
import com.example.mini_son.tmon_musicplayer.MusicApplication;
import com.example.mini_son.tmon_musicplayer.view.fragment.AlbumFragment;
import com.example.mini_son.tmon_musicplayer.view.fragment.PlaylistFragment;
import com.example.mini_son.tmon_musicplayer.view.listener.OnClickPlaylistListener;
import com.example.mini_son.tmon_musicplayer.view.music.MusicAdapter;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.MusicViewType;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.RepeatStatus;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.ShuffleStatus;
import com.example.mini_son.tmon_musicplayer.view.playlist.typevalue.PlaylistStatus;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicPlayerActivity extends AppCompatActivity implements IOnShowingListener,OnClickPlaylistListener {

    private final String ALBUM_FRAGMENT = "ALBUM_FRAGMENT";
    private final String PLAYLIST_FRAGMENT = "PLAYLIST_FRAGMENT";

    private SlidingUpPanelLayout mBottomDrawerLayout;
    public static ShuffleStatus shuffleStatus = ShuffleStatus.SHUFFLE_OFF;
    public static RepeatStatus repeatStatus = RepeatStatus.REPEAT_OFF;

    private static final String TAG = "MusicPlayerActivity";
    private int LOADER_ID = 1;

    private MusicViewType viewType = MusicViewType.LIST;

    private PlaylistStatus playlistStatus = PlaylistStatus.OFF;
    private RecyclerView mRecyclerView;

    private MusicAdapter mAdapter;
    private ImageButton mArrowBtn;

    private ImageButton mPlaylistBtn;
    private ImageButton mMoreVertBtn;
    private SearchView searchView;

    private ImageButton mSkipPreiviousBtn;

    private ImageButton mPlayCircleBtn;
    private ImageButton mSkipNextBtn;

    private ImageView mImgFooter;
    private TextView mTxtFooterTitle;
    private TextView mTxtFooterArtist;
    private TextView mPlaylingTime;
    private TextView mDuration;

    private String mAlbumUri;

    private FragmentManager fragmentManager;
    private AlbumFragment albumFragment;

    private SeekBar mSeekbar;

    private MediaPlayer mMediaPlayer;

    private Handler handler = new Handler();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TEST", "TEST BROADCAST RECEIVER " + intent.getAction());
            updateUI();
            //updateBtnUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        init();
        checkAuthority();

        mAdapter = new MusicAdapter(viewType);
        mRecyclerView.setAdapter(mAdapter);

        panelListener();
        //목록뷰
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        registerBroadcast();
        updateUI();
        //isShowing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    /**
     * 데이터 초기화
     */
    private void init() {
        mPlaylistBtn = (ImageButton) findViewById(R.id.btn_playlist);
        mMoreVertBtn = (ImageButton) findViewById(R.id.btn_moreVert);
        mArrowBtn = (ImageButton) findViewById(R.id.btn_arrow);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBottomDrawerLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mPlayCircleBtn = (ImageButton) findViewById(R.id.btn_playCircle);
        mSkipNextBtn = (ImageButton) findViewById(R.id.btn_skipNext);
        mSkipPreiviousBtn = (ImageButton) findViewById(R.id.btn_skipPrevious);

        mImgFooter = (ImageView) findViewById(R.id.img_footer);
        mTxtFooterTitle = (TextView) findViewById(R.id.txt_footer_title);
        mTxtFooterArtist = (TextView) findViewById(R.id.txt_footer_artist);
        mPlaylingTime = (TextView) findViewById(R.id.txt_nowPlayingTime);
        mDuration = (TextView) findViewById(R.id.txt_durationTime);

        mSeekbar = (SeekBar) findViewById(R.id.bar_duration);

        albumFragment = new AlbumFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("TAG", "You queried for=" + query); // Here it prints correct query input. but doesnot starting the new activity.
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("SearchWord", query);
                startActivity(intent);
                menu.findItem(R.id.action_search).collapseActionView();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_bar) {
            LinearLayoutManager llm = null;
            switch (viewType) {
                case LIST:
                    item.setIcon(R.drawable.ic_list_all_black_24dp);
                    setMusicPlayerAdapter(MusicViewType.CARD);
                    llm = new GridLayoutManager(this, 2);
                    mRecyclerView.setLayoutManager(llm);
                    viewType = MusicViewType.CARD;
                    break;
                case CARD:
                    item.setIcon(R.drawable.ic_card_all_black_24dp);
                    setMusicPlayerAdapter(MusicViewType.LIST);
                    llm = new LinearLayoutManager(this);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    viewType = MusicViewType.LIST;
                    break;
            }
            mRecyclerView.setLayoutManager(llm);
        }
        checkAuthority();
        return super.onOptionsItemSelected(item);
    }


    private void setMusicPlayerAdapter(MusicViewType type) {
        mAdapter = new MusicAdapter(type);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void panelListener() {
        setDefaultFragment();
        mBottomDrawerLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("TEST", "TEST STRANGER ::: sliding");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                mBottomDrawerLayout.setDragView(panel);
                Log.i("TEST", "TEST STRANGER ::: collapsed");
                //arrow btn 활성화 및 비활성화
                mArrowBtn.setVisibility(View.VISIBLE);
                mPlaylistBtn.setVisibility(View.GONE);
                mMoreVertBtn.setVisibility(View.GONE);

                mArrowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("TEST", "TEST MUSIC PLAY ::: mArrowBtn CLICKED");
                        MusicApplication.getInstance().getServiceInterface().togglePlay();
                    }
                });
            }

            @Override
            public void onPanelExpanded(final View panel) {
                //setDefaultFragment();
                mArrowBtn.setVisibility(View.GONE);
                mPlaylistBtn.setVisibility(View.VISIBLE);
                mMoreVertBtn.setVisibility(View.VISIBLE);
                isShowing();
                mPlaylistBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("TEST", "TEST STRANGER ::: touch");
                        switch (playlistStatus) {
                            case OFF: //현재상태
                                Log.i("TEST", "TEST STRANGER ::: toOpen");
                                mBottomDrawerLayout.setDragView(R.id.footer);
                                setPlaylistFragment();
                                mPlaylistBtn.setImageResource(R.drawable.ic_playlist_play_tmon_24dp);
                                playlistStatus = PlaylistStatus.ON;
                                break;
                            case ON:
                                Log.i("TEST", "TEST STRANGER ::: toClose");
                                mPlaylistBtn.setImageResource(R.drawable.ic_playlist_play_black_24dp);
                                setAlbumFragment();
                                playlistStatus = PlaylistStatus.OFF;
                                break;
                        }
                    }
                });

                mSkipPreiviousBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("TEST", "TEST MUSIC PLAY ::: mSkipPreiviousBtn CLICKED");
                        MusicApplication.getInstance().getServiceInterface().rewind();
                    }
                });

                mPlayCircleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("TEST", "TEST MUSIC PLAY ::: mPlayCircleBtn CLICKED");
                        MusicApplication.getInstance().getServiceInterface().togglePlay();
                    }
                });

                mSkipNextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("TEST", "TEST MUSIC PLAY ::: mSkipNextBtn CLICKED");
                        MusicApplication.getInstance().getServiceInterface().forward();
                    }
                });

            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });
    }

    //TODO ::
    private void setDefaultFragment() {
        mPlaylistBtn.setImageResource(R.drawable.ic_playlist_play_black_24dp);
        FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_place, new AlbumFragment());
        fragmentTransaction.commit();
    }

    private void setPlaylistFragment() {
        Log.i("TEST", "TEST FRAGMENT ::: setPlaylistFragment() ");
        Fragment playlistFragment = new PlaylistFragment();
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, playlistFragment, PLAYLIST_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void setAlbumFragment() {
        fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
        fragmentTransaction2.replace(R.id.fragment_place, albumFragment, ALBUM_FRAGMENT);
        fragmentTransaction2.commit();
    }


    public void checkAuthority() {
        //OS가 Marshmallow 이상일 경우 권한 체크를 해야함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                //READ_EXTERNAL_STORAGE에 대한 권한이 있음
                getMusicList();
            }
        } else {
            //OS가 Marshmallow 이전일 경우 권한체크를 하지 않음
            getMusicList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //READ_EXTERNAL_STORAGE에 대한 권한 획득
            getMusicList();
        }
    }

    /**
     * MediaStore에서 뽑고 싶은 것만 뽑아 온 뒤
     */
    private void getMusicList() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                String selection = MediaStore.Audio.Media.IS_MUSIC + " =1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //selection :: 가져올 데이터를 필터링하는 정보, SQL의 WHERE절과 비슷
                return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data != null && data.getCount() > 0) {
                    while (data.moveToNext()) {
                        Log.i(TAG, "TITLE : " + data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    }
                }
                //로딩이 끝난후 adapter에 Cursor를 설정
                mAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                //Loader가 리셋되었을 때 호출, 기존 데이터를 해제
                mAdapter.swapCursor(null);
            }
        });
    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast() {
        unregisterReceiver(mBroadcastReceiver);
    }

    //TODO :: 이거 따로 빼기
    public void updateUI() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: ACTIVITY ::: update UI ");
        updateBtnUI();


        final MusicItem musicItem = MusicApplication.getInstance().getServiceInterface().getMusicItem();
        mMediaPlayer = MusicApplication.getInstance().getServiceInterface().getMusicPlayer();

        if (musicItem != null) {
            Log.i("TEST", "TEST ORDER MUSIC PLAY ::: ACTIVITY ::: 현재 재생 곡 :: " + musicItem.mTitle);
            Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), musicItem.mAlbumId);
            mAlbumUri = albumUri.toString();
            Picasso.with(getApplicationContext()).load(albumUri).error(R.drawable.ic_audiotrack_black_24dp).into(mImgFooter);
            mTxtFooterTitle.setText(musicItem.mTitle);
            mTxtFooterArtist.setText(musicItem.mArtist);
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            mDuration.setText(format.format(new Date(musicItem.mDuration)));

            mSeekbar.setMax((int) musicItem.mDuration);
            mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.i("TEST", "TEST SEEK MUSIC PLAY ::: seekbar ::: onProgressChanged ");
                    if (fromUser) {
                        mMediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.i("TEST", "TEST SEEK MUSIC PLAY ::: seekbar ::: onStartTrackingTouch ");
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.i("TEST", "TEST SEEK MUSIC PLAY ::: seekbar ::: onStopTrackingTouch ");
                }
            });
            handler.postDelayed(updateSeekBarTime, 100);
            isShowing();
        }
    }

    public void updateBtnUI() {
        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            Log.i("TEST", "TEST ORDER  MUSIC PLAY ::: ACTIVITY ::: update UI :: now playing");
            mPlayCircleBtn.setImageResource(R.drawable.ic_pause_black_24dp);
            mArrowBtn.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            Log.i("TEST", "TEST ORDER  MUSIC PLAY ::: ACTIVITY ::: update UI :: not playing");
            mPlayCircleBtn.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            mArrowBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            long currentDuration = mMediaPlayer.getCurrentPosition();
            Log.i("TEST", "TEST DURATION ::: " + currentDuration);
            mSeekbar.setProgress((int) currentDuration);
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            mPlaylingTime.setText(format.format(new Date(currentDuration)));
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void isShowing() {
        Log.i("TEST", "TESTTEST MUSIC PLAY ::: isShowing ");
        albumFragment.setImgFooter(mAlbumUri);
    }

    @Override
    public void onClickPlaylist() {
        Log.i("TEST","TEST myListener ::: ");
        //updateUI();
    }
}
