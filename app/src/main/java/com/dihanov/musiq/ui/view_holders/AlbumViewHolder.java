package com.dihanov.musiq.ui.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dihanov.musiq.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dimitar.dihanov on 11/2/2017.
 */

public class AlbumViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.album_title)
    TextView title;

    @BindView(R.id.album_song_count)
    TextView count;

    @BindView(R.id.album_thumbnail)
    ImageView thumbnail;

    public AlbumViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getCount() {
        return count;
    }

    public ImageView getThumbnail() {
        return thumbnail;
    }
}
