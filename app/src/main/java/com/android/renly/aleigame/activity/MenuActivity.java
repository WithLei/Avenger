package com.android.renly.aleigame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.android.renly.aleigame.R;

public class MenuActivity extends FragmentActivity implements View.OnClickListener{
    private Button btn_easy_mode;
    private Button btn_hard_mode;
    private Button btn_fxxk_mode;
    private Button btn_skin;

    private Button btn_config;

    private static final int LEAVE_MENUPAGE_EASY = 1;
    private static final int LEAVE_MENUPAGE_HARD = 2;
    private static final int LEAVE_MENUPAGE_FXXK = 3;
    private static final int LEAVE_MENUPAGE_SKIN = 4;
    private static final int LEAVE_MENUPAGE_CONFIG = 5;
    //默认初始皮肤为DJ
    private String skin = "DJ";


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = null;
            switch (msg.what) {
                case LEAVE_MENUPAGE_EASY:
                    intent = new Intent(MenuActivity.this,TicketTextActivity.class);
                    intent.putExtra("difficulty","easy");
                    intent.putExtra("skin",skin);
                    break;
                case LEAVE_MENUPAGE_HARD:
                    intent = new Intent(MenuActivity.this,TicketTextActivity.class);
                    intent.putExtra("difficulty","hard");
                    intent.putExtra("skin",skin);
                    break;
                case LEAVE_MENUPAGE_FXXK:
                    intent = new Intent(MenuActivity.this,TicketTextActivity.class);
                    intent.putExtra("difficulty","fxxk");
                    intent.putExtra("skin",skin);
                    break;
                case LEAVE_MENUPAGE_SKIN:
                    intent = new Intent(MenuActivity.this,SkinActivity.class);
                    break;
                case LEAVE_MENUPAGE_CONFIG:
                    intent = new Intent(MenuActivity.this,ConfigActivity.class);
                    intent.putExtra("skin",skin);
                    break;
            }
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        initView();
        initListener();
    }

    private void initListener() {
        btn_easy_mode.setOnClickListener(this);
        btn_hard_mode.setOnClickListener(this);
        btn_fxxk_mode.setOnClickListener(this);
        btn_skin.setOnClickListener(this);
        btn_config.setOnClickListener(this);
    }

    private void initView() {
        btn_easy_mode = findViewById(R.id.btn_easy_mode);
        btn_hard_mode = findViewById(R.id.btn_hard_mode);
        btn_fxxk_mode = findViewById(R.id.btn_fxxk_mode);
        btn_skin = findViewById(R.id.btn_skin);
        btn_config = findViewById(R.id.btn_config);

    }

    private int getLayoutId(){
        return R.layout.activity_menu;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_easy_mode:
                handler.sendEmptyMessage(LEAVE_MENUPAGE_EASY);
                break;
            case R.id.btn_hard_mode:
                handler.sendEmptyMessage(LEAVE_MENUPAGE_HARD);
                break;
            case R.id.btn_fxxk_mode:
                handler.sendEmptyMessage(LEAVE_MENUPAGE_FXXK);
                break;
            case R.id.btn_skin:
                handler.sendEmptyMessage(LEAVE_MENUPAGE_SKIN);
                break;
            case R.id.btn_config:
                handler.sendEmptyMessage(LEAVE_MENUPAGE_CONFIG);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
