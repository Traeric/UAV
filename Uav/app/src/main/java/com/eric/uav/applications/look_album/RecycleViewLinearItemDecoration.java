package com.eric.uav.applications.look_album;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecycleViewLinearItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public RecycleViewLinearItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }
}
