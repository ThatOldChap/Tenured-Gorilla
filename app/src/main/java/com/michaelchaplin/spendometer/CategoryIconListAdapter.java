package com.michaelchaplin.spendometer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CategoryIconListAdapter extends RecyclerViewCursorAdapter<CategoryIconViewHolder> {

    private RecyclerViewItemTouchListener mCategoryTouchListener;

    public CategoryIconListAdapter(Context context, Cursor cursor, RecyclerViewItemTouchListener categoryTouchListener) {

        // Invokes the superclass methods for the CategoryIconListAdapter
        super(context, cursor);

        // Passes a CategoryTouchListener into the adapter
        this.mCategoryTouchListener = categoryTouchListener;
    }

    @Override
    public CategoryIconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the category icon layout items
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_category_icons, parent, false);

        // Return a new CategoryViewHolder with a TouchListener attached
        return new CategoryIconViewHolder(view, mCategoryTouchListener);
    }

    @Override
    public void onBindViewHolder(CategoryIconViewHolder viewHolder, Cursor cursor) {

        // Verifies whether the cursor was moved to the requested position of ViewHolder being bound
        cursor.moveToPosition(cursor.getPosition());

        // Sets the cursor data into the CategoryIconViewHolder
        viewHolder.setCursorData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

}
