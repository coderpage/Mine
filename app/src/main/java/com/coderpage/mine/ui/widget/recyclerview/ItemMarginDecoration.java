/*
 * Copyright (c) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.coderpage.mine.ui.widget.recyclerview;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A {@link RecyclerView.ItemDecoration} for adding margins to items in a {@code RecyclerView}.
 */
public class ItemMarginDecoration extends RecyclerView.ItemDecoration {

    private Rect mCommonOffset = new Rect();
    private Rect mFirstItemOffset = null;
    private Rect mLastItemOffset = null;

    public ItemMarginDecoration(int leftPx, int topPx, int rightPx, int bottomPx) {
        mCommonOffset.left = leftPx;
        mCommonOffset.top = topPx;
        mCommonOffset.right = rightPx;
        mCommonOffset.bottom = bottomPx;
    }

    public void setFirstItemOffset(int leftPx, int topPx, int rightPx, int bottomPx) {
        mFirstItemOffset = new Rect(leftPx, topPx, rightPx, bottomPx);
    }

    public void setLastItemOffset(int leftPx, int topPx, int rightPx, int bottomPx) {
        mLastItemOffset = new Rect(leftPx, topPx, rightPx, bottomPx);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        if (mFirstItemOffset != null && position == 0) {
            outRect.set(mFirstItemOffset);
            return;
        }

        if (mLastItemOffset != null && position == state.getItemCount() - 1) {
            outRect.set(mLastItemOffset);
            return;
        }

        outRect.set(mCommonOffset);
    }

}
