package com.michaelchaplin.spendometer;
package com.michaelchaplin.spendometer.ExpandableRecyclerView;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ReyclerView;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class RecentSpendingActivity extends AppCompatActivity {

    public RecyclerView mExpenseListRecyclerView;
    public ExpenseDayAdapter mExpenseDayAdapter;
    List<Expense> expenseData1 = new ArrayList<>();
    List<Expense> expenseData2 = new ArrayList<>();
    List<Expense> expenseData3 = new ArrayList<>();
    List<ExpenseDay> mExpenseDayData = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Setting up the dummy data to populate the RecyclerView
        expenseData1.add(new Expense("Food", "Stan's Diner", "AMEX", R.drawable.round_fastfood_white_48, 1581310800005L, 15.48));
        expenseData1.add(new Expense("Groceries", "Independent", "PCMC", R.drawable.round_restaurant_white_48, 1581310805000L, 72.38));
        expenseData1.add(new Expense("Gas", "Ultramar", "PCMC", R.drawable.round_local_gas_station_white_48, 1581310800050L, 125.00));
        
        expenseData2.add(new Expense("Food", "Pho May", "PCMC", R.drawable.round_fastfood_white_48, 1581397200005L, 16.20));
        expenseData2.add(new Expense("Alcohol", "Overflow Brewery", "AMEX", R.drawable.round_local_bar_white_48, 1581397205000L, 10));
                       
        expenseData3.add(new Expense("Movies", "Harley Quinn", "AMEX", R.drawable.round_local_movies_white_48, 1581483605000L, 26.87));
        expenseData3.add(new Expense("Work Dues", "PEO Renewal", "AMEX", R.drawable.round_work_white_48, 1581483600050L, 156.65));
        expenseData3.add(new Expense("Sports", "Hockey Intramural", "CASH", R.drawable.round_sports_tennis_white_48, 1581483650000L, 20));                 
                         
        mExpenseDayData.add(expenseData1);
        mExpenseDayData.add(expenseData2);
        mExpenseDayData.add(expenseData3);
        // 1581310800000L is 2020/02/10, 1581397200000L is 2020/02/11 and 1581483600000 is 2020/02/12
        
        // Find the RecyclerView and assign its layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mExpenseListRecyclerView = findViewById(R.id.expense_list_recycler);
        mExpenseListRecyclerView.setLayoutManager(layoutManager);
        mExpenseListRecyclerView.setHasFixedSize(true); // Improves the performance
        
        // Defines the adapter and its characteristics
        mExpenseDayAdapter = new ExpenseDayAdapter(this, mExpenseDayData);
        mExpenseListRecyclerView.setAdapter(mExpenseDayAdapter);
        
    }

    

}
