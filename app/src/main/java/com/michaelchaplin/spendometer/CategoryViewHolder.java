package com.michaelchaplin.spendometer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mCategory;
    public ImageView mIcon;
    private RecyclerViewItemTouchListener mCategoryTouchListener;

    // Constructor to add all the views into the ViewHolder for a Category
    public CategoryViewHolder(View itemView, RecyclerViewItemTouchListener categoryTouchListener) {

        // Invokes the superclass methods for RecyclerView.ViewHolder
        super(itemView);

        // Finds the views that will be populated with the category data
        mCategory = itemView.findViewById(R.id.list_view_item_category_name);
        mIcon = itemView.findViewById(R.id.list_view_item_category_icon);

        // Creates and sets a CategoryTouchListener on the created CategoryViewHolder
        this.mCategoryTouchListener = categoryTouchListener;
        itemView.setOnClickListener(this);
    }

    public void setCursorData(Cursor cursor){

        // Extracts the values from the cursor at the given position
        String category = cursor.getString(cursor.getColumnIndex(SpendometerContract.CategoryEntry.COL_NAME));
        int icon = cursor.getInt(cursor.getColumnIndex(SpendometerContract.CategoryEntry.COL_ICON_ID));

        // Binds the data from the cursor to the views in the CategoryViewHolder
        mCategory.setText(category);
        mIcon.setImageResource(icon);
    }

    @Override
    public void onClick(View view) {
        mCategoryTouchListener.onItemClick(getAdapterPosition());
        Log.d(LOG_TAG, "onClick: ViewHolder has been clicked");
    }
}
