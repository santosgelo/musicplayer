package gelo.com.musicplayer.ui.view.music;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import gelo.com.musicplayer.AndroidApplication;
import gelo.com.musicplayer.R;
import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.mvp.mosby.BaseActivity;
import gelo.com.musicplayer.ui.adapter.RecyclerViewItemClickListener;
import gelo.com.musicplayer.ui.adapter.SongsAdapter;
import gelo.com.musicplayer.ui.adapter.SongsHeaderAdapter;
import gelo.com.musicplayer.ui.presenter.MusicPresenter;
import gelo.com.musicplayer.ui.view.Navigator;
import timber.log.Timber;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

public class MusicActivity extends BaseActivity<MusicView,MusicPresenter> implements MusicView, SwipeRefreshLayout.OnRefreshListener, RecyclerViewItemClickListener.OnItemClickListener, View.OnClickListener, SearchView.OnCloseListener, SearchView.OnQueryTextListener {


    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.contentView)
    SwipeRefreshLayout contentView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.button_filter)
    ImageButton buttonFilter;
    @Bind(R.id.button_search)
    ImageButton buttonSearch;
    @Bind(R.id.searchview)
    SearchView searchView;

    MusicComponent component;
    SongsHeaderAdapter adapter;
    private boolean searchMode;

    @Override
    protected void createComponent() {
        component = DaggerMusicComponent.builder()
                .applicationComponent(((AndroidApplication)getApplication()).getComponent())
                .build();
    }

    @NonNull
    @Override
    public MusicPresenter createPresenter() {
        return component.presenter();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        setSupportActionBar(toolbar);
        Timber.tag("MusicActivity");
        Timber.d("onCreate");
        adapter = component.adapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);

        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, this));
        contentView.setOnRefreshListener(this);
        loadData(false);
        buttonFilter.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
        //presenter.startMediaPlaybackService(this);
    }

    @Override
    public void onStart() {
        Timber.d("onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Timber.d("onStop");
        super.onStop();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MusicView impl

    @Override
    public void showSongs(List<Song> songList) {
        Timber.d("showSongs");
        adapter.setSongList(songList);
    }

    @Override
    public void hideLoadingIndicator() {
        contentView.setRefreshing(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UI

    @Override
    public void onRefresh() {
        if(!searchMode){
            loadData(true);
        } else {
            contentView.setRefreshing(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void loadData(boolean isRefresh) {
        Timber.d("loading data");
        Toast.makeText(this, "Loading Songs", Toast.LENGTH_SHORT).show();
        getPresenter().loadSongList();
        if (!isRefresh) {
            contentView.setRefreshing(true);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Timber.d("onItemClick position - "+position);
        Song song = adapter.getItem(position);
        getPresenter().playSong(song);
    }

    @Override
    public void onClick(View v) {
        Timber.d("onClick");
        switch(v.getId()){
            case R.id.button_filter:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.select_sorting);
                builder.setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        int selectedSortingType = SongsAdapter.ENUM_SORT_BY_TITLE;
                        switch (which) {
                            case 1:
                                selectedSortingType = SongsAdapter.ENUM_SORT_BY_ARTIST;
                                Timber.d("Sort songs by Artist");
                                break;
                            case 2:
                                selectedSortingType = SongsAdapter.ENUM_SORT_BY_ALBUM;
                                Timber.d("Sort songs by Album");
                                break;
                            default:
                                selectedSortingType = SongsAdapter.ENUM_SORT_BY_TITLE;
                                Timber.d("Sort songs by Title");
                                break;

                        }
                        adapter.setSortType(selectedSortingType);
                    }
                });
                builder.show();
                break;
            case R.id.button_search:
                Timber.d("Search button selected");
                //we need to show the a textinput and close button
                toolbar.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
                searchMode = true;
                break;
        }
    }

    @Override
    public boolean onClose() {
        Timber.d("Search closed");
        searchView.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        loadData(true);
        searchMode = false;
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Timber.d("Perform search");
        presenter.performSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
