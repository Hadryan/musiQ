package com.dihanov.musiq.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dihanov.musiq.R;
import com.dihanov.musiq.util.Constants;
import com.dihanov.musiq.util.KeyboardHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Dihanov on 9/16/2017.
 */

public class MainActivity extends DaggerAppCompatActivity implements MainActivityContract.View{
    private static final String search = "search for artists";
    private static final String TAG_LAST_SEARCH = "lastSearch";

    @Inject MainActivityPresenter mainActivityPresenter;

    @BindView(R.id.bird)
    TextView bird;

    @BindView(R.id.main_gridview)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.tabs) TabLayout tabLayout;

    @BindView(R.id.viewpager) ViewPager viewPager;

    private SearchView searchBar;
    private String lastSearch;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(savedInstanceState != null){
            this.lastSearch = savedInstanceState.getString(TAG_LAST_SEARCH);
        }

        initGridView();
        initViewPager();
        initCollapsingToolbar();
        setSupportActionBar(toolbar);

        appBarLayout.setExpanded(true);
        mainActivityPresenter.takeView(this);
        mainActivityPresenter.setBackdropImageChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.LAST_SEARCH, searchBar.getQuery().toString());
    }

    private void initViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //need to call this as calligraphy doesnt change the fonts of the tablayout, since there is no exposes property,
        //in the xml, and the fonts are set programatically
        Constants.changeTabsFont(this, tabLayout);
    }

    private void initCollapsingToolbar() {
        Constants.setToolbarFont(collapsingToolbar, this);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    searchBar.setVisibility(View.INVISIBLE);
                    collapsingToolbar.setTitle(" ");

                    isShow = true;
                } else if (isShow) {
                    searchBar.setVisibility(View.VISIBLE);
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityPresenter.leaveView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        this.searchBar = (SearchView) myActionMenuItem.getActionView();

        if(this.lastSearch != null){
            this.searchBar.setQuery(lastSearch, true);
        }

        return true;
    }

    @Override
    public SearchView getSearchBar() {
        return this.searchBar;
    }


    @Override
    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    @Override
    public void showProgressBar() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    public View getBirdIcon() {
        return this.bird;
    }

    @Override
    public void hideKeyboard() {
        KeyboardHelper.hideKeyboard(this);
    }

    private void initGridView() {
        RecyclerView.LayoutManager layoutManager = new CustomGridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }
}
