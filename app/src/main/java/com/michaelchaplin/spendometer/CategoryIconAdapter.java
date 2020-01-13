package com.michaelchaplin.spendometer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.michaelchaplin.spendometer.data.SpendometerProvider.LOG_TAG;

public class CategoryIconAdapter extends RecyclerView.Adapter<CategoryIconAdapter.MyViewHolder> {

    private ArrayList mValues;
    private Context mContext;
    private OnIconListener mOnIconListener;

    // The constructor for the Category icon Data set
    public CategoryIconAdapter(Context context, ArrayList dataset, OnIconListener onIconListener) {
        mContext = context;
        mValues = dataset;
        this.mOnIconListener = onIconListener;
    }

    // Provides a reference to each of the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView iconName;
        private ImageView iconImage;
        public CategoryIconDataModel item;
        OnIconListener onIconListener;

        private MyViewHolder(View view, OnIconListener onIconListener) {

            super(view);
            iconName = view.findViewById(R.id.category_icon_name);
            iconImage = view.findViewById(R.id.category_icon_image);
            this.onIconListener = onIconListener;

            view.setOnClickListener(this);
        }

        public void setData(CategoryIconDataModel item) {

            this.item = item;
            iconName.setText(item.iconName);
            iconImage.setImageResource(item.iconDrawable);
        }

        @Override
        public void onClick(View view) {
            onIconListener.onIconClick(getAdapterPosition());
        }
    }


    @Override
    public CategoryIconAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_category_icons, parent, false);
        return new MyViewHolder(view, mOnIconListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // Get element from the mValues Array at the specified position
        // This will replace the contents of the view with that element
        holder.setData((CategoryIconDataModel) mValues.get(position));
        Log.d(LOG_TAG, "onBindViewHolder: New data being bound at position " + position);
    }

    @Override
    public int getItemCount() {
        // Returns the number of arrays within the mValues ArrayList which is the size of the dataset
        return mValues.size();
    }

    public interface OnIconListener {
        void onIconClick(int position);
    }

}

