package com.boyantomov.fetchdata;

import java.util.Comparator;

/**
 * Created by admin on 16.3.2015 г..
 */
public class TopicList {

    String topicTitle;
    String facebookAccount;
    String created;
    int replies;
    int views;
    User user;


    public static Comparator<TopicList> SORT_BY_DATE = new Comparator<TopicList>() {
        @Override
        public int compare(TopicList lhs, TopicList rhs) {
            return lhs.created.compareTo(rhs.created);
        }
    };

    public static Comparator<TopicList> SORT_BY_REPLIES = new Comparator<TopicList>() {
        @Override
        public int compare(TopicList lhs, TopicList rhs) {
            return lhs.getReplies() - rhs.getReplies();
        }
    };

    public static Comparator<TopicList> SORT_BY_VIEWS = new Comparator<TopicList>() {
        @Override
        public int compare(TopicList lhs, TopicList rhs) {
            return lhs.getViews() - rhs.getViews();
        }
    };

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getFacebookAccount() {
        return facebookAccount;
    }

    public void setFacebookAccount(String facebookAccount) { this.facebookAccount = facebookAccount; }

    public int getReplies() { return replies; }

    public void setReplies(int replies) { this.replies = replies; }

    public int getViews() { return views; }

    public void setViews(int views) { this.views = views; }

    public String getCreated() { return created; }

    public void setCreated(String created) { this.created = created; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
