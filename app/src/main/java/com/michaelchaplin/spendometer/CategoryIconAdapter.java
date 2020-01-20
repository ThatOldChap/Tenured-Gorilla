package com.michaelchaplin.spendometer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CategoryIconAdapter extends RecyclerView.Adapter<CategoryIconViewHolder> {

    private ArrayList<CategoryIconDataModel> mValues;
    private Context mContext;
    private RecyclerViewItemTouchListener mIconTouchListener;

    // The constructor for the Category icon Data set using an ArrayList as a dataset
    public CategoryIconAdapter(Context context, ArrayList<CategoryIconDataModel> dataset, RecyclerViewItemTouchListener iconListener) {
        mContext = context;
        mValues = dataset;

        // Passes an IconTouchListener into the adapter
        this.mIconTouchListener = iconListener;
    }

    @Override
    public CategoryIconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the category icon layout items
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_category_icons, parent, false);

        // Returns a new CategoryIconViewHolder with a TouchListener attached
        return new CategoryIconViewHolder(view, mIconTouchListener);
    }

    @Override
    public void onBindViewHolder(CategoryIconViewHolder holder, int position) {

        // Sets the mValues data into the ViewHolder at the given position
        holder.setArrayListData(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        // Returns the number of arrays within the mValues ArrayList which is the size of the dataset
        return mValues.size();
    }

}

