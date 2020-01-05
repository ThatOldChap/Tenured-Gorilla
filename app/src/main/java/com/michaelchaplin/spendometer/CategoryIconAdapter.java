package com.michaelchaplin.spendometer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryIconAdapter extends RecyclerView.Adapter<CategoryIconAdapter.MyViewHolder> {

    private ArrayList mValues;
    private Context mContext;

    // The constructor for the Category icon Data set
    public CategoryIconAdapter(Context context, ArrayList dataset) {
        mContext = context;
        mValues = dataset;
    }

    // Provides a reference to each of the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView iconName;
        public ImageView iconImage;
        public CategoryIconData item;

        public MyViewHolder(View view) {

            super(view);

            iconName = view.findViewById(R.id.category_icon_name);
            iconImage = view.findViewById(R.id.category_icon_image);
        }

        public void setData(CategoryIconData item) {
            this.item = item;

            iconName.setText(item.iconName);
            iconImage.setImageResource(item.iconDrawable);
        }

    }


    @Override
    public CategoryIconAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_category_icons, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // Get element from the mValues Array at the specified position
        // This will replace the contents of the view with that element
        holder.setData((CategoryIconData) mValues.get(position));

    }

    @Override
    public int getItemCount() {

        // Returns the number of arrays within the mValues ArrayList which is the size of the dataset
        return mValues.size();
    }

}

