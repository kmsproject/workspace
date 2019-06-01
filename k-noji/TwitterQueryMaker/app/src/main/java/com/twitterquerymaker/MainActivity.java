package com.twitterquerymaker;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String APP_TITLE = "Twitter 検索ワードメーカー";
    private final String TAG = "TwitterQueryMaker";

    private FloatingActionButton fab;
    private ListView listView;

    private SQLOpenHelper helper;
    private SQLiteDatabase db;

    private final static int RESULT_QUERY = 1001;

    ArrayList<ListItem>listItem = new ArrayList<>();
    ListAdapter adapter;

    private int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(APP_TITLE);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateQueryActivity.class);
                startActivityForResult(intent,RESULT_QUERY);
            }
        });

        readData();
        addListView(MainActivity.this);
        setListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // TODO Listの削除と並び替えを追加する
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * CreateQueryActivityから帰ってくる値を受け取る
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_QUERY) {
            MainApplication app = (MainApplication)MainActivity.this.getApplication();
            if (resultCode != Activity.RESULT_OK) {
                Log.d(TAG, "resultCode : Canceled");
                app.clearObj();
                return;
            } else if (resultCode == Activity.RESULT_OK) {

                if(helper == null){
                    helper = new SQLOpenHelper(getApplicationContext());
                }
                if(db == null){
                    db = helper.getWritableDatabase();
                }

                String title = app.getTitle();
                String query = app.getQuery();
                insertData(db, number, title, query);
                readData();
                addListView(this);
                setListViewListener();
            }
        }
    }

    /**
     * ListActivityに表示する
     * @param context
     */
    public void addListView(final Context context) {
        // ファイルをリストに追加
        adapter = new ListAdapter(context, R.layout.query_list_column, listItem);
        listView.setAdapter(adapter);
    }

    /**
     * データベースを読み込み
     */
    private void readData(){
        listItem = new ArrayList<>();
        if(helper == null){
            helper = new SQLOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        Cursor cursor = db.query(
                "tqdb",
                new String[] { "number", "title", "squery" },
                null,
                null,
                null,
                null,
                null
        );
        ListItem item;
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (!cursor.getString(2).equals(null)) {
                item = new ListItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                listItem.add(item);
                cursor.moveToNext();
                Log.d(TAG, "Read item :" +  item.getmNumber() +  ", " + item.getmTitle() + ", " + item.getmQuery());
            }
        }
        number = cursor.getCount();
        cursor.close();
    }

    private void deleteData() {
    }

    /**
     * データベースに登録
     * @param db
     * @param number
     * @param title
     * @param squery
     */
    private void insertData(SQLiteDatabase db, int number, String title, String squery){

        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("title", title);
        values.put("squery", squery);

        db.insert("tqdb", null, values);
        Log.d(TAG, "Insert data : " + number + ", " + title + ", " + squery);
    }

    /**
     * ListViewのListenerを登録
     */
    private void setListViewListener () {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // タップしたアイテムの取得
                ListView listView = (ListView) parent;
                ListItem item = (ListItem) listView.getItemAtPosition(position);

                int number = item.getmNumber();
                String title = item.getmTitle();
                String squery = item.getmQuery();

                Log.d(TAG, "Listview item clicked : " + number + ", " + title + ", " + squery);

                // Twitterのリンクのインテントを作成
                Uri uri = Uri.parse(squery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

}
