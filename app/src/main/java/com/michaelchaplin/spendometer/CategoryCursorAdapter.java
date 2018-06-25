package com.michaelchaplin.spendometer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * {@link CategoryCursorAdapter} is an adapter for a list view that uses a cursor of data as its source.
 * This adapter creates list items for each row of data in the cursor
 */
public class CategoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link CategoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public CategoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param viewGroup  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_category, viewGroup, false);
    }


    /**
     * This method binds the category data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current category can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Finds the fields that need to be populated in the inflated template
        TextView categoryNameTextView = view.findViewById(R.id.list_view_item_category);

        // Extract the properties from the cursor
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("name"));

        // Populate the list item's fields with the extracted properties
        categoryNameTextView.setText(categoryName);

    }
}
