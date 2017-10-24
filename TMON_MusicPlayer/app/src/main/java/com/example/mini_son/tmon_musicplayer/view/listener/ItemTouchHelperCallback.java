package com.example.mini_son.tmon_musicplayer.view.listener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by sonminhee on 2017. 7. 25..
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    ItemTouchHelperListener mItemTouchHelperListener;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener){
        Log.i("TEST", "TEST CALLBACK ::: ItemTouchHelperCallback");
        this.mItemTouchHelperListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //어느 방향으로 움직일 것인지에 대한 flag 정의
        Log.i("TEST", "TEST CALLBACK ::: getMovementFlags");
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //움직이면 어떻게 할건지 처리
        Log.i("TEST", "TEST CALLBACK ::: onMove");
        mItemTouchHelperListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
