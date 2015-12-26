package gelo.com.musicplayer.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;

import javax.inject.Inject;

import gelo.com.musicplayer.R;

public class SongsHeaderAdapter extends SongsAdapter<SongsAdapter.SongViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {


    @Inject
    public SongsHeaderAdapter(){super();}
    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View songView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_music_disc_info, parent, false);
        return new SongViewHolder(songView);
    }


    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        /*TextView textView = (TextView) holder.itemView;
        textView.setText(getItem(position).getTitle());*/
        holder.bind(getItem(position));
    }

    @Override
    public long getHeaderId(int position) {
        String sortWord = String.valueOf(getItem(position).getTitle().charAt(0));
        switch (getSortType()){
            case SongsAdapter.ENUM_SORT_BY_ARTIST:
                sortWord = getItem(position).getArtist();
                break;
            case SongsAdapter.ENUM_SORT_BY_ALBUM:
                sortWord = getItem(position).getAlbum();
        }
        int value =0;
        for(int i=0;i<sortWord.length();i++){
            value+=sortWord.charAt(i);
        }
            return value;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        String sortWord = String.valueOf(getItem(position).getTitle().charAt(0));
        switch (getSortType()){
            case SongsAdapter.ENUM_SORT_BY_ARTIST:
                sortWord = getItem(position).getArtist();
                break;
            case SongsAdapter.ENUM_SORT_BY_ALBUM:
                sortWord = getItem(position).getAlbum();
        }
        TextView textView = (TextView) holder.itemView.findViewById(R.id.text_view_header);
        textView.setText(String.valueOf(sortWord));
        //holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

}
