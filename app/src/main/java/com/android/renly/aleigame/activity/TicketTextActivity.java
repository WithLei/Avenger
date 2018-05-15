package com.android.renly.aleigame.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.opengl.GLES20;

import com.android.renly.aleigame.MainActivity;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TickerText;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TicketTextActivity extends SimpleBaseGameActivity{
    // ===========================================================
    // Constants
    // ===========================================================

    private static final int CAMERA_WIDTH = 590;
    private static final int CAMERA_HEIGHT = 359;

    // ===========================================================
    // Fields
    // ===========================================================

    private Font mFont;


    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources() {
        this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
        this.mFont.load();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

        final Text text = new TickerText(0, 0, this.mFont,
                "角色介绍：\n卢西奥：\n主角操作对象\n死神：\n搞笑艺人，主角遇到会被笑死，扣除生命值\n" +
                        "补给箱：\n分数的象征，越多越好！\n粉色笑脸：\n神秘的笑脸，据说碰到会恢复生命值", new TickerText.TickerTextOptions(HorizontalAlign.CENTER, 15), this.getVertexBufferObjectManager());
        text.registerEntityModifier(
                new SequenceEntityModifier(
                        new ParallelEntityModifier(
                                new AlphaModifier(10, 0.0f, 1.0f),
                                new ScaleModifier(10, 0.5f, 1.0f)
                        ),
                        new RotationModifier(5, 0, 360)
                )
        );
        text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scene.attachChild(text);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(TicketTextActivity.this, MainActivity.class));
                finish();
            }
        }.start();
        return scene;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
