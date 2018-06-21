package com.android.renly.aleigame.activity;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.renly.aleigame.R;
import com.android.renly.aleigame.adt.CustomRecyclerViewAdapter;
import com.android.renly.aleigame.db.MySQLiteOpenHelper;
import com.android.renly.aleigame.entity.UserScore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import shivam.developer.featuredrecyclerview.FeatureLinearLayoutManager;
import shivam.developer.featuredrecyclerview.FeaturedRecyclerView;

import static com.android.renly.aleigame.db.MySQLiteOpenHelper.TABLE_NAME;

public class LeaderboardActivity extends FragmentActivity {
    List<UserScore> userScores = new ArrayList<>(100);
    List<String> scores = new ArrayList<>(100);
    FeaturedRecyclerView featuredRecyclerView;
    private static MySQLiteOpenHelper mySQLiteOpenHelper;
    private static SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setContentView(getLayoutID());
        queryDB();
        createDummyDataList();
        initView();
    }

    private void queryDB() {
        mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(this);
        db = mySQLiteOpenHelper.getWritableDatabase();
        //插入测试
//        insertDB(db);
        if (!db.isOpen())
            db = mySQLiteOpenHelper.getReadableDatabase();
        db.beginTransaction();
        synchronized (mySQLiteOpenHelper) {
            //开启查询 获得游标
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            //判断游标是否为空
            if (cursor.moveToFirst()) {
                //遍历游标
                do {
//            Log.e("TAG","i == " + i + "cursor.getcount() ==  " + cursor.getCount());
                    //获得用户名
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    //获得用户分数
                    int score = cursor.getInt(cursor.getColumnIndex("score"));
                    //test输出
                    Log.e("PRINT", "name = " + name + " score = " + score);

                    UserScore temp = new UserScore(name, score);
                    userScores.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.endTransaction();
        db.close();
        mySQLiteOpenHelper.close();
    }

    private void insertDB(SQLiteDatabase db) {
        synchronized (mySQLiteOpenHelper) {
            db.beginTransaction();

            UserScore temp1 = new UserScore("爱吃鸡魔人", 2500);
            UserScore temp2 = new UserScore("啊磊", 9998);

            db.execSQL(insertSql(temp1));
            db.execSQL(insertSql(temp2));

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }

    private String insertSql(UserScore temp) {
        return "insert into " + TABLE_NAME + "(name,score) values('" + temp.getUserName() + "','" + temp.getScore() + "')";
    }

    private void initView() {
        featuredRecyclerView = (FeaturedRecyclerView) findViewById(R.id.featured_recycler_view);

        FeatureLinearLayoutManager layoutManager = new FeatureLinearLayoutManager(this);
        featuredRecyclerView.setLayoutManager(layoutManager);

        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter();

        adapter.swapData(scores);

        featuredRecyclerView.setAdapter(adapter);
    }

    public int getLayoutID() {
        return R.layout.activity_leaderboard;
    }

    private void createDummyDataList() {
        //分数排序
        Collections.sort(userScores, new Comparator<UserScore>() {
            @Override
            public int compare(UserScore a, UserScore b) {
                if (a.getScore() > b.getScore()) return -1;
                else return 1;
            }
        });

        for (int i = 1; i <= 40; i++) {
            if (i <= 3 && userScores.size() >= i)
                scores.add("\u265A " + userScores.get(i - 1).getUserName() + "    " + userScores.get(i - 1).getScore());
            else if (i > 3 && userScores.size() >= i)
                scores.add(userScores.get(i - 1).getUserName() + "    " + userScores.get(i - 1).getScore());
            else if (i <= 3)
                scores.add("\u265A ----    ----");
            else
                scores.add(" ----    ----");
        }
    }
}
