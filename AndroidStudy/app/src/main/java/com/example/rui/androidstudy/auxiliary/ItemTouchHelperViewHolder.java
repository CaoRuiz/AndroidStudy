package com.example.rui.androidstudy.auxiliary;

/**
 * Created by zonelue003 on 2017/6/26.
 */

public interface ItemTouchHelperViewHolder {
    /**
     * 当Item开始拖拽或者滑动的时候调用
     */
    void onItemSelected();

    /**
     * 当Item完成拖拽或者滑动的时候调用
     */
    void onItemClear();
}
