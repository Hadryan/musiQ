package com.dihanov.musiq.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dihanov.musiq.R;
import com.dihanov.musiq.models.Artist;
import com.dihanov.musiq.ui.main.MainActivityPresenter;
import com.dihanov.musiq.ui.view_holders.TopArtistsViewHolder;
import com.dihanov.musiq.util.Constants;

import java.util.List;

/**
 * Created by dimitar.dihanov on 9/27/2017.
 */

public class TopArtistAdapter extends RecyclerView.Adapter<TopArtistsViewHolder> {
    private MainActivityPresenter mainActivityPresenter;
    private Context context;
    private List<Artist> topArtist;

    public TopArtistAdapter(Context context, List<Artist> topArtist, MainActivityPresenter mainActivityPresenter) {
        this.context = context;
        this.topArtist = topArtist;
        this.mainActivityPresenter = mainActivityPresenter;
    }

    @Override
    public TopArtistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.top_artist_viewholder, parent, false);
        return new TopArtistsViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(TopArtistsViewHolder holder, int position) {
        Artist artist = topArtist.get(position);
        Glide.with(context)
                .load(artist.getImage().get(Constants.IMAGE_XLARGE).getText())
                .crossFade(2000)
                .into(holder.getThumbnail());
        holder.getName().setText(artist.getName().toLowerCase());
        holder.getName().setTagColor(Color.parseColor(context.getString(R.color.colorAccent)));
        holder.getName().setTagCircleRadius(10f);
        holder.getThumbnail().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityPresenter.addOnArtistResultClickedListener(holder, artist.getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.topArtist.size();
    }



}
