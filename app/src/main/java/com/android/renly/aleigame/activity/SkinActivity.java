package com.android.renly.aleigame.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.renly.aleigame.R;
import com.jaouan.compoundlayout.CompoundLayout;

public class SkinActivity extends Activity {

    private TextView subtitleTextView;
    private TextView tv_intro;

    private LinearLayout ll_skin;

    private View descriptionLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_skin);

        subtitleTextView =  findViewById(R.id.subtitle);
        descriptionLayout = findViewById(R.id.description_layout);
        tv_intro = findViewById(R.id.tv_intro);
        ll_skin = findViewById(R.id.ll_skin);

        bindCompoundListener( findViewById(R.id.profile_1), R.string.firstName, R.string.firstIntro, R.drawable.djbackground, 1);
        bindCompoundListener( findViewById(R.id.profile_2), R.string.secondName, R.string.secondIntro,R.drawable.dvabackground, 2);
        bindCompoundListener( findViewById(R.id.profile_3), R.string.thirdName, R.string.thirdIntro,R.drawable.soldierbackground, 3);
        bindCompoundListener( findViewById(R.id.profile_4), R.string.fourthName, R.string.fourthIntro,R.drawable.tracerbackground, 4);
        bindCompoundListener( findViewById(R.id.profile_5), R.string.fifthName, R.string.fifthIntro,R.drawable.menubackground2, 5);
    }

    /**
     * Bind compound listener.
     *
     * @param compoundLayout Compound layout.
     * @param subtitle       Subtitle to set.
     * @param subintro Subintro to set
     */
    private void bindCompoundListener(final CompoundLayout compoundLayout, @StringRes final int subtitle, @StringRes final int subintro, final int subdrawable, int id) {
        compoundLayout.setOnCheckedChangeListener(new CompoundLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundLayout compoundLayout, boolean checked) {
                if (checked) {
                    final Animation fadeOutAnimation = AnimationUtils.loadAnimation(SkinActivity.this, android.R.anim.fade_out);
                    fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            subtitleTextView.setText(getString(subtitle));
                            tv_intro.setText(getString(subintro));
                            ll_skin.setBackground(getResources().getDrawable(subdrawable));
                            descriptionLayout.startAnimation(AnimationUtils.loadAnimation(SkinActivity.this, android.R.anim.fade_in));
                            SharedPreferences sharedPreferences = getSharedPreferences("skin",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            //存入数据
                            String temp = null;
                            switch(id){
                                case 1:
                                    temp = "DJ";
                                    break;
                                case 2:
                                    temp = "dva";
                                    break;
                                case 3:
                                    temp = "soldier";
                                    break;
                                case 4:
                                    temp = "tracer";
                                    break;
                                case 5:
                                    temp = "angle";
                                    break;
                                default:
                                    temp = "DJ";
                                    break;
                            }
                            editor.putString("skin",temp);
                            //提交修改
                            editor.commit();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    descriptionLayout.startAnimation(fadeOutAnimation);
                }
            }
        });
    }
}
