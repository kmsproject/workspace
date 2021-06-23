package com.twitterquerymaker;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
    ListAdapter listAdapter;

    private int number = 0;
    private long deleteCreated = 0;
    private int tappedPosition = 0;

    public enum sortMethod{
        created_asc(1),
        created_desc(2),
        title_asc(3),
        title_desc(4);

        private int sortMethodInt;
        sortMethod(int sortMethodInt) {
            this.sortMethodInt = sortMethodInt;
        }
        public int getInt() {
            return this.sortMethodInt;
        }
    }
    private sortMethod useSortMethod;

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

        // SharedPreferences読み込み
        SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);
        int sortMethodInt = data.getInt("DataInt", 0);
        Log.d(TAG, "onCreate: SharedPreferences load useSortMethod(" + sortMethodInt + ")");
        if (sortMethodInt == 1) {
            useSortMethod = sortMethod.created_asc;
        } else if(sortMethodInt == 2) {
            useSortMethod = sortMethod.created_desc;
        } else if(sortMethodInt == 3) {
            useSortMethod = sortMethod.title_asc;
        } else {
            useSortMethod = sortMethod.title_desc;
        }

        readData();
        addListView(MainActivity.this);
        setListViewListener();
    }

    /**
     * タップされたリストアイテムのセッター
     * @param position
     */
    private void setPosition(int position){
        tappedPosition = position;
    }

    /**
     * タップされたリストアイテムのゲッター
     * @return
     */
    private int getPosition(){
        return tappedPosition;
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
                Long created = System.currentTimeMillis();
                insertData(db, number, created, title, query);
                Log.d(TAG, "resultCode : OK");
                readData();
                addListView(this);
                setListViewListener();
            }
        }
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
        // 並び替え順を指定(ListView格納時に並び順が逆になるので、逆に設定)
        String order_by = "";
        if (useSortMethod == sortMethod.created_asc) {
            order_by = "created DESC";
        } else if (useSortMethod == sortMethod.created_desc){
            order_by = "created ASC";
        } else if (useSortMethod == sortMethod.title_asc) {
            order_by = "title DESC";
        } else {
            order_by = "title ASC";
        }

        // DBから読み込み
        Cursor cursor = db.query(
                "tqdb",
                new String[] { "number", "created", "title", "squery" },
                null,
                null,
                null,
                null,
                order_by
        );
        ListItem item;
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (!cursor.getString(2).equals(null)) {
                item = new ListItem(cursor.getInt(0), cursor.getLong(1),
                        cursor.getString(2), cursor.getString(3));
                listItem.add(item);
                cursor.moveToNext();
                Log.d(TAG, "Read item :" +  item.getmNumber() + ", " + item.getmCreated() +
                        ", " + item.getmTitle() + ", " + item.getmQuery());
            }
        }
        number = cursor.getCount();
        cursor.close();
    }

    /**
     * ListActivityに表示する
     * @param context
     */
    public void addListView(final Context context) {
        // ファイルをリストに追加
        listAdapter = new ListAdapter(context, R.layout.query_list_column, listItem);
        listView.setAdapter(listAdapter);
        setDefaultMassage();
    }

    /**
     * データベースに登録
     * @param db
     * @param number
     * @param title
     * @param squery
     */
    private void insertData(SQLiteDatabase db, int number, long created, String title, String squery){

        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("created", created);
        values.put("title", title);
        values.put("squery", squery);

        db.insert("tqdb", null, values);
        Log.d(TAG, "Insert data : " + number + ", " + created + ", " + title + ", " + squery);
    }

    /**
     * データベースから削除
     */
    private void deleteData(SQLiteDatabase db, long dCreated){

        String str = String.valueOf(dCreated);
        ContentValues values = new ContentValues();
        values.put("created", str);

        db.delete("tqdb", "created=?", new String[]{str});

        readData();
        addListView(this);
        setListViewListener();

        Log.d(TAG, "Delete from Database : " + dCreated);

    }

    /**
     * ListViewのListenerを登録
     */
    private void setListViewListener () {
        // リストをタップしたときの処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // タップしたアイテムの取得
                ListView listView = (ListView) parent;
                ListItem listItem = (ListItem) listView.getItemAtPosition(position);

                setPosition(listItem.getmNumber());
                String title = listItem.getmTitle();
                String squery = listItem.getmQuery();

                Log.d(TAG, "Listview item clicked : " + getPosition() + ", " + title + ", " + squery);

                // Twitterのリンクのインテントを作成
                Uri uri = Uri.parse(squery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return;
            }
        });

        // 長押し時に表示するダイアログの設定
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deleteCreated != 0){
                            deleteData(db, deleteCreated);
                        } else {
                            Toast.makeText(MainActivity.this, "検索ワードは削除できませんでした", Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "Deleted Item : " + deleteCreated);
                    }
                });
        builder.setNegativeButton(R.string.delete_dialog_cancel, null);

        // リストを長押ししたときの処理
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // タップしたアイテムの取得
                ListView listView = (ListView) parent;
                ListItem listItem = (ListItem) listView.getItemAtPosition(position);

                setPosition(listItem.getmNumber());
                deleteCreated = listItem.getmCreated();

                Log.d(TAG, "Listview item long clicked : " + getPosition());
                builder.show();

                return true;
            }
        });
    }

    public void setDefaultMassage() {
        LinearLayout defaultMassage = (LinearLayout)findViewById(R.id.defaultMassage);
        if (listItem.isEmpty()) {
            defaultMassage.setVisibility(View.VISIBLE);
            Log.d(TAG, "setDefaultMassage: listItem is Empty");
        } else {
            defaultMassage.setVisibility(View.GONE);
            Log.d(TAG, "setDefaultMassage: listItem is Visible");
        }
    }

    /**
     * オプションボタンの作成
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * オプションメニューの中身を選択
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // SharedPreferences書き込み
        SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= data.edit();
        int id = item.getItemId();
        if (id == R.id.item_sort_create) {
            if (useSortMethod == sortMethod.title_asc || useSortMethod == sortMethod.title_desc) {
                useSortMethod = sortMethod.created_asc;
            }
            if (useSortMethod == sortMethod.created_asc) {
                useSortMethod = sortMethod.created_desc;
            } else {
                useSortMethod = sortMethod.created_asc;
            }
            Log.d(TAG, "onOptionsItemSelected: useSortMethod(" + useSortMethod.getInt() + ")");
            editor.putInt("DataInt", useSortMethod.getInt());
            editor.apply();

            readData();
            addListView(this);
            setListViewListener();
            return true;
        } else if (id == R.id.item_sort_name) {
            if (useSortMethod == sortMethod.created_asc || useSortMethod == sortMethod.created_desc) {
                useSortMethod = sortMethod.title_asc;
            }
            if (useSortMethod == sortMethod.title_asc) {
                useSortMethod = sortMethod.title_desc;
            } else {
                useSortMethod = sortMethod.title_asc;
            }
            Log.d(TAG, "onOptionsItemSelected: useSortMethod(" + useSortMethod.getInt() + ")");
            editor.putInt("DataInt", useSortMethod.getInt());
            editor.apply();

            readData();
            addListView(this);
            setListViewListener();
            return true;
        }
        /*
        else if (id == R.id.item_how_to_use) {
            // 使い方を説明するアクティビティを表示する
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }
}
