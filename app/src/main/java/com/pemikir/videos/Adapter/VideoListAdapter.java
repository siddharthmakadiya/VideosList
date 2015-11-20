package com.pemikir.videos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.pemikir.videos.R;
import com.pemikir.videos.VideoUtils.Utils;
import com.pemikir.videos.View.PlayVideoActivity;


import java.io.File;
import java.util.List;

import me.everything.providers.android.media.MediaProvider;
import me.everything.providers.android.media.Video;
import me.everything.providers.core.Data;

/**
 * Created by iconflux-android on 10/19/2015.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder> {

    List<Video> mVideolist;
    Context context;


    public VideoListAdapter(List<Video> Videolist, Context ctx) {
        this.mVideolist = Videolist;
        this.context = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_row_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Video video = mVideolist.get(position);

        if (video.title.toString().length() > 45) {
            holder.title.setText(video.title.toString().substring(0, 45) + "...");

        } else {
            holder.title.setText(video.title.toString());
        }
        holder.descrip.setText(video.displayName);
        try {
            Log.i("VideoPath", "=>" + Utils.getVideoUriFromVideoId(video.id + ""));

            Glide.with(context).load(Utils.getVideoUriFromVideoId(video.id + "")).into(holder.thumbnail);

//            Picasso.with(context).load(Utils.getVideoUriFromVideoId(video.id + "")).into(holder.thumbnail);
//            holder.thumbnail.setImageBitmap(Utils.getBitmapFromVideoUrl(Utils.getVideoPathFromVideoId(context,video.id + "")));
        } catch (Throwable throwable) {

            throwable.printStackTrace();
        }

        holder.swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PlayVideoActivity.class).putExtra(PlayVideoActivity.VIDEO_URL, Utils.getVideoPathFromVideoId(context, video.id + "")));
            }
        });

        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Please Waite Take Some Time", Toast.LENGTH_SHORT).show();

                shareVideo(video.title, Utils.getVideoPathFromVideoId(context, video.id + ""));
            }
        });

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("VideoPath", "=>" + Utils.getVideoUriFromVideoId(video.id + ""));

                if (Utils.showAlert(context, "ARE YOU SURE ?")) {
                    context.getContentResolver().delete(Utils.getVideoUriFromVideoId(video.id + ""), null, null);
                    Toast.makeText(context, "Video Deleted", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            }
        });

    }

    public void shareVideo(final String title, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        context.startActivity(Intent.createChooser(shareIntent,
                                context.getString(R.string.str_share_this_video)));

                    }
                });
    }


    @Override
    public int getItemCount() {
        return mVideolist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView descrip;
        ImageView thumbnail;
        RelativeLayout swipe;
        ImageView img_share, img_delete;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            descrip = (TextView) itemView.findViewById(R.id.details);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumb);
            swipe = (RelativeLayout) itemView.findViewById(R.id.swipe);
            img_share = (ImageView) itemView.findViewById(R.id.img_share);
            img_delete = (ImageView) itemView.findViewById(R.id.img_delete);

//            swipe = (SwipeLayout) itemView.findViewById(R.id.swipe);
//            swipe.setShowMode(SwipeLayout.ShowMode.PullOut);
//            View starBottView = swipe.findViewById(R.id.swipe_item);
//            swipe.addDrag(SwipeLayout.DragEdge.Right, swipe.findViewById(R.id.swipe_item));

        }
    }


}
