package com.pemikir.videos.Fragment;


import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class InternalVideoListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    View mFragmentView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InternalVideoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // TODO: Change Adapter to display your content
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
        Data<Video> videolistExternal = provider.getVideos(MediaProvider.Storage.INTERNAL);
        List<Video> VideoList = videolistExternal.getList();

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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
