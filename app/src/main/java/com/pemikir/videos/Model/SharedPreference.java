package com.pemikir.videos.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

import com.google.gson.Gson;
import com.pemikir.videos.R;
import com.pemikir.videos.VideoUtils.Utils;

import me.everything.providers.android.media.Video;


public class SharedPreference {

    public static final String PREFS_NAME = "VideoLibrary";
    public static final String FAVORITES = "Video_Favorite";

    public SharedPreference() {
        super();
    }

    public void saveWallpaperurl(Context context, String Url) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(FAVORITES, Url);
        editor.commit();
    }

    public String getWallpaperurl(Context context) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        Uri path = Uri.parse("android.resource://com.pemikir.videos/" + R.drawable.retro_black);
        return settings.getString(FAVORITES, null);
    }


    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Video> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, Video product) {
        List<Video> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Video>();
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Video product) {
        ArrayList<Video> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(product);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<Video> getFavorites(Context context) {
        SharedPreferences settings;
        List<Video> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Video[] favoriteItems = gson.fromJson(jsonFavorites,
                    Video[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Video>(favorites);
        } else
            return null;

        return (ArrayList<Video>) favorites;
    }
}