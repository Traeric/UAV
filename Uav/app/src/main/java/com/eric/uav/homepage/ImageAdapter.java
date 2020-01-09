package com.eric.uav.homepage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.eric.uav.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.GridViewHolder> {
    private Context context;
    private String[] images;

    public ImageAdapter(Context context, String[] images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GridViewHolder(LayoutInflater.from(this.context).inflate(R.layout.news_image_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, int i) {
        // 设置图片地址
        Glide.with(this.context).load(images[i]).into(gridViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private LinearLayout linearLayout;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.item_image);
            linearLayout = itemView.findViewById(R.id.image_item_wrap);

            // 根据不同的手机设置每一项不同的宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            // 获取屏幕宽度
            int width = dm.widthPixels;
            // 屏幕密度（0.75 / 1.0 / 1.5）
            float density = dm.density;
            // 计算每一项的高度
            int itemWidth = (int) ((width - 20 * density) / 3 - 2.5 * density);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.width = itemWidth;
//            layoutParams1.height = itemWidth;
            linearLayout.setLayoutParams(layoutParams1);
        }
    }
}
