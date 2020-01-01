package com.eric.uav.applications.look_album;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing; //间隔

    public RecyclerViewSpacesItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spacing;
        outRect.top = spacing;
    }
}

