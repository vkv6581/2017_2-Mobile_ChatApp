package com.example.taeksu.chatkut;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.taeksu.chatkut.openchannel.OpenChannelActivity;
import com.example.taeksu.chatkut.openchannel.OpenChatFragment;
import com.example.taeksu.chatkut.utils.PreferenceUtils;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;

public class Main_bookmark extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Toolbar mToolbar;
    private Intent building;
    private ListView mylist;
    //즐겨찾기 저장을 위한 셰어드 프리퍼런스.
    private SharedPreferences bookmark;
    //즐겨찾기의 Uri와 이름을 저장할 리스트.
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> Uris = new ArrayList<String>();
    private ArrayAdapter<String> name_Adapter;
    private int bookmarkNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("건물/즐겨찾기 목록");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bookmark);

        mylist = (ListView) findViewById(R.id.bookmark_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        findViewById(R.id.building_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //건물 목록으로 이동.
                building = new Intent(Main_bookmark.this, MainActivity.class);
                startActivity(building);
            }
        });

        findViewById(R.id.button_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃
                disconnect();
            }
        });

        //셰어드 프리퍼런스 초기화
        bookmark = this.getSharedPreferences("bookmark",MODE_PRIVATE);
        init_Bookmark_List();
        name_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        mylist.setAdapter(name_Adapter);
        mylist.setOnItemLongClickListener(this);

    }//onCreate 끝.

    //프리퍼런스에서 리스트 뷰를 초기화하는 함수
    private void init_Bookmark_List() {
        names.clear();
        Uris.clear();
        bookmarkNum = bookmark.getInt("count",0);
        //반복문을 통해 리스트 초기화.
        for (int i = 0 ; i < bookmarkNum ; i++) {
            //names와 Uri에 값들을 추가한다.
            names.add(bookmark.getString(i+"name",""));
            Uris.add(bookmark.getString(i+"Uri",""));
        }
    }//init_Bookmark_List끝.

    //onresume에서 어댑터의 변경을 캐치한다.
    @Override
    public void onResume() {
        super.onResume();
        init_Bookmark_List();
        name_Adapter.notifyDataSetChanged();
    }//onresume함수 끝.

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
                        PreferenceUtils.setConnected(Main_bookmark.this, false);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    //리스트뷰 아이템 클릭 리스너.
    //클릭한 아이템의 주소로 Fragment이동.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //채널의 Uri를 저장한 후 채널에 입장!!
        String channelUrl = Uris.get(position);
        OpenChatFragment fragment = OpenChatFragment.newInstance(channelUrl);
        /*getFragmentManager().beginTransaction()
                .replace(R.id.container_open_channel, fragment)
                .addToBackStack(null)
                .commit();*/
    }

    //리스트뷰 아이템 롱 클릭 리스너.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showBMKOptionsDialog(position, this);
        return false;
    }//onitemLongClick끝.

    //채널 롱 클릭 시 이벤트 다이얼로그 생성
    private void showBMKOptionsDialog(final int position, final Main_bookmark act) {
        String[] options = new String[] { "즐겨찾기 삭제" };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //즐겨찾기에 삭제하는 코드.
                    //셰어드 프리퍼런스에 저장하기 위해 에디터 생성
                    SharedPreferences.Editor editor = bookmark.edit();
                    //에디터를 비워줌.
                    editor.clear();
                    editor.commit();

                    //Uri와 이름에서 삭제할 위치의 북마크 삭제.
                    names.remove(position);
                    Uris.remove(position);

                    for (int i=0 ; i < names.size() ; i++) {
                        //uri와 이름을 저장한다.
                        editor.putString(i+"Uri",Uris.get(i));
                        editor.putString(i+"name",names.get(i));
                    }
                    editor.putInt("count",names.size());
                    editor.commit();
                    //화면을 갱신한다.
                    act.onResume();
                }
            }
        });
        builder.create().show();
    }
}
