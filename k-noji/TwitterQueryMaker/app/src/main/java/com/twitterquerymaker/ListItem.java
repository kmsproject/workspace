package com.twitterquerymaker;

public class ListItem {
    private int mNumber;
    private long mCreated;
    private String mTitle = null;
    private String mQuery = null;

    public ListItem() {}

    public ListItem(int number, long created, String title, String query){
        mNumber = number;
        mCreated = created;
        mTitle = title;
        mQuery = query;
    }

    public int getmNumber() {
        return mNumber;
    }

    public void setmNumber(int mNumber) {
        this.mNumber = mNumber;
    }

    public long getmCreated() {
        return mCreated;
    }

    public void setmCreated(long mCreated) {
        this.mCreated = mCreated;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmQuery() {
        return mQuery;
    }

    public void setmQuery(String mQuery) {
        this.mQuery = mQuery;
    }
}
