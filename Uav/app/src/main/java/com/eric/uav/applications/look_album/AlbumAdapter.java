package com.eric.uav.applications.look_album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.eric.uav.R;
import com.eric.uav.applications.look_album.image_viewer.ImageViewerActivity;
import com.eric.uav.applications.look_album.video_viewer.VideoViewerActivity;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.GridViewHolder> {
    private List<ConvertFile> list;
    private Context context;

    public AlbumAdapter(List<ConvertFile> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GridViewHolder(LayoutInflater.from(this.context).inflate(R.layout.album_recycle_item, viewGroup, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, int i) {
        if (list.get(i).getFile().getAbsolutePath().endsWith(".mp4")) {
            gridViewHolder.videoView.setVideoPath(list.get(i).getFile().getAbsolutePath());
            gridViewHolder.videoView.setBackgroundResource(R.drawable.video_scale);
            // 设置imageView宽高为0
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = 0;
            layoutParams.width = 0;
            gridViewHolder.imageView.setLayoutParams(layoutParams);

            // 为视频设置点击事件
            gridViewHolder.videoView.setOnTouchListener((view, motionEvent) -> {
                Intent intent = new Intent(context, VideoViewerActivity.class);
                DataTransform.videoSrc = list.get(i).getFile().getAbsolutePath();
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0, 0);
                return false;
            });
        } else {
            // 设置图片地址
            gridViewHolder.imageView.setImageBitmap(list.get(i).getBitmap());
            gridViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER);

            // 为图片设置点击事件
            gridViewHolder.imageView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ImageViewerActivity.class);
                DataTransform.imageBitmap = list.get(i).getBitmap();
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(0, 0);
            });
        }
    }

    /**
     * 总共多少行
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int getItemCount() {
        return list.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private VideoView videoView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.album_image);
            videoView = itemView.findViewById(R.id.album_video);
            LinearLayout linearLayout = itemView.findViewById(R.id.wrap_item);

            // 根据不同的手机设置每一项不同的宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            // 获取屏幕宽度
            int width = dm.widthPixels;
            // 屏幕密度（0.75 / 1.0 / 1.5）
            float density = dm.density;
            // 计算每一项的高度
            int itemWidth = (int) ((width - 20 * density) / 4 - 2.5 * density);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.width = itemWidth;
            layoutParams1.height = itemWidth;
            linearLayout.setLayoutParams(layoutParams1);
        }
    }
}
