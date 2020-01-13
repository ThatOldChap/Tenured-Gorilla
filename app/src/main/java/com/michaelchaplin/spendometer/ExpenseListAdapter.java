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

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {

    private ArrayList mDataset;
    private Context mContext;
    private ExpenseTouchListener mExpenseTouchListener;

    // A constructor used to pass in a dataset to the adapter
    public ExpenseListAdapter (Context context, ArrayList dataset, ExpenseTouchListener expenseTouchListener) {
        mContext = context;
        mDataset = dataset;
        this.mExpenseTouchListener = expenseTouchListener;
    }

    // Provides a reference to each of the views contained in each data item
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView expenseCategory;
        private TextView expenseDate;
        private TextView expenseCost;
        private TextView expenseNotes;
        private TextView expenseAccount;
        private ImageView expenseCategoryIcon;
        public ExpenseDataModel expense;
        ExpenseTouchListener expenseTouchListener;

        public ExpenseViewHolder(View itemView, ExpenseTouchListener expenseTouchListener) {

            // Invokes the superclass methods for ExpenseViewHolder
            super(itemView);

            // Finds the views that will populated with the expense data
            expenseCategory = itemView.findViewById(R.id.expense_category);
            expenseDate = itemView.findViewById(R.id.expense_date);
            expenseCost = itemView.findViewById(R.id.expense_cost);
            expenseNotes = itemView.findViewById(R.id.expense_notes);
            expenseAccount = itemView.findViewById(R.id.expense_account);
            expenseCategoryIcon = itemView.findViewById(R.id.expense_category_icon);

            Log.d(LOG_TAG, "ExpenseViewHolder: Views have been found");
            // Creates an sets an ExpenseTouchListener on the selected view
            this.expenseTouchListener = expenseTouchListener;
            itemView.setOnClickListener(this);
        }

        public void setExpenseData(ExpenseDataModel Expense){

            this.expense = Expense;

            // Populates the ViewHolders with their Expense data
            expenseCategory.setText(Expense.category);
            expenseDate.setText(Integer.toString(Expense.date));
            expenseCost.setText("$" + Double.toString(Expense.cost));
            expenseNotes.setText(Expense.notes);
            expenseAccount.setText(Expense.account);
            expenseCategoryIcon.setImageResource(Expense.icon);
        }

        @Override
        public void onClick(View view) {
            expenseTouchListener.onExpenseClick(getAdapterPosition());
        }
    }

    @Override
    public ExpenseListAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(LOG_TAG, "onCreateViewHolder: Viewholder has been created");
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_expense, parent, false);
        return new ExpenseViewHolder(view, mExpenseTouchListener);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {

        // Updates the contents of the itemView with the data at the given position in mDataset
        Log.d(LOG_TAG, "onBindViewHolder: Viewholder is being binded to data at position " + position);
        holder.setExpenseData((ExpenseDataModel) mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        // Returns the number of Expenses in the mDataset ArrayList which is the size of the dataset
        Log.d(LOG_TAG, "getItemCount: mDataset is " + mDataset.size() + " elements in size");
        return mDataset.size();
    }

    public interface ExpenseTouchListener {
        void onExpenseClick(int position);
    }

}
