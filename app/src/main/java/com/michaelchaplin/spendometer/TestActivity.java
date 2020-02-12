package com.michaelchaplin.spendometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.michaelchaplin.spendometer.data.androidxprep.CategoryViewModel;

public class TestActivity extends AppCompatActivity {

    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_spending);



    }



}
