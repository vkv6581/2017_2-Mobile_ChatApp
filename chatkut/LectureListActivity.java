package com.example.taeksu.chatkut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.taeksu.chatkut.openchannel.OpenChannelActivity;
import com.example.taeksu.chatkut.utils.PreferenceUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

public class LectureListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*this.setTitle("건물");*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white_24_dp);
        }

        //각 건물에 대한 인텐트를 만들어서 name과 value를 정해줌//
        findViewById(R.id.linear_layout_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dam_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                dam_intent.putExtra("customtype", "기계공학부");
                startActivity(dam_intent);
            }
        });

        findViewById(R.id.linear_layout_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent human_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                human_intent.putExtra("customtype", "메카트로닉스공학부");
                startActivity(human_intent);
            }
        });

        findViewById(R.id.linear_layout_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent first_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                first_intent.putExtra("customtype", "전기전자통신공학부");
                startActivity(first_intent);
            }
        });

        findViewById(R.id.linear_layout_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent second_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                second_intent.putExtra("customtype", "컴퓨터공학부");
                startActivity(second_intent);
            }
        });

        findViewById(R.id.linear_layout_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent third_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                third_intent.putExtra("customtype", "디자인건축공학부");
                startActivity(third_intent);
            }
        });

        findViewById(R.id.linear_layout_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fourth_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                fourth_intent.putExtra("customtype", "에너지신소재화학공학부");
                startActivity(fourth_intent);
            }
        });

        findViewById(R.id.linear_layout_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fourth_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                fourth_intent.putExtra("customtype", "산업경영학부");
                startActivity(fourth_intent);
            }
        });
        findViewById(R.id.linear_layout_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fourth_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                fourth_intent.putExtra("customtype", "교양학부");
                startActivity(fourth_intent);
            }
        });
        findViewById(R.id.linear_layout_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fourth_intent = new Intent(LectureListActivity.this, OpenChannelActivity.class);
                fourth_intent.putExtra("customtype", "HRD학과");
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

    //로그아웃 메뉴 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu_main에 선언된 로그아웃 버튼을 추가한다.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //로그아웃 버튼 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_logout) {
            Toast.makeText(this, "로그아웃",Toast.LENGTH_SHORT).show();
            disconnect();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        PreferenceUtils.setConnected(LectureListActivity.this, false);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
