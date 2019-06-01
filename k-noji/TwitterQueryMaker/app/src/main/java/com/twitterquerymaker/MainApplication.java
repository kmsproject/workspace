package com.twitterquerymaker;

import android.app.Application;

public class MainApplication extends Application {
    private String title;
    private String query;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void clearObj(){
        title = null;
        query = null;
    }
}