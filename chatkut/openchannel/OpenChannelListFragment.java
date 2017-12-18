package com.example.taeksu.chatkut.openchannel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sendbird.android.BaseMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBirdException;
import com.example.taeksu.chatkut.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

//담헌실학관 클릭 후 나오는 채널 목록을 만들어준다.
public class OpenChannelListFragment extends Fragment {

    public static final String EXTRA_OPEN_CHANNEL_URL = "OPEN_CHANNEL_URL";
    private static final String LOG_TAG = OpenChannelListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private OpenChannelListAdapter mChannelListAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private FloatingActionButton mCreateChannelFab;

    private OpenChannelListQuery mChannelListQuery;

    //즐겨찾기 저장을 위한 쉐어드프리퍼런스.
    private SharedPreferences bookmark;

    public String mCustomType;

    public static OpenChannelListFragment newInstance(String customType) {
        OpenChannelListFragment fragment = new OpenChannelListFragment();
        fragment.mCustomType = customType;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //셰어드 프리퍼런스 초기화
        bookmark = this.getActivity().getSharedPreferences("bookmark",MODE_PRIVATE);

        View rootView = inflater.inflate(R.layout.fragment_open_channel_list, container, false);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        ((OpenChannelActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.all_open_channels));

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_open_channel_list);
        mChannelListAdapter = new OpenChannelListAdapter(getContext());

        // Set color?
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout_open_channel_list);

        // Swipe down to refresh channel list.
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(true);
                refreshChannelList(15);
            }
        });

        //채널추가 원형 버튼임!!
        mCreateChannelFab = (FloatingActionButton) rootView.findViewById(R.id.fab_open_channel_list);
        mCreateChannelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateOpenChannelActivity.class);
                intent.putExtra("customtype", mCustomType);
                startActivity(intent);
            }
        });

        setUpAdapter();
        setUpRecyclerView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh once
        refreshChannelList(15);
    }

    void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChannelListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // If user scrolls to bottom of the list, loads more channels.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.getItemCount() - 1) {
                    loadNextChannelList();
                }
            }
        });
    }

    // 리스트의 아이템을 클릭했을 시 채널에 입장한다.
    private void setUpAdapter() {
        mChannelListAdapter.setOnItemClickListener(new OpenChannelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OpenChannel channel) {
                //채널의 Uri를 저장한 후 채널에 입장!!
                String channelUrl = channel.getUrl();
                OpenChatFragment fragment = OpenChatFragment.newInstance(channelUrl);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container_open_channel, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //채널 이름 롱 클릭 시 이벤트 처리.
        mChannelListAdapter.setOnItemLongClickListener(new OpenChannelListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongPress(OpenChannel channel) {
                showChannelOptionsDialog(channel);
            }
        });
    }
    //채널 롱 클릭 시 이벤트 다이얼로그 생성
    private void showChannelOptionsDialog(final OpenChannel channel) {
        String[] options = new String[] { "즐겨찾기 추가" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //즐겨찾기에 추가하는 코드.
                    //즐겨찾기 개수를 불러온다. 저장되있지 않다면 기본값 0
                    int bookmarkcount = bookmark.getInt("count",0);

                    //셰어드 프리퍼런스에 저장하기 위해 에디터 생성
                    SharedPreferences.Editor editor = bookmark.edit();
                    //uri와 이름을 저장한다.
                    editor.putString(bookmarkcount+"Uri",channel.getUrl());
                    editor.putString(bookmarkcount+"name",channel.getName());

                    //즐겨찾기를 추가했기 때문에 개수를 1개 증가시켜 준다.
                    editor.putInt("count",bookmarkcount+1);

                    editor.commit();
                }
            }
        });
        builder.create().show();
    }


    /**
     * Creates a new query to get the list of the user's Open Channels,
     * then replaces the existing dataset.
     *
     * @param numChannels   The number of channels to load.
     */
    void refreshChannelList(int numChannels) {
        mChannelListQuery= OpenChannel.createOpenChannelListQuery();
        mChannelListQuery.setLimit(numChannels);
        mChannelListQuery.setCustomTypeFilter(mCustomType); //mCustomType에 따라 channellist를 생성
        mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if (e != null) {

                }
                mChannelListAdapter.setOpenChannelList(list);

                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    /**
     * Loads the next channels from the current query instance.
     */
    void loadNextChannelList() {
        mChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                for (OpenChannel channel : list) {
                    mChannelListAdapter.addLast(channel);
                }
            }
        });
    }

}
