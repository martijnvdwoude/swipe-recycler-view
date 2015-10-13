package me.mvdw.swiperecyclerview.adapter;

import android.view.ViewGroup;

import java.util.Observable;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public class FrontViewTranslationObservable extends Observable {
    public void frontViewTranslationChanged(ViewGroup frontView){
        setChanged();
        notifyObservers(frontView);
    }
}