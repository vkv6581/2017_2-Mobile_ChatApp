package com.example.taeksu.chatkut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.example.taeksu.chatkut.openchannel.OpenChannelActivity;
import com.example.taeksu.chatkut.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("건물");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        //각 건물에 대한 인텐트를 만들어서 name과 value를 정해줌//
        findViewById(R.id.linear_layout_damheon_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dam_intent = new Intent(MainActivity.this, OpenChannelActivity.class);
                dam_intent.putExtra("customtype", "담헌실학관");
                startActivity(dam_intent);
            }
        });

        findViewById(R.id.linear_layout_human_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent human_intent = new Intent(MainActivity.this, OpenChannelActivity.class);
                human_intent.putExtra("customtype", "인문경영관");
                startActivity(human_intent);
            }
        });

        findViewById(R.id.linear_layout_first_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent first_intent = new Intent(MainActivity.this, OpenChannelActivity.class);
                first_intent.putExtra("customtype", "1공학관");
                startActivity(first_intent);
            }
        });

        findViewById(R.id.linear_layout_second_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent second_intent = new Intent(MainActivity.this, OpenChannelActivity.class);
                second_intent.putExtra("customtype", "2공학관");
                startActivity(second_intent);
            }
        });

        findViewById(R.id.linear_layout_third_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent third_intent = new Intent(MainActivity.this, OpenChannelActivity.class);
                third_intent.putExtra("customtype", "3공학관");
                startActivity(third_intent);
            }
        });

        findViewById(R.id.linear_layout_fourth_channels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fourth_intent = new Intent(MainActivity.this, OpenChannelActivity.class);
                fourth_intent.putExtra("customtype", "4공학관");
                startActivity(fourth_intent);
            }
        });

        findViewById(R.id.button_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃
                disconnect();
            }
        });

    }

    //로그아웃
    private void disconnect() {
        SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
            @Override
            public void onUnregistered(SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();

                    // Don't return because we still need to disconnect.
                } else {
//                    Toast.makeText(MainActivity.this, "All push tokens unregistered.", Toast.LENGTH_SHORT).show();
                }

                SendBird.disconnect(new SendBird.DisconnectHandler() {
                    @Override
                    public void onDisconnected() {
                        PreferenceUtils.setConnected(MainActivity.this, false);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
