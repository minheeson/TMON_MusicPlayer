package com.example.mini_son.tmon_musicplayer.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mini_son.tmon_musicplayer.MusicAdapter;
import com.example.mini_son.tmon_musicplayer.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MusicPlayerActivity extends AppCompatActivity {
    private SlidingUpPanelLayout mBottomDrawerLayout;

    private static final String TAG = "MusicPlayerActivity";
    private int LOADER_ID = 1;
    private static final int ITEM_LIST = 0;
    private static final int ITEM_CARD = 1;
    private static int ITEM_TYPE = 0;


    private RecyclerView mRecyclerView;
    private MusicAdapter mAdapter;
    private ImageButton mArrowBtn;
    private ImageButton mPlaylistBtn;
    private ImageButton mMoreVertBtn;

    private SearchView searchView;

    private ImageButton mModeChangeBtn;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar, menu);

        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("TEST SEARCH ", "SUBMIT TEST :::" + query);
                return false;
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
            if (ITEM_TYPE == ITEM_CARD) {
                item.setIcon(R.drawable.ic_card_all_black_24dp);
                setMusicPlayer(ITEM_CARD);
            } else if (ITEM_TYPE == ITEM_LIST) {
                item.setIcon(R.drawable.ic_list_all_black_24dp);
                setMusicPlayer(ITEM_LIST);
            }
        }/*else if(id==R.id.action_search){
            Log.i("TEST ", "SEARCH CLICKED ::: ");


        }*/
        checkAuthority();
        return super.onOptionsItemSelected(item);
    }

    /*
        @Override
        public void onBackPressed() {
            if(mBottomDrawerLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                mBottomDrawerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }else {
                super.onBackPressed();
            }
        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        init();
        checkAuthority();


        mAdapter = new MusicAdapter(this, null, ITEM_LIST);
        mRecyclerView.setAdapter(mAdapter);

        panelListener();

        //목록뷰
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
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

/*

        mRealm = Realm.getDefaultInstance();
        RealmResults<PlaylistVO> playlist = getPlaylist();
        Log.i(TAG, " playlist.size ::: " + playlist.size());
        insertPlaylistData();
        Log.i(TAG, " playlist.size ::: " + playlist.size());
*/
    }

    /**
     * 재생목록 데이터 리스트 반환
     */
    /*
    private RealmResults<PlaylistVO> getPlaylist() {
        return mRealm.where(PlaylistVO.class).findAll();
    }
*/
    /**
     * 재생목록 데이터 DB 저장
     */
    /*
    private void insertPlaylistData() {
        mRealm.beginTransaction();
        PlaylistVO playlist = mRealm.createObject(PlaylistVO.class);
        playlist.setmTitle("손민히");
        playlist.setmAlbumId(235);
        playlist.setmArtist("사탕");
        mRealm.commitTransaction();
    }
*/
    public void setMusicPlayer(int itmeType) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if (itmeType == ITEM_LIST) {

            setMusicPlayerAdapter(ITEM_CARD);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

            mRecyclerView.setLayoutManager(layoutManager);
            ITEM_TYPE = ITEM_CARD;

        } else if (itmeType == ITEM_CARD) {

            setMusicPlayerAdapter(ITEM_LIST);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(layoutManager);
            ITEM_TYPE = ITEM_LIST;
        }
    }

    private void setMusicPlayerAdapter(int itemType) {
        mAdapter = new MusicAdapter(this, null, itemType); //0
        mRecyclerView.setAdapter(mAdapter);
    }


    public void panelListener() {
        onClickPanel();

        mBottomDrawerLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelCollapsed(View panel) {
                mArrowBtn.setVisibility(View.VISIBLE);
                mPlaylistBtn.setVisibility(View.GONE);
                mMoreVertBtn.setVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View panel) {
                mArrowBtn.setVisibility(View.GONE);
                mPlaylistBtn.setVisibility(View.VISIBLE);
                mMoreVertBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });
    }

    private void onClickPanel() {
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                switch (viewId) {
                    case R.id.btn_arrow:
                        Log.i("ONCLICK TEST ", "ARROW BTN :::");
                        break;
                    case R.id.btn_playlist:
                        Log.i("ONCLICK TEST ", "PLAYLIST BTN :::");
                        mPlaylistBtn.setImageResource(R.drawable.ic_playlist_play_tmon_24dp);
                        break;
                    case R.id.btn_moreVert:
                        Log.i("ONCLICK TEST ", "MOREVERT BTN :::");
                        //이거 우선은 Holder에서 복붙함
                        PopupMenu p = new PopupMenu(v.getContext(), v);
                        MenuInflater inflater = p.getMenuInflater();
                        Menu menu = p.getMenu();

                        inflater.inflate(R.menu.menu_play_option, menu);

                        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return false;
                            }
                        });
                        p.show();
                        break;
                }
            }
        };
        mArrowBtn.setOnClickListener(onClickListener);
        mPlaylistBtn.setOnClickListener(onClickListener);
        mMoreVertBtn.setOnClickListener(onClickListener);
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
                Log.i("SOOJONG", "onLoadFinishedcalled~!");
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

}
