package com.dihanov.musiq.di.builders;

import com.dihanov.musiq.di.annotations.PerActivity;
import com.dihanov.musiq.di.annotations.PerFragment;
import com.dihanov.musiq.di.modules.DetailActivityModule;
import com.dihanov.musiq.di.modules.DetailFragmentModule;
import com.dihanov.musiq.di.modules.MainActivityModule;
import com.dihanov.musiq.ui.detail.DetailActivity;
import com.dihanov.musiq.ui.detail.fragment.DetailFragment;
import com.dihanov.musiq.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Dimitar Dihanov on 15.9.2017 г..
 */

@Module
public abstract class ActivityBindingModule {
    @PerActivity
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = DetailActivityModule.class)
    abstract DetailActivity bindDetailActivity();

    @PerFragment
    @ContributesAndroidInjector(modules = DetailFragmentModule.class)
    abstract DetailFragment bindDetailFragment();
}