package com.twitterquerymaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<ListItem> {

    private int mResource;
    private List<ListItem> mItems;
    private LayoutInflater mInflater;

    public ListAdapter(Context context, int resource, List<ListItem> items){
        super(context, resource, items);

        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(mResource, null);
        }

        ListItem items = mItems.get(position);

        // タイトルを設定
        TextView title = (TextView)view.findViewById(R.id.title);
        if (items.getmTitle() != null) {
            title.setText(items.getmTitle());
        }

        // クエリを設定
        TextView query = (TextView)view.findViewById(R.id.squery);
        if (items.getmQuery() != null) {
            query.setText(items.getmQuery());
        }

        return view;
    }
}
