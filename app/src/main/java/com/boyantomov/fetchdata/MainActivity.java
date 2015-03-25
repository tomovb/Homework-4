package com.boyantomov.fetchdata;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class MainActivity extends ActionBarActivity {

    Stack urlAddress = new Stack();
    String moreTopicsURL;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView lvDetail;
    Button bNextPage, bPreviousPage;
    RadioGroup radioGroup;
    Context context = MainActivity.this;
    ArrayList<TopicList> myList = new ArrayList<TopicList>();
    ArrayList<User> userList = new ArrayList<>();
    String data;
    int totalUsersCount, guestUsersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlAddress.push("http://frm.hackafe.org/latest.json");

        //tvTotalUsers = (TextView) findViewById(R.id.tvTotalUsers);
        //tvGuests = (TextView) findViewById(R.id.tvGuests);
        bNextPage = (Button) findViewById(R.id.bNextPage);
        bPreviousPage = (Button) findViewById(R.id.bPreviousPage);
        RadioGroup sortRadioGroup = (RadioGroup) findViewById(R.id.rgRadioGroup);
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        lvDetail = (ListView) findViewById(R.id.lvCustomList);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //populate users
        getUsersInList();

        //populate list with data
        getDataInList();

        final ForecastAdapter myAdapter = new ForecastAdapter(context, myList);
        lvDetail.setAdapter(myAdapter);


        //Set onButtonClick Listener
        bNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nextURL = "http://frm.hackafe.org/" + moreTopicsURL;
                urlAddress.push(nextURL);
                Log.d("Sunshine", "URL: " + urlAddress.peek().toString());
                myList.clear();
                getDataInList();
                myAdapter.notifyDataSetChanged();
            }
        });

        bPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urlAddress.peek() == "http://frm.hackafe.org/latest.json"){
                    Toast.makeText(context, "No previous page found!", Toast.LENGTH_SHORT).show();
                } else {
                    urlAddress.pop();
                    //Log.d("Sunshine", "URL: " + urlAddress.peek().toString());
                    myList.clear();
                    getDataInList();
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

        sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbCreated){
                    Collections.sort(myList, TopicList.SORT_BY_DATE);
                    myAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.rbReplies){
                    Collections.sort(myList, TopicList.SORT_BY_REPLIES);
                    myAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.rbViews){
                    Collections.sort(myList, TopicList.SORT_BY_VIEWS);
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

        //setOnItemClick Listener
        lvDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TopicList itemFromList = (TopicList) parent.getItemAtPosition(position);
                if (itemFromList.getFacebookAccount().toString().length() > 0) {
                    String facebookAddress = "http://facebook.com/" + itemFromList.getFacebookAccount().toString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookAddress)));
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myList.clear();
                getDataInList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        lvDetail.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                /**
                 * This enables us to force the layout to refresh only when the first item
                 * of the list is visible.
                 **/
                if (lvDetail != null && lvDetail.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvDetail.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvDetail.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                mSwipeRefreshLayout.setEnabled(enable);
            }
        });
    }


    private List<String> parseForecast(String data) {
        try {
            //parse String so we have JSONObject
            JSONObject obj = new JSONObject(data);

            JSONObject dataObj = obj.getJSONObject("topic_list");

            moreTopicsURL = dataObj.getString("more_topics_url");

            //totalUsersCount = dataObj.getInt("count");
            //guestUsersCount = dataObj.getInt("guests");

            JSONArray list = dataObj.getJSONArray("topics");
            for (int i = 0; i < list.length(); i++) {
                JSONObject topicListObj = list.getJSONObject(i);
                //get "title" object
                String topicName = topicListObj.getString("title");
                //get "created_at" object
                String topicDate = topicListObj.getString("created_at");
                //get "reply_count" object
                int topicReplies = topicListObj.getInt("reply_count");
                //get "views" object
                int topicViews = topicListObj.getInt("views");

                User posterUser = null;
                JSONArray posters = topicListObj.getJSONArray("posters");
                for(int j=0; j<posters.length();j++){
                    JSONObject posterObj = posters.getJSONObject(j);
                    // check if description contains "Original Poster"
                    String description = posterObj.getString("description");
                    if(description.contains("Original Poster")){
                        String posterUserID = posterObj.getString("user_id");
                        posterUser = createUserFromID(posterUserID);
                    }
                }

                //Create object
                TopicList ld = new TopicList();
                ld.setTopicTitle(topicName);
                ld.setCreated(topicDate);
                ld.setReplies(topicReplies);
                ld.setViews(topicViews);
                ld.setUser(posterUser);

                //Add object into the ArrayList
                myList.add(ld);

                Log.d("Sunshine", "Topic Name = " + topicName + ", UserName: " + posterUser.getUserName());
            }
        } catch (Throwable t) {
            Log.e("Sunshine", t.getMessage(), t);
            return null;
        }
        return null;
    }

    private String getForecast() {
        try {
            //URL url = new URL(urlAddress);
            //InputStream inputStream = url.openStream();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet((String) urlAddress.peek());
            InputStream inputStream = httpClient.execute(httpGet).getEntity().getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            String data = total.toString();
            return data;

        } catch (Throwable t) {
            Log.e("Sunshine", t.getMessage(), t);
            Toast.makeText(context, "No connection to server", Toast.LENGTH_SHORT).show();

            return null;
        }
    }


    private void getDataInList() {

        data = getForecast();
        if (data != null && !data.isEmpty()) {
            List<String> forecast = parseForecast(data);

            //tvTotalUsers.setText("Total Users: " + Integer.toString(totalUsersCount));
            //tvGuests.setText("Guests: " + Integer.toString(guestUsersCount));

        } else {
            Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show();
        }

    }

    private void getUsersInList() {
        data = getForecast();
        if (data != null && !data.isEmpty()) {
            try {
                //parse String so we have JSONObject
                JSONObject obj = new JSONObject(data);

                JSONArray list = obj.getJSONArray("users");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject userListObj = list.getJSONObject(i);

                    String userID = userListObj.getString("id");
                    String userName = userListObj.getString("username");
                    String avatar = userListObj.getString("avatar_template");

                    //Create object
                    User ul = new User(userID, userName, avatar);

                    //Add object into the ArrayList
                    userList.add(ul);

                    Log.d("Sunshine", "User = " + userName);
                }
            } catch (Throwable t) {
                Log.e("Sunshine", t.getMessage(), t);
            }

        } else {
            Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show();
        }
    }

    private User createUserFromID(String posterUserID){
        for(int i=0; i<userList.size();i++){
            if(userList.get(i).getId() == posterUserID){
                return new User(userList.get(i).getId(), userList.get(i).getUserName(), userList.get(i).getAvatarTemplate());
            }
        }
        return null;
    }
}
