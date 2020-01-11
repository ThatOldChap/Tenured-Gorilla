package com.michaelchaplin.spendometer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {

    private ArrayList mDataset;
    private Context mContext;
    private ExpenseTouchListener mExpenseTouchListener;

    // The Constructor for the ExpenseList dataset
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
            expenseCategoryIcon = itemView.findViewById(R.id.expense_category_icon);

            // Creates an sets an ExpenseTouchListener on the selected view
            this.expenseTouchListener = expenseTouchListener;
            itemView.setOnClickListener(this);
        }

        public void setExpenseData(ExpenseDataModel Expense){

            this.expense = Expense;

            // Populates the ViewHolders with their Expense data
            expenseCategory.setText(Expense.category);
            expenseDate.setText((int) Expense.date);
            expenseCost.setText((int) Expense.cost);
            expenseNotes.setText(Expense.notes);
            expenseCategoryIcon.setImageResource(Expense.icon);
        }

        @Override
        public void onClick(View view) {
            expenseTouchListener.onExpenseClick(getAdapterPosition());
        }
    }

    @Override
    public ExpenseListAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_expense, parent, false);
        return new ExpenseViewHolder(view, mExpenseTouchListener);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {

        // Updates the contents of the itemView with the data at the given position in mDataset
        holder.setExpenseData((ExpenseDataModel) mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        // Returns the number of Expenses in the mDataset ArrayList which is the size of the dataset
        return mDataset.size();
    }

    public interface ExpenseTouchListener {
        void onExpenseClick(int position);
    }

}
