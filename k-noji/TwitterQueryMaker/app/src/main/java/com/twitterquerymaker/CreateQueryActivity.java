package com.twitterquerymaker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.os.strictmode.IntentReceiverLeakedViolation;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "TwitterQueryMaker";
    private int newId = 0;

    final String[] logicalItems = {"AND", "OR", "NOT"};

    final String[] logicalItemsString = {"", "", ""};
    final String[] languageItems = {"en", "ja"};
    final String[] languageItemsString = {"", ""};

    private Button saveButton;
    private ImageView cancelButton;
    private EditText titleEditText;

    //                                                    0       1        2       3        4        5
    private static final String[] resourceTypeItem = {"search", "user", "place", "time", "lang", "filter"};

    private LinearLayout searchLinearLayout;
    private LinearLayout userLinearLayout;
    private LinearLayout placeLinearLayout;
    private LinearLayout timeLinearLayout;
    private LinearLayout langLinearLayout;
    private LinearLayout filterLinearLayout;
    private RelativeLayout placeAddView;
    private RelativeLayout timeAddView;
    private RelativeLayout langAddView;
    private RelativeLayout filterAddView;

    private List<RelativeLayoutTags> tagsList = new ArrayList<RelativeLayoutTags>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_data_list);

        // デフォルトでキーワードのViewを追加する
        //LinearLayout searchLinearLayout = (LinearLayout)findViewById(R.id.search_linearlayout);
        //getLayoutInflater().inflate(R.layout.input_data_column_search_main, searchLinearLayout);

        logicalItemsString[0] = getString(R.string.logical_and_hint);
        logicalItemsString[1] = getString(R.string.logical_or_hint);
        logicalItemsString[2] = getString(R.string.logical_not_hint);
        languageItemsString[0] = getString(R.string.lang_item_en);
        languageItemsString[1] = getString(R.string.lang_item_ja);

        // 上部のView
        saveButton = (Button)findViewById(R.id.buttonSave);
        cancelButton = (ImageView)findViewById(R.id.imageViewCancel);
        titleEditText = (EditText)findViewById(R.id.editTextTitle);
        setEditTextListener(titleEditText);
        // Listenerの設定
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // Viewを追加するためのLinearLayout（Adder）
        searchLinearLayout = (LinearLayout)findViewById(R.id.search_linearlayout);
        userLinearLayout   = (LinearLayout)findViewById(R.id.user_linearlayout);
        placeLinearLayout  = (LinearLayout)findViewById(R.id.place_linearlayout);
        timeLinearLayout   = (LinearLayout)findViewById(R.id.time_linearlayout);
        langLinearLayout   = (LinearLayout)findViewById(R.id.lang_linearlayout);
        filterLinearLayout = (LinearLayout)findViewById(R.id.filter_linearlayout);

        // 各Adderの取得
        RelativeLayout searchAddView = (RelativeLayout) findViewById(R.id.layout_search_adder);
        RelativeLayout userAddView   = (RelativeLayout) findViewById(R.id.layout_user_adder);
        placeAddView  = (RelativeLayout) findViewById(R.id.layout_place_adder);
        timeAddView   = (RelativeLayout) findViewById(R.id.layout_time_adder);
        langAddView   = (RelativeLayout) findViewById(R.id.layout_lang_adder);
        filterAddView = (RelativeLayout) findViewById(R.id.layout_filter_adder);
        // AdderのListenerを設定
        searchAddView.setOnClickListener(this);
        userAddView.setOnClickListener(this);
        placeAddView.setOnClickListener(this);
        timeAddView.setOnClickListener(this);
        langAddView.setOnClickListener(this);
        filterAddView.setOnClickListener(this);

        layoutSearchAdderEvent();
    }


    /**
     * SearchAddを押したときのイベント
     * 初期状態で追加したいのでメソッド化する
     */
    private void layoutSearchAdderEvent() {
        // Resource取得
        final RelativeLayout layoutSearchMain = (RelativeLayout)getLayoutInflater().inflate(R.layout.input_data_column_search_main, null);
        searchLinearLayout.addView(layoutSearchMain);
        EditText editText = layoutSearchMain.findViewById(R.id.keyword);
        TextView logicalButton = layoutSearchMain.findViewById(R.id.logical);
        cancelButton = layoutSearchMain.findViewById(R.id.cancel);

        // Tagの設定
        layoutSearchMain.setId(newId);
        RelativeLayoutTags relativeLayoutTags = new RelativeLayoutTags(createLayoutTagId(), resourceTypeItem[0], layoutSearchMain);
        layoutSearchMain.setTag(relativeLayoutTags);
        tagsList.add(relativeLayoutTags);

        // Listenerの設定
        setEditTextListener(editText);
        setLogicalButtonListener(logicalButton);
        setCancelButtonListener(cancelButton, layoutSearchMain);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Log.d(TAG, "created id : " + newId);

        RelativeLayoutTags relativeLayoutTags;
        ImageView cancelButton;

        switch (id) {
            case R.id.buttonSave:
                String query = createQueryStrings();
                String title = titleEditText.getText().toString();

                MainApplication app = (MainApplication)CreateQueryActivity.this.getApplication();
                app.setTitle(title);
                app.setQuery(query);

                Intent intentOk = new Intent();
                setResult(RESULT_OK, intentOk);
                finish();
                break;

            case R.id.imageViewCancel:
                Intent intentCancel = new Intent();
                setResult(RESULT_CANCELED, intentCancel);
                finish();
                break;

            case R.id.layout_search_adder:
                Log.d(TAG, "onClick : R.id.layout_search_adder");
                layoutSearchAdderEvent();
                break;

            case R.id.layout_user_adder:
                Log.d(TAG, "onClick : R.id.layout_user_adder");

                // Resource取得
                final RelativeLayout layoutUserMain = (RelativeLayout)getLayoutInflater().inflate(R.layout.input_data_column_user_main, null);
                userLinearLayout.addView(layoutUserMain);
                EditText editText = layoutUserMain.findViewById(R.id.keyword);
                TextView logicalButton = layoutUserMain.findViewById(R.id.logical);
                cancelButton = layoutUserMain.findViewById(R.id.cancel);

                // Tagの設定
                layoutUserMain.setId(newId);
                relativeLayoutTags = new RelativeLayoutTags(createLayoutTagId(), resourceTypeItem[1], layoutUserMain);
                layoutUserMain.setTag(relativeLayoutTags);
                tagsList.add(relativeLayoutTags);

                // Listenerの設定
                setEditTextListener(editText);
                setLogicalButtonListener(logicalButton);
                setCancelButtonListener(cancelButton, layoutUserMain);
                break;

            case R.id.layout_place_adder:
                Log.d(TAG, "onClick : R.id.layout_place_adder");

                // Resource取得
                final RelativeLayout layoutPlaceMain = (RelativeLayout)getLayoutInflater().inflate(R.layout.input_data_column_place_main, null);
                placeLinearLayout.addView(layoutPlaceMain);
                EditText editTextPoint = layoutPlaceMain.findViewById(R.id.edit_text_place_point);
                EditText editTextRadius = layoutPlaceMain.findViewById(R.id.edit_text_place_radius);
                cancelButton = layoutPlaceMain.findViewById(R.id.cancel);
                // Adderレイアウトを隠す
                placeAddView.setVisibility(View.GONE);
                // Tagの設定
                layoutPlaceMain.setId(newId);
                relativeLayoutTags = new RelativeLayoutTags(createLayoutTagId(), resourceTypeItem[2], layoutPlaceMain);
                layoutPlaceMain.setTag(relativeLayoutTags);
                tagsList.add(relativeLayoutTags);

                // Listenerの設定
                setEditTextListener(editTextPoint);
                setEditTextListener(editTextRadius);
                setCancelButtonListener(cancelButton, layoutPlaceMain);
                break;

            case R.id.layout_time_adder:
                Log.d(TAG, "onClick : R.id.layout_time_adder");

                // Resource取得
                final RelativeLayout layoutTimeMain = (RelativeLayout)getLayoutInflater().inflate(R.layout.input_data_column_time_main, null);
                timeLinearLayout.addView(layoutTimeMain);
                TextView textViewFrom = layoutTimeMain.findViewById(R.id.start_day);
                TextView textViewAfter = layoutTimeMain.findViewById(R.id.end_day);
                cancelButton = layoutTimeMain.findViewById(R.id.cancel);
                // Adderレイアウトを隠す
                timeAddView.setVisibility(View.GONE);
                // 初期値の設定
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int endDay = c.get(Calendar.DAY_OF_MONTH);
                int startDay;
                if (endDay > 4) {
                    startDay = endDay - 3;
                } else {
                    startDay = 1;
                }
                setTimeParam(textViewFrom, year, month, startDay);
                setTimeParam(textViewAfter, year, month, endDay);

                // Tagの設定
                layoutTimeMain.setId(newId);
                relativeLayoutTags = new RelativeLayoutTags(createLayoutTagId(), resourceTypeItem[3], layoutTimeMain);
                layoutTimeMain.setTag(relativeLayoutTags);
                tagsList.add(relativeLayoutTags);

                // Listenerの設定
                setDataPickerDialogListener(textViewFrom);
                setDataPickerDialogListener(textViewAfter);
                setCancelButtonListener(cancelButton, layoutTimeMain);
                break;

            case R.id.layout_lang_adder:
                Log.d(TAG, "onClick : R.id.layout_lang_adder");

                // Resource取得
                final RelativeLayout layoutLangMain = (RelativeLayout)getLayoutInflater().inflate(R.layout.input_data_column_lang_main, null);
                langLinearLayout.addView(layoutLangMain);
                TextView textViewLang = layoutLangMain.findViewById(R.id.lang);
                cancelButton = layoutLangMain.findViewById(R.id.cancel);
                // Adderレイアウトを隠す
                langAddView.setVisibility(View.GONE);

                // 初期値の設定
                textViewLang.setText(languageItemsString[1]);
                textViewLang.setTag(languageItems[1]);

                // Tagの設定
                layoutLangMain.setId(newId);
                relativeLayoutTags = new RelativeLayoutTags(createLayoutTagId(), resourceTypeItem[4], layoutLangMain);
                layoutLangMain.setTag(relativeLayoutTags);
                tagsList.add(relativeLayoutTags);

                // Listenerの設定
                setLanguageListener(textViewLang);
                setCancelButtonListener(cancelButton, layoutLangMain);
                break;

            case R.id.layout_filter_adder:
                Log.d(TAG, "onClick : R.id.layout_filter_adder");

                // Adderレイアウトを隠す
                filterAddView.setVisibility(View.GONE);

                // Resource取得
                final RelativeLayout layoutFilterMain = (RelativeLayout)getLayoutInflater().inflate(R.layout.input_data_column_filter_main, null);
                filterLinearLayout.addView(layoutFilterMain);
                cancelButton = layoutFilterMain.findViewById(R.id.cancel);
                TextView textView1 = layoutFilterMain.findViewById(R.id.logical_links);
                TextView textView2 = layoutFilterMain.findViewById(R.id.logical_verify);
                TextView textView3 = layoutFilterMain.findViewById(R.id.logical_images);
                TextView textView4 = layoutFilterMain.findViewById(R.id.logical_video);
                TextView textView5 = layoutFilterMain.findViewById(R.id.logical_gif);
                TextView textView6 = layoutFilterMain.findViewById(R.id.logical_media);
                TextView textView7 = layoutFilterMain.findViewById(R.id.logical_news);
                TextView textView8 = layoutFilterMain.findViewById(R.id.logical_safe);
                TextView textView9 = layoutFilterMain.findViewById(R.id.logical_retweet);
                TextView textViewA = layoutFilterMain.findViewById(R.id.logical_faves);
                TextView textViewB = layoutFilterMain.findViewById(R.id.logical_reply);

                // Tagの設定
                layoutFilterMain.setId(newId);
                relativeLayoutTags = new RelativeLayoutTags(createLayoutTagId(), resourceTypeItem[5], layoutFilterMain);
                layoutFilterMain.setTag(relativeLayoutTags);
                tagsList.add(relativeLayoutTags);

                // Listenerの設定
                setCancelButtonListener(cancelButton, layoutFilterMain);
                setLogicalButtonListener(textView1);
                setLogicalButtonListener(textView2);
                setLogicalButtonListener(textView3);
                setLogicalButtonListener(textView4);
                setLogicalButtonListener(textView5);
                setLogicalButtonListener(textView6);
                setLogicalButtonListener(textView7);
                setLogicalButtonListener(textView8);
                setLogicalButtonListener(textView9);
                setLogicalButtonListener(textViewA);
                setLogicalButtonListener(textViewB);

                break;
            default:
                Log.d(TAG, "onClick");
                break;
        }


    }

    // IDの割り振り
    private int createLayoutTagId(){
        if (newId == 0) {
            newId++;
            return 0;
        }
        newId++;
        return newId;
    }

    private void setEditTextListener(final EditText editText) {
        /*
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        */
    }

    /**
     * 論理演算子のリスナー設定
     * @param textView
     */
    private void setLogicalButtonListener(final TextView textView) {
        textView.setTag("%20");
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CreateQueryActivity.this)
                        .setItems(logicalItemsString, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                textView.setText(logicalItems[which]);
                                switch (which){
                                    case 0:
                                        textView.setTag("%20");
                                        break;
                                    case 1:
                                        textView.setTag("%20OR%20");
                                        break;
                                    case 2:
                                        textView.setTag("%20-");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * キャンセルボタンのリスナー設定
     * @param imageView
     * @param relativeLayout
     */
    private void setCancelButtonListener(ImageView imageView, final RelativeLayout relativeLayout) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.removeAllViews();
                RelativeLayoutTags tmp = (RelativeLayoutTags)relativeLayout.getTag();
                tmp.setIsVisible(false);
                if (tmp.getmType().equals(resourceTypeItem[2])) {
                    placeAddView.setVisibility(View.VISIBLE);
                } else if (tmp.getmType().equals(resourceTypeItem[3])) {
                    timeAddView.setVisibility(View.VISIBLE);
                } else if (tmp.getmType().equals(resourceTypeItem[4])) {
                    langAddView.setVisibility(View.VISIBLE);
                } else if (tmp.getmType().equals(resourceTypeItem[5])) {
                    filterAddView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 日付選択ダイアログのリスナー設定
     * @param textView
     */
    private void setDataPickerDialogListener(final TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        setTimeParam(textView, year, month, dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateQueryActivity.this, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    /**
     * 日付のTagとTextの設定
     * @param textView
     * @param year
     * @param month
     * @param day
     */
    private void setTimeParam(TextView textView, int year, int month, int day) {
        textView.setText(year + getString(R.string.time_year) + ((int)month + 1) + getString(R.string.time_month) + day + getString(R.string.time_day));
        textView.setTag(year + "-" + ((int)month + 1) + "-" + day);
    }

    /**
     * 言語選択ダイアログのリスナー設定
     * @param textView
     */
    private void setLanguageListener(final TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CreateQueryActivity.this)
                        .setItems(languageItemsString, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                textView.setText(languageItemsString[which]);
                                textView.setTag(languageItems[which]);
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * 検索クエリの作成
     * @return
     */
    private String createQueryStrings() {
        Log.d(TAG, "createQueryStrings()");
        // 言語、キーワード、ユーザー、日付、場所、フィルターの順番
        String qLang = "";
        String qKeyword = "";
        String qUser = "";
        String qDay = "";
        String qPlace = "";
        String qFilter = "";


        RelativeLayoutTags relativeLayoutTags;

        for (int i = 0; i < newId; i++) {
            relativeLayoutTags =  tagsList.get(i);
            String tmp;

            if (relativeLayoutTags.getIsVisible()){
                Log.d(TAG, "Layout tags " + i + " :" + relativeLayoutTags.getmType());

                if (relativeLayoutTags.getmType().equals(resourceTypeItem[0])) {
                    // リソース取得
                    RelativeLayout layoutSearchMain = relativeLayoutTags.getmRelativeLayout();
                    EditText editText = layoutSearchMain.findViewById(R.id.keyword);
                    TextView logicalButton = layoutSearchMain.findViewById(R.id.logical);
                    CheckBox checkBox = layoutSearchMain.findViewById(R.id.perfect);

                    // 文字列作成
                    tmp = replaceString(editText.getText().toString());
                    if (checkBox.isChecked()) {
                        tmp = "\"" + tmp + "\"";
                    }
                    tmp = logicalButton.getTag().toString() + tmp;

                    // 結合
                    qKeyword = qKeyword + tmp;

                } else if (relativeLayoutTags.getmType().equals(resourceTypeItem[1])) {
                    // リソース取得
                    RelativeLayout layoutUserMain = relativeLayoutTags.getmRelativeLayout();
                    EditText editText = layoutUserMain.findViewById(R.id.keyword);
                    TextView logicalButton = layoutUserMain.findViewById(R.id.logical);
                    RadioButton radioButton1 = layoutUserMain.findViewById(R.id.is_from);

                    // 文字列作成
                    tmp = replaceString(editText.getText().toString());
                    if (radioButton1.isChecked()) {
                        tmp = "from%3A" + tmp;
                    } else {
                        tmp = "to%3A" + tmp;
                    }
                    tmp = logicalButton.getTag().toString() + tmp;

                    // 結合
                    qUser = qUser + tmp;

                } else if (relativeLayoutTags.getmType().equals(resourceTypeItem[2])) {
                    // リソース取得
                    RelativeLayout layoutPlaceMain = relativeLayoutTags.getmRelativeLayout();
                    EditText editTextPoint = layoutPlaceMain.findViewById(R.id.edit_text_place_point);
                    EditText editTextRadius = layoutPlaceMain.findViewById(R.id.edit_text_place_radius);

                    // 文字列作成
                    tmp = "%20near%3A" + editTextPoint.getText().toString() + "%20within%3A"
                            + editTextRadius.getText().toString() + "km";

                    // 結合
                    qPlace = tmp;

                } else if (relativeLayoutTags.getmType().equals(resourceTypeItem[3])) {
                    // リソース取得
                    RelativeLayout layoutTimeMain = relativeLayoutTags.getmRelativeLayout();
                    TextView textView1 = layoutTimeMain.findViewById(R.id.start_day);
                    TextView textView2 = layoutTimeMain.findViewById(R.id.end_day);

                    // 文字列作成
                    // %20since%3A2019-02-19%20until%3A2019-02-26
                    tmp = "%20since%3A" + textView1.getTag().toString() + "%20until%3A" + textView2.getTag().toString();

                    // 結合
                    qDay = tmp;

                } else if (relativeLayoutTags.getmType().equals(resourceTypeItem[4])) {
                    // リソース取得
                    RelativeLayout layoutLangMain = relativeLayoutTags.getmRelativeLayout();
                    TextView textView1 = layoutLangMain.findViewById(R.id.lang);

                    // 文字列作成
                    tmp = "l=" + textView1.getTag().toString();

                    // 結合
                    qLang = tmp + "&";

                } else if (relativeLayoutTags.getmType().equals(resourceTypeItem[5])) {
                    // リソース取得
                    RelativeLayout layoutFilterMain = relativeLayoutTags.getmRelativeLayout();
                    TextView textView1 = layoutFilterMain.findViewById(R.id.logical_links);
                    TextView textView2 = layoutFilterMain.findViewById(R.id.logical_verify);
                    TextView textView3 = layoutFilterMain.findViewById(R.id.logical_images);
                    TextView textView4 = layoutFilterMain.findViewById(R.id.logical_video);
                    TextView textView5 = layoutFilterMain.findViewById(R.id.logical_gif);
                    TextView textView6 = layoutFilterMain.findViewById(R.id.logical_media);
                    TextView textView7 = layoutFilterMain.findViewById(R.id.logical_news);
                    TextView textView8 = layoutFilterMain.findViewById(R.id.logical_safe);
                    TextView textView9 = layoutFilterMain.findViewById(R.id.logical_retweet);
                    TextView textViewA = layoutFilterMain.findViewById(R.id.logical_faves);
                    TextView textViewB = layoutFilterMain.findViewById(R.id.logical_reply);
                    CheckBox checkBox1 = layoutFilterMain.findViewById(R.id.is_links);
                    CheckBox checkBox2 = layoutFilterMain.findViewById(R.id.is_verify);
                    CheckBox checkBox3 = layoutFilterMain.findViewById(R.id.is_images);
                    CheckBox checkBox4 = layoutFilterMain.findViewById(R.id.is_video);
                    CheckBox checkBox5 = layoutFilterMain.findViewById(R.id.is_gif);
                    CheckBox checkBox6 = layoutFilterMain.findViewById(R.id.is_media);
                    CheckBox checkBox7 = layoutFilterMain.findViewById(R.id.is_news);
                    CheckBox checkBox8 = layoutFilterMain.findViewById(R.id.is_safe);
                    CheckBox checkBox9 = layoutFilterMain.findViewById(R.id.is_retweet);
                    CheckBox checkBoxA = layoutFilterMain.findViewById(R.id.is_faves);
                    CheckBox checkBoxB = layoutFilterMain.findViewById(R.id.is_reply);
                    EditText editText1 = layoutFilterMain.findViewById(R.id.num_retweet);
                    EditText editText2 = layoutFilterMain.findViewById(R.id.num_faves);
                    EditText editText3 = layoutFilterMain.findViewById(R.id.num_reply);

                    // 文字列作成
                    tmp = "";
                    if (checkBox1.isChecked()) {
                        tmp = tmp + textView1.getTag().toString() + "filter%3Alinks";
                    }
                    if (checkBox2.isChecked()) {
                        tmp = tmp + textView2.getTag().toString() + "filter%3Averified";
                    }
                    if (checkBox3.isChecked()) {
                        tmp = tmp + textView3.getTag().toString() + "filter%3Aimages";
                    }
                    if (checkBox4.isChecked()) {
                        tmp = tmp + textView4.getTag().toString() + "filter%3Avideos";
                    }
                    if (checkBox5.isChecked()) {
                        tmp = tmp + textView5.getTag().toString() + "card_name%3Aanimated_gif";
                    }
                    if (checkBox6.isChecked()) {
                        tmp = tmp + textView6.getTag().toString() + "filter%3Amedia";
                    }
                    if (checkBox7.isChecked()) {
                        tmp = tmp + textView7.getTag().toString() + "filter%3Anews";
                    }
                    if (checkBox8.isChecked()) {
                        tmp = tmp + textView8.getTag().toString() + "filter%3Asafe";
                    }
                    if (checkBox9.isChecked()) {
                        tmp = tmp + textView9.getTag().toString() + "min_retweets%3A" + replaceString(editText1.getText().toString());
                    }
                    if (checkBoxA.isChecked()) {
                        tmp = tmp + textViewA.getTag().toString() + "min_faves%3A" + replaceString(editText1.getText().toString());
                    }
                    if (checkBoxB.isChecked()) {
                        tmp = tmp + textViewB.getTag().toString() + "min_replies%3A" + replaceString(editText1.getText().toString());
                    }

                    // 結合
                    qFilter = tmp;
                }
            }
        }
        String result = "https://twitter.com/search?" + qLang + "q=" + qKeyword + qUser + qDay + qPlace + qFilter;
        Log.d(TAG, "Result string :" + result);
        return result;
    }

    // 無効文字の置き換え
    private String replaceString(String s) {
        if (s == null) {
            return null;
        }
        s = s.replace(" ", "%20");
        //str = s.replace("!", "%21");
        //str = s.replace(""", "%22");
        s = s.replace("#", "%23");
        s = s.replace("$", "%24");
        s = s.replace("%", "%25");
        s = s.replace("&", "%26");
        s = s.replace("'", "%27");
        //s = s.replace("(", "%28");
        //s = s.replace(")", "%29");
        //s = s.replace("*", "%2A");
        //s = s.replace("+", "%2B");
        s = s.replace(",", "%2C");
        s = s.replace("/", "%2F");
        s = s.replace(":", "%3A");
        s = s.replace(";", "%3B");
        //str = s.replace("<", "%3C");
        //str = s.replace("=", "%3D");
        //str = s.replace(">", "%3E");
        //str = s.replace("?", "%3F");
        s = s.replace("@", "%40");
        s = s.replace("[", "%5B");
        s = s.replace("]", "%5D");
        s = s.replace("^", "%5E");
        s = s.replace("`", "%60");
        s = s.replace("{", "%7B");
        s = s.replace("|", "%7C");
        s = s.replace("}", "%7D");
        //str = s.replace("~", "%7E");
        return s;
    }

}

