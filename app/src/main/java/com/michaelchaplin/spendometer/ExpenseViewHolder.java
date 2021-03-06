package com.michaelchaplin.spendometer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.data.SpendometerContract;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mCategory, mDateMonth, mDateDay, mCost, mNotes, mAccount;
    public ImageView mIcon;
    Calendar mCalendar;
    private RecyclerViewItemTouchListener mExpenseTouchListener;

    // Constructor to add all the views into the ViewHolder for an expense
    public ExpenseViewHolder(View itemView, RecyclerViewItemTouchListener expenseTouchListener) {

        // Invokes the superclass methods for RecyclerView.ViewHolder
        super(itemView);

        // Finds the views that will populated with the expense data
        mCategory = itemView.findViewById(R.id.expense_category);
        mDateMonth = itemView.findViewById(R.id.expense_date_month);
        mDateDay = itemView.findViewById(R.id.expense_date_day);
        mCost = itemView.findViewById(R.id.expense_cost);
        mNotes = itemView.findViewById(R.id.expense_notes);
        mAccount = itemView.findViewById(R.id.expense_edit_account);
        mIcon = itemView.findViewById(R.id.expense_category_icon);

        // Creates and sets an ExpenseTouchListener on the created ExpenseViewHolder
        this.mExpenseTouchListener = expenseTouchListener;
        itemView.setOnClickListener(this);
    }

    public void setCursorData(Cursor cursor){

        // Extracts the values from the cursor at the given position
        String category = cursor.getString(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_CATEGORY));
        Double cost = cursor.getDouble(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_COST));
        long date = cursor.getLong(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_DATE));
        String notes = cursor.getString(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_NOTES));
        int icon = cursor.getInt(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ICON_ID));
        String account = cursor.getString(cursor.getColumnIndex(SpendometerContract.ExpenseEntry.COL_ACCOUNT));

        // Binds the data extracted from the cursor at the given position into the views within the ExpenseViewHolder
        mCategory.setText(category);
        mCost.setText(String.valueOf(cost));
        mNotes.setText(notes);
        mIcon.setImageResource(icon);
        mAccount.setText(account);

        mCalendar = Calendar.getInstance(TimeZone.getDefault());
        mCalendar.setTimeInMillis(date);

        mDateMonth.setText(mCalendar.getDisplayName(mCalendar.get(Calendar.MONTH),Calendar.LONG, Locale.getDefault()));
        mDateDay.setText(String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)));
    }

    @Override
    public void onClick(View view) {
        mExpenseTouchListener.onItemClick(getAdapterPosition());
    }
}
