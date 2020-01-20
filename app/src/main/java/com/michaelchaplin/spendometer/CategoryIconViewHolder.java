package com.michaelchaplin.spendometer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

public class CategoryIconViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mCategory;
    public ImageView mIcon;
    private RecyclerViewItemTouchListener mIconTouchListener;
    public CategoryIconDataModel mItem;

    // Constructor to add all the views into the ViewHolder for a Category
    public CategoryIconViewHolder(View itemView, RecyclerViewItemTouchListener categoryTouchListener) {

        // Invokes the superclass methods for RecyclerView.ViewHolder
        super(itemView);

        // Finds the views that will be populated with the category data
        mCategory = itemView.findViewById(R.id.category_icon_name);
        mIcon = itemView.findViewById(R.id.category_icon_image);

        // Creates and sets a CategoryTouchListener on the created CategoryViewHolder
        this.mIconTouchListener = categoryTouchListener;
        itemView.setOnClickListener(this);
    }

    public void setCursorData(Cursor cursor){

        // Extracts the values from the cursor at the given position
        String category = cursor.getString(cursor.getColumnIndex(SpendometerContract.CategoryEntry.COL_NAME));
        int icon = cursor.getInt(cursor.getColumnIndex(SpendometerContract.CategoryEntry.COL_ICON_ID));

        // Binds the data from the cursor to the views in the CategoryIconViewHolder
        mCategory.setText(category);
        mIcon.setImageResource(icon);
    }

    public void setArrayListData(CategoryIconDataModel item) {

        // Set the CategoryIconViewHolder's item to be the passed in item
        this.mItem = item;

        // Binds the data from the CategoryIconDataModel item to the CategoryIconViewHolder
        mCategory.setText(item.iconName);
        mIcon.setImageResource(item.iconDrawable);
    }

    @Override
    public void onClick(View view) {
        mIconTouchListener.onItemClick(getAdapterPosition());
    }

}
