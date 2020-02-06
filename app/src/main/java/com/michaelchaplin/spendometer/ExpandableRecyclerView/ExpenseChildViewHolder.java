package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelchaplin.spendometer.R;

public class ExpenseChildViewHolder extends ChildViewHolder{

    private TextView mCost, mNotes, mAccount;
    private ImageView mIcon;

    public ExpenseChildViewHolder(View itemView) {
        super(itemView);

        mCost = itemView.findViewById(R.id.expense_child_cost);
        mNotes = itemView.findViewById(R.id.expense_child_notes);
        mAccount = itemView.findViewById(R.id.expense_child_account);
        mIcon = itemView.findViewById(R.id.expense_child_category_icon);
    }

    public void setData(Expense expense) {

        mCost.setText(String.valueOf(expense.getCost()));
        mNotes.setText(expense.getNotes());
        mAccount.setText(expense.getAccount());
        mIcon.setImageResource(expense.getIconID());
    }
}
