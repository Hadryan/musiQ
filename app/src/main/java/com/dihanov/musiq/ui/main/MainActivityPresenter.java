package com.dihanov.musiq.ui.main;


import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;

import com.dihanov.musiq.interfaces.ArtistDetailsIntentShowableImpl;
import com.dihanov.musiq.interfaces.ClickableArtistViewHolder;
import com.dihanov.musiq.models.Artist;
import com.dihanov.musiq.service.LastFmApiClient;
import com.dihanov.musiq.ui.adapters.TopArtistAdapter;
import com.dihanov.musiq.util.Connectivity;
import com.dihanov.musiq.util.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Dihanov on 9/16/2017.
 */

public class MainActivityPresenter extends ArtistDetailsIntentShowableImpl implements MainActivityContract.Presenter{
    private static final String LOADING_ARTISTS = "loading this week's top artists...";
    private static final long NETWORK_CHECK_THREAD_TIMEOUT = 5000;
    private static int TOP_ARTIST_LIMIT = 6;

    private MainActivityContract.View mainActivityView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RecyclerView recyclerView;

    @Inject
    LastFmApiClient lastFmApiClient;

    @Inject
    public MainActivityPresenter() {
    }

    @Override
    public void takeView(MainActivityContract.View view) {
        this.recyclerView = view.getRecyclerView();
        this.mainActivityView = view;
    }

    @Override
    public void leaveView() {
        compositeDisposable.clear();
        this.mainActivityView = null;
    }


    @Override
    public void setBackdropImageChangeListener(MainActivity mainActivity) {
        setListenerOnFoundConnection(mainActivity);
    }

    private void loadBackdrop(MainActivity mainActivity) {
        if (Constants.isTablet(mainActivity) && Constants.getOrientation(mainActivity) == Configuration.ORIENTATION_LANDSCAPE) {
            TOP_ARTIST_LIMIT = 10;
        }

        lastFmApiClient.getLastFmApiService().chartTopArtists(TOP_ARTIST_LIMIT)
                .map(topArtistsResult -> topArtistsResult.getArtists().getArtistMatches())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Artist>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Constants.showTooltip(mainActivity, mainActivity.getBirdIcon(), LOADING_ARTISTS);
                        mainActivity.showProgressBar();
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<Artist> artists) {
                        TopArtistAdapter topArtistAdapter = new TopArtistAdapter(mainActivity, (ArrayList<Artist>) artists, MainActivityPresenter.this);
                        recyclerView.setAdapter(topArtistAdapter);
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(artists.size() - 1);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mainActivity.hideProgressBar();
                        compositeDisposable.clear();
                    }
                });
    }

    private void setListenerOnFoundConnection(MainActivity mainActivity) {
        Thread newConnThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Connectivity.isConnected(mainActivity)) {
                    try {
                        Constants.showNetworkErrorTooltip(mainActivity);
                        Thread.sleep(NETWORK_CHECK_THREAD_TIMEOUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadBackdrop(mainActivity);
                    }
                });
            }
        });

        if (!newConnThread.isAlive()) {
            newConnThread.start();
        }
    }


    @Override
    public void addOnArtistResultClickedListener(ClickableArtistViewHolder viewHolder, String artistName) {
        RxView.clicks(viewHolder.getThumbnail())
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(click -> {
                    this.showArtistDetailsIntent(artistName, mainActivityView);
                });
    }
}
