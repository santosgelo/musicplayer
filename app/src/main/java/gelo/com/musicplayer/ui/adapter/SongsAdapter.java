package gelo.com.musicplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import gelo.com.musicplayer.R;
import gelo.com.musicplayer.data.entity.Song;
import gelo.com.musicplayer.data.service.SongsProvider;
import gelo.com.musicplayer.mvp.mosby.BaseRxPresenter;
import gelo.com.musicplayer.util.CircleTransform;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class SongsAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txt_song_title)  TextView txtSongTitle;
        @Bind(R.id.txt_song_artist)   TextView txtSongArtist;
        @Bind(R.id.img_disc_image)   ImageView imgAlbumArt;
        View rootView;

        SongViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, rootView);
          /*  txtSongTitle = (TextView) itemView.findViewById(R.id.txt_song_title);
            txtSongArtist = (TextView) itemView.findViewById(R.id.txt_song_artist);
            imgAlbumArt = (ImageView) itemView.findViewById(R.id.img_disc_image);*/
        }

        public void bind(Song song) {
            if (song.getTitle() != null) {
                txtSongTitle.setText(song.getTitle());
                txtSongArtist.setText(song.getArtist());
                //imgAlbumArt.setImageBitmap(song.getAlbumArt());
                if (song.getAlbumArtUri() != null) {
                    Picasso.with(rootView.getContext()).load(new File(song.getAlbumArtUri())).transform(new CircleTransform()).into(imgAlbumArt);
                } else {
                    if(song.getAlbumArt()!=null){
                        imgAlbumArt.setImageBitmap(song.getAlbumArt());
                    } else {
                        imgAlbumArt.setImageResource(R.mipmap.ic_launcher);
                    }
                }
                txtSongArtist.setVisibility(View.VISIBLE);
                txtSongTitle.setVisibility(View.VISIBLE);
                imgAlbumArt.setVisibility(View.VISIBLE);
            } else {
                //we hide the controls
                txtSongArtist.setVisibility(View.INVISIBLE);
                txtSongTitle.setVisibility(View.INVISIBLE);
                imgAlbumArt.setVisibility(View.INVISIBLE);
            }
        }
    }
    public static final int ENUM_SORT_BY_TITLE = 0;
    public static final int ENUM_SORT_BY_ARTIST = 1;
    public static final int ENUM_SORT_BY_ALBUM = 2;
    public static final int ENUM_SORT_BY_FAVORITE = 3;

    private List<Song> lstSongs;

    public SongsAdapter(List<Song> songs) {
        this.lstSongs = songs;
    }

    public void setSongList(List<Song> songs){
        this.lstSongs = songs;
        clear();
        addAll(songs);
    }

    private List<Song> items = new ArrayList<Song>();

    public SongsAdapter() {
        setHasStableIds(true);
    }

    public void add(Song object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Song object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Song> collection) {
        if (collection != null) {
            items.addAll(collection);
            setSortType(mSortType);
            notifyDataSetChanged();
        }
    }

    public void addAll(Song... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(Song object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public Song getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int mSortType = SongsAdapter.ENUM_SORT_BY_TITLE;;

    public void setSortType(int sortType){
        mSortType = sortType;
        switch (mSortType){
            case 0:
                items = SongsProvider.sortSongsByTitle(items);
                break;
            case SongsAdapter.ENUM_SORT_BY_ARTIST:
                items = SongsProvider.sortSongsByArtist(items);
                break;
            case SongsAdapter.ENUM_SORT_BY_ALBUM:
                items = SongsProvider.sortSongsByAlbum(items);
                break;
        }
        notifyDataSetChanged();
    }

    public int getSortType(){
        return mSortType;
    }

}
