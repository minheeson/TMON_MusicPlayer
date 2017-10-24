package com.example.mini_son.tmon_musicplayer.view.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
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

import com.example.mini_son.tmon_musicplayer.R;
import com.example.mini_son.tmon_musicplayer.view.music.MusicAdapter;
import com.example.mini_son.tmon_musicplayer.view.music.typevalue.MusicViewType;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by sonminhee on 2017. 7. 24..
 */


public class SearchResultActivity extends AppCompatActivity {
    private SlidingUpPanelLayout mBottomDrawerLayout;

    private static final String TAG = "MusicPlayerActivity";
    private int LOADER_ID = 1;

    private MusicViewType viewType = MusicViewType.LIST;

    private RecyclerView mRecyclerView;
    private MusicAdapter mAdapter;
    private ImageButton mArrowBtn;
    private ImageButton mPlaylistBtn;
    private ImageButton mMoreVertBtn;

    private SearchView searchView;
    private String mSearchWord;
    private Toolbar mToolbar;

    private ImageButton mModeChangeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setSupportActionBar((Toolbar) findViewById(R.id.search_result_toolbar));
        Log.i("TEST", "TEST ORDER 3 :::");


        Intent intent = getIntent();
        mSearchWord = intent.getExtras().getString("SearchWord");

        init();
        checkAuthority();


        mAdapter = new MusicAdapter(viewType);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("TEST", "TEST MENU 2::: ");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search_result, menu);

        mToolbar = (Toolbar) findViewById(R.id.search_result_toolbar);
        mToolbar.setTitle("\'" + mSearchWord + "\' 검색 결과");
        menu.addSubMenu(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i("TEST", "TEST ORDER 1 :::");
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
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        checkAuthority();
        return super.onOptionsItemSelected(item);
    }


    private void setMusicPlayerAdapter(MusicViewType type) {
        mAdapter = new MusicAdapter(type); //0
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
                String selection = MediaStore.Audio.Media.TITLE + " like " + "\"%" + mSearchWord + "%\"";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                //selection :: 가져올 데이터를 필터링하는 정보, SQL의 WHERE절과 비슷

                Log.i("TEST", "TEST ORDER 2 :::");
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
