package com.twitterquerymaker;

import android.widget.RelativeLayout;

public class RelativeLayoutTags {

    int mTagId = 0;
    String mType = null;
    RelativeLayout mRelativeLayout;
    boolean isVisible = false;

    public RelativeLayoutTags(int mTagId, String mType, RelativeLayout mRelativeLayout) {
        this.mTagId = mTagId;
        this.mType = mType;
        this.mRelativeLayout = mRelativeLayout;
        isVisible = true;
    }

    public int getmTagId() {
        return mTagId;
    }

    public void setmTagId(int mTagId) {
        this.mTagId = mTagId;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public RelativeLayout getmRelativeLayout() {
        return mRelativeLayout;
    }

    public void setmRelativeLayout(RelativeLayout mRelativeLayout) {
        this.mRelativeLayout = mRelativeLayout;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean visible) {
        isVisible = visible;
    }
}
