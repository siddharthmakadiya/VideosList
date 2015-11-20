package com.pemikir.videos.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pemikir.videos.Adapter.VideoListAdapter;
import com.pemikir.videos.R;
import com.pemikir.videos.VideoUtils.Utils;

import java.util.List;

import me.everything.providers.android.media.MediaProvider;
import me.everything.providers.android.media.Video;
import me.everything.providers.core.Data;

/**
 * Created by iconflux-android on 10/19/2015.
 */
public class ExternalVideoListFragment extends Fragment {


    private View mFragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_recycle, container, false);
            init(mFragmentView);
        }
        return mFragmentView;
    }

    private void init(View view) {
        MediaProvider provider = new MediaProvider(getActivity());

        Data<Video> videolistExternal = provider.getVideos(MediaProvider.Storage.EXTERNAL);

        List<Video> VideoList = videolistExternal.getList();

     /*   for (Video video : videolistExternal.getList()) {
            Log.i("VIDEOLIST", "=>" + video.id);
            Log.i("VIDEOLIST", "=>" + video.size);
            Log.i("VIDEOLIST", "=>" + video.album);
            Log.i("VIDEOLIST", "=>" + video.category);
            Log.i("VIDEOLIST", "=>" + video.dateAdded);
            Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, video.id + "");// Integer.toString(video.id));
            Log.i("VIDEOLIST", "=>" + Utils.getVideoPathFromVideoId(getActivity(), video.id + ""));
        }*/

        RecyclerView list_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        VideoListAdapter adapter = new VideoListAdapter(VideoList, getActivity());
        list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        list_view.setAdapter(adapter);

        AdView mAdView = (AdView) view.findViewById(R.id.adView);

        if (Utils.isConnectingToInternet(getActivity())) {
            AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
            mAdView.loadAd(new AdRequest.Builder().build());
        }
    }
}
