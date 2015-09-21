package me.mvdw.swiperecyclerviewexample.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import me.mvdw.swiperecyclerviewexample.R;
import me.mvdw.swiperecyclerviewexample.fragment.ExampleActivityFragment;


public class ExampleActivity extends FragmentActivity {

    private Button addItemButton;
    private ExampleActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        fragment = (ExampleActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        addItemButton = (Button) findViewById(R.id.toolbar_add_button);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.addItem();
            }
        });
    }
}