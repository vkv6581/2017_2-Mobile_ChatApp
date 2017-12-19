package com.example.taeksu.chatkut;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.taeksu.chatkut.openchannel.OpenChatActivity;
import com.example.taeksu.chatkut.openchannel.OpenChatFragment;
import com.example.taeksu.chatkut.utils.PreferenceUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Toolbar mToolbar;
    private Intent mBuilding;
    private Intent mLecture;
    private ListView mBookmarkList;
    //즐겨찾기 저장을 위한 셰어드 프리퍼런스.
    private SharedPreferences mBookmark;
    //즐겨찾기의 Uri와 이름을 저장할 리스트.
    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<String> mUris = new ArrayList<String>();
    private ArrayAdapter<String> mNameAdapter;
    private int mBookmarkNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*this.setTitle("건물/즐겨찾기 목록");*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        mBookmarkList = (ListView) findViewById(R.id.bookmark_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        findViewById(R.id.building_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //건물 목록으로 이동.
                mBuilding = new Intent(BookmarkActivity.this, MainActivity.class);
                startActivity(mBuilding);
            }
        });

        findViewById(R.id.lecture_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //건물 목록으로 이동.
                mLecture = new Intent(BookmarkActivity.this, LectureListActivity.class);
                startActivity(mLecture);
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
        mBookmark = this.getSharedPreferences("bookmark",MODE_PRIVATE);
        initBookmarkList();
        mNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mNames);
        mBookmarkList.setAdapter(mNameAdapter);
        mBookmarkList.setOnItemLongClickListener(this);
        mBookmarkList.setOnItemClickListener(this);

    }//onCreate 끝.

    //프리퍼런스에서 리스트 뷰를 초기화하는 함수
    private void initBookmarkList() {
        mNames.clear();
        mUris.clear();
        mBookmarkNum = mBookmark.getInt("count",0);
        //반복문을 통해 리스트 초기화.
        for (int i = 0; i < mBookmarkNum; i++) {
            //names와 Url에 값들을 추가한다.
            mNames.add(mBookmark.getString(i+"name",""));
            mUris.add(mBookmark.getString(i+"Url",""));
        }
    }//init_Bookmark_List끝.

    //onresume에서 어댑터의 변경을 캐치한다.
    @Override
    public void onResume() {
        super.onResume();
        initBookmarkList();
        mNameAdapter.notifyDataSetChanged();
    }//onresume함수 끝.


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
                        PreferenceUtils.setConnected(BookmarkActivity.this, false);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    public interface onBackPressedListener {
        boolean onBack();
    }
    private BookmarkActivity.onBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(BookmarkActivity.onBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener != null && mOnBackPressedListener.onBack()) {
            return;
        }
        super.onBackPressed();
    }

    //리스트뷰 아이템 클릭 리스너.
    //클릭한 아이템의 주소로 Fragment이동.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //채널의 Uri를 저장한 후 채널에 입장!!
        String channelUrl = mUris.get(position);
        String name = mNames.get(position);

        Intent intent = new Intent(getApplicationContext(), OpenChatActivity.class);
        intent.putExtra("channelUrl", channelUrl);
        intent.putExtra("name", name);
        startActivity(intent);

        /*OpenChatFragment fragment = OpenChatFragment.newInstance(channelUrl);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_open_channel, fragment)
                .addToBackStack(null)
                .commit();*/
        /*OpenChatFragment fragment = OpenChatFragment.newInstance(channelUrl);

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();

        manager.beginTransaction()
                .replace(R.id.container_open_channel, fragment)
                .commit();
        Toast.makeText(BookmarkActivity.this, channelUrl + name, Toast.LENGTH_SHORT).show();*/
    }

    //리스트뷰 아이템 롱 클릭 리스너.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showBMKOptionsDialog(position, this);
        return true;
    }//onitemLongClick끝.

    //채널 롱 클릭 시 이벤트 다이얼로그 생성
    private void showBMKOptionsDialog(final int position, final BookmarkActivity act) {
        String[] options = new String[] { "즐겨찾기 삭제" };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //즐겨찾기에 삭제하는 코드.
                    //셰어드 프리퍼런스에 저장하기 위해 에디터 생성
                    SharedPreferences.Editor editor = mBookmark.edit();
                    //에디터를 비워줌.
                    editor.clear();
                    editor.commit();

                    //Uri와 이름에서 삭제할 위치의 북마크 삭제.
                    mNames.remove(position);
                    mUris.remove(position);

                    for (int i = 0; i < mNames.size() ; i++) {
                        //uri와 이름을 저장한다.
                        editor.putString(i+"Uri", mUris.get(i));
                        editor.putString(i+"name", mNames.get(i));
                    }
                    editor.putInt("count", mNames.size());
                    editor.commit();
                    //화면을 갱신한다.
                    act.onResume();
                }
            }
        });
        builder.create().show();
    }
}
