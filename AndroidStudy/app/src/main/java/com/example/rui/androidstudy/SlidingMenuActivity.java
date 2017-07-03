package com.example.rui.androidstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.rui.androidstudy.View.SlidingMenu;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SlidingMenuActivity extends AppCompatActivity {

    @Bind(R.id.one)
    ImageView one;
    @Bind(R.id.two)
    ImageView two;
    @Bind(R.id.three)
    ImageView three;
    @Bind(R.id.four)
    ImageView four;
    @Bind(R.id.five)
    ImageView five;
    @Bind(R.id.id_menu)
    SlidingMenu idMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu);
        ButterKnife.bind(this);
        idMenu = (SlidingMenu) findViewById(R.id.id_menu);
    }

    public void toggleMenu(View view) {
        idMenu.toggle();
    }


}
