package com.example.taeksu.chatkut.openchannel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.taeksu.chatkut.R;

public class OpenChannelActivity extends AppCompatActivity {

    private String customType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_channel);

        //발생시킨 인텐트에서 customType을 받아온다.
        Intent intent = getIntent();
        customType = intent.getStringExtra("customtype");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_open_channel);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white_24_dp);
        }

        // 채널리스트 불러오기
        Fragment fragment = OpenChannelListFragment.newInstance(customType);

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();

        manager.beginTransaction()
                .replace(R.id.container_open_channel, fragment)
                .commit();
    }

    interface onBackPressedListener {
        boolean onBack();
    }
    private onBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(onBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener != null && mOnBackPressedListener.onBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //cutstomType에 따른 액션바 타이틀 설정//
    void setActionBarTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }
}
