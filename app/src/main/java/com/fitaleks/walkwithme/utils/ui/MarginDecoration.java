package com.fitaleks.walkwithme.utils.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fitaleks.walkwithme.R;

/**
 * Created by alexander on 13.05.16.
 */
public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;
    private boolean withTopMargin;

    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.list_item_vertical_space);
        withTopMargin = true;
    }

    public MarginDecoration(Context context, boolean withTop) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.list_item_vertical_space);
        withTop = withTop;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, withTopMargin ? margin : 0, 0, margin); // margins only on top and bottom
    }

}
