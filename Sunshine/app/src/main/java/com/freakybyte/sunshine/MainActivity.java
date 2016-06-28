package com.freakybyte.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.freakybyte.sunshine.controller.ui.PlaceholderFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
}
