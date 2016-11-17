package com.freakybyte.sunshine.controller.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.controller.ui.fragment.DetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jose Torres on 03/10/2016.
 */

public class DetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        Bundle arguments = new Bundle();
        arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }

}
