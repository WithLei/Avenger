package com.android.renly.aleigame;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;
import android.widget.Toast;

import com.android.renly.aleigame.constants.AvengerConstants;
import com.android.renly.aleigame.entity.Box;
import com.android.renly.aleigame.entity.Dj;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import java.io.IOException;

public class MainActivity extends SimpleBaseGameActivity implements AvengerConstants {
    // 摄像头尺寸
    private static final int CAMERA_WIDTH = 590;
    private static final int CAMERA_HEIGHT = 359;
//    private static final int CAMERA_HEIGHT = CELLS_VERTICAL * CELL_HEIGHT; // 460
    private Camera mCamera;

    //贴图
        // 人物贴图
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private BitmapTextureAtlas mBitmapBoxTextureAtlas;
    private TiledTextureRegion mHeadTextureRegion;
    private TiledTextureRegion mCoinTextureRegion;
    private ITextureRegion mFaceTextureRegion;
    private ITextureRegion mBoxTextureRegion;

    // 背景贴图
    private BitmapTextureAtlas mBackgroundTexture;
    private ITextureRegion mBackgroundTextureRegion;
        // 控制杆贴图
    private BitmapTextureAtlas mOnScreenControlTexture;
    private ITextureRegion mOnScreenControlBaseTextureRegion;
    private ITextureRegion mOnScreenControlKnobTextureRegion;

    private boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;

    // 字体
    private Font mFont;

    // 场景
    private Scene mScene;

    // 声音
    private Sound mGameOverSound;
    private Sound[] mMunchSound;
    private Music mGameBackgroundSound;

    //视图层数
    private static final int LAYER_BACKGROUND = 0;
    private static final int LAYER_COIN = LAYER_BACKGROUND + 1;
    private static final int LAYER_HERO = LAYER_COIN + 1;
    private static final int LAYER_SCORE = LAYER_HERO + 1;
    private static final int LAYER_COUNT = 4;

    // 分数显示
    private Text mScoreText;
    // 游戏结束提示
    private Text mGameOverText;

    // 主角对象
    private Dj face;
    // 金币对象
    private Box mBox;

    // 控制杆
    private DigitalOnScreenControl mDigitalOnScreenControl;

    // 游戏是否进行中
    protected boolean mGameRunning;
    // 记录分数
    private int mScore = 0;

    @Override
    public EngineOptions onCreateEngineOptions() {
        //构建摄像机
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        // 构建Engine，全屏显示，手机方向为竖屏，按比例拉伸
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);
        if(MultiTouch.isSupported(this)) {
            if(MultiTouch.isSupportedDistinct(this)) {
//                Toast.makeText(this, "MultiTouch detected --> Both controls will work properly!", Toast.LENGTH_SHORT).show();
            } else {
                this.mPlaceOnScreenControlsAtDifferentVerticalLocations = true;
                Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
        }
        engineOptions.getAudioOptions().setNeedsMusic(true);
        engineOptions.getAudioOptions().setNeedsSound(true);
        return engineOptions;
    }

    /* 加载所有需要的贴图和声音 */
    @Override
    protected void onCreateResources() {
        /* Load the font we are going to use. */
        FontFactory.setAssetBasePath("font/");
        this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, TextureOptions.BILINEAR, this.getAssets(), "Plok.ttf", 32, true, Color.WHITE);
        this.mFont.load();

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        /* Load all the textures this game needs. */
        /*
        创建瓦片贴图TileTextureRegion
        TileTextureRegion通常含有一张以上的图片，每张图片都具有相同的宽高，这些图片以矩阵的形式组织起来，
        每一张都可以通过在矩阵中的位置来定位。瓦片贴图可以用来创建动画精灵、储存地图的图块。
         */
        //Hero 脸部
        this.mBitmapBoxTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapBoxTextureAtlas,this,"box2.png",0,0);
        this.mBitmapBoxTextureAtlas.load();

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "DJ.png", 0, 0);
        this.mBitmapTextureAtlas.load();

        //背景贴图
        this.mBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(),1024,512);
        this.mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTexture,this,"background.jpg",0,0);
        this.mBackgroundTexture.load();

        //控制杆贴图
        this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(),256,128,TextureOptions.BILINEAR);
        this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture,this,"onscreen_control_base.png", 0, 0);
        this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture,this,"onscreen_control_knob.png", 128, 0);
        this.mOnScreenControlTexture.load();

        /* Load all the sounds this game needs. */
        try {
            SoundFactory.setAssetBasePath("mfx/");
            MusicFactory.setAssetBasePath("mfx/");
            this.mGameBackgroundSound = MusicFactory.createMusicFromAsset(this.getMusicManager(),this,"Victory.ogg");
            this.mGameOverSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "djdie.ogg");
            this.mMunchSound = new Sound[8];
            for(int i = 1;i <= 6 ; i++){
                String path = "dj" + i + ".ogg";
                this.mMunchSound[i] = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, path);
            }
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    /* 加载控件*/
    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        for(int i = 0;i < LAYER_COUNT;i++){
            this.mScene.attachChild(new Entity());
        }

        int centerX = (int)(CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
//        centerX /= CELL_WIDTH;
        int centerY = (int)(CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
//        centerY /= CELL_HEIGHT;
        face = new Dj(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
        final PhysicsHandler physicsHandler = new PhysicsHandler(face);
        face.registerUpdateHandler(physicsHandler);
        this.mScene.getChildByIndex(LAYER_HERO).attachChild(face);

        /* No background color needed as we have a fullscreen background sprite. */
        this.mScene.setBackgroundEnabled(false);
        this.mScene.getChildByIndex(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager()));

        /* The ScoreText showing how many points the pEntity scored. */
        this.mScoreText = new Text(5, 5, this.mFont, "Score: 0", "Score: XXXX".length(), this.getVertexBufferObjectManager());
        this.mScoreText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        this.mScoreText.setAlpha(0.5f);
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.mScoreText);

        this.mBox = new Box(0,0,this.mBoxTextureRegion,this.getVertexBufferObjectManager());
        this.setBoxToRandomCell();
        this.mScene.getChildByIndex(LAYER_COIN).attachChild(this.mBox);

/**
 *  start
 */
        /* Velocity control (left). */
        final float x1 = 0;
        final float y1 = CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight();
        final AnalogOnScreenControl velocityOnScreenControl = new AnalogOnScreenControl(x1, y1, this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new AnalogOnScreenControl.IAnalogOnScreenControlListener() {
            @Override
            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
                physicsHandler.setVelocity(pValueX * 100, pValueY * 100);
            }

            @Override
            public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
                /* Nothing. */
            }
        });
        velocityOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        velocityOnScreenControl.getControlBase().setAlpha(0.5f);

        this.mScene.setChildScene(velocityOnScreenControl);


        /* Rotation control (right). */
//        final float y2 = (this.mPlaceOnScreenControlsAtDifferentVerticalLocations) ? 0 : y1;
//        final float x2 = CAMERA_WIDTH - this.mOnScreenControlBaseTextureRegion.getWidth();
//        final AnalogOnScreenControl rotationOnScreenControl = new AnalogOnScreenControl(x2, y2, this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new AnalogOnScreenControl.IAnalogOnScreenControlListener() {
//            @Override
//            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
//                if(pValueX == x1 && pValueY == x1) {
//                    face.setRotation(x1);
//                } else {
//                    face.setRotation(MathUtils.radToDeg((float)Math.atan2(pValueX, -pValueY)));
//                }
//            }
//
//            @Override
//            public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
//                /* Nothing. */
//            }
//        });
//        rotationOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        rotationOnScreenControl.getControlBase().setAlpha(0.5f);
//
//        velocityOnScreenControl.setChildScene(rotationOnScreenControl);

/**
 *  finish
 */

        /* Make the Hero move every 0.05 seconds. */
        this.mScene.registerUpdateHandler(new TimerHandler(0.05f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                if(MainActivity.this.mGameRunning) {
//                    try {
//                        MainActivity.this.mHero.move();
//                    } catch (final AvengerSuicideException e) {
//                        MainActivity.this.onGameOver();
//                    }

                    MainActivity.this.handleNewHeroPosition();
                }
            }
        }));

        /* The title-text. */
        final Text titleText = new Text(0, 0, this.mFont, "Game\nStart!", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
        titleText.setPosition((CAMERA_WIDTH - titleText.getWidth()) * 0.5f, (CAMERA_HEIGHT - titleText.getHeight()) * 0.5f);
        titleText.setScale(0.0f);
        titleText.registerEntityModifier(new ScaleModifier(2, 0.0f, 1.0f));
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(titleText);

        /* The handler that removes the title-text and starts the game. */
        this.mScene.registerUpdateHandler(new TimerHandler(3.0f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                MainActivity.this.mScene.unregisterUpdateHandler(pTimerHandler);
                MainActivity.this.mScene.getChildByIndex(LAYER_SCORE).detachChild(titleText);
                MainActivity.this.mGameRunning = true;
            }
        }));

        /* The game-over text. */
        this.mGameOverText = new Text(0, 0, this.mFont, "Game\nOver", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
        this.mGameOverText.setPosition((CAMERA_WIDTH - this.mGameOverText.getWidth()) * 0.5f, (CAMERA_HEIGHT - this.mGameOverText.getHeight()) * 0.5f);
        this.mGameOverText.registerEntityModifier(new ScaleModifier(3, 0.1f, 2.0f));
        this.mGameOverText.registerEntityModifier(new RotationModifier(3, 0, 720));

        this.mGameBackgroundSound.play();

        return this.mScene;
    }
    /*
     * Method
     */
    private void setBoxToRandomCell() {
        this.mBox.setPosition(MathUtils.random(1, CELLS_HORIZONTAL - 2) * CELL_WIDTH, MathUtils.random(1, CELLS_VERTICAL - 2) * CELL_HEIGHT);
    }

    private void handleNewHeroPosition() {
//        final HeroHead heroHead = this.mHero.getHead();
        face.refresh();
        mBox.refresh();

        Log.e("log","X = " + (int)((face.getX()+0.5)/CELL_WIDTH)+ "   Y = " + (int)((face.getY()+0.5)/CELL_WIDTH) +
                "; boxX = " + this.mBox.getmCellX() + " boxY = " + this.mBox.getmCellY());
        if(face.getmCellX() < 0 || face.getmCellX() >= CELLS_HORIZONTAL || face.getmCellY() < 0 || face.getmCellY() >= CELLS_VERTICAL) {
            this.onGameOver();
        } else if(face.isInSameCell(this.mBox)) {
            this.mScore += 50;
            this.mScoreText.setText("Score: " + this.mScore);
            int index = MathUtils.random(1,6);
            this.mMunchSound[index].play();
            this.setBoxToRandomCell();
        }
    }

    @Override
    public synchronized void onGameCreated() {
    }

    private void onGameOver(){
        this.mGameBackgroundSound.pause();
        this.mGameOverSound.play();
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.mGameOverText);
        this.mGameRunning = false;
    }
}
