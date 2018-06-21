package com.michaelchaplin.spendometer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view that shows the Categories TextView
        TextView categories = findViewById(R.id.main_header_categories);

        // Sets up a click listener on the Categories TextView
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to open the Categories Activity
                Intent categoriesIntent = new Intent(MainActivity.this, CategoriesActivity.class);

                // Starts the CategoriesActivity
                startActivity(categoriesIntent);

            }
        });

    }
}
