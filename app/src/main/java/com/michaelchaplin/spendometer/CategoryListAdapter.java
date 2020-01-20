package com.michaelchaplin.spendometer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CategoryListAdapter extends RecyclerViewCursorAdapter<CategoryViewHolder> {

    private RecyclerViewItemTouchListener mCategoryTouchListener;

    // The constructor to add a cursor and TouchListener capability into the adapter
    public CategoryListAdapter(Context context, Cursor cursor, RecyclerViewItemTouchListener categoryTouchListener) {

        // Invokes the superclass methods for the CategoryListAdapter
        super(context, cursor);

        // Passes a CategoryTouchListener into the adapter
        this.mCategoryTouchListener = categoryTouchListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the category list item layout into a new view
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_category, parent, false);

        // Return a new CategoryViewHolder with a TouchListener attached
        return new CategoryViewHolder(view, mCategoryTouchListener);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder viewHolder, Cursor cursor) {

        // Verifies whether the cursor was moved to the requested position of ViewHolder being bound
        cursor.moveToPosition(cursor.getPosition());

        // Sets the cursor data into the CategoryViewHolder
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

