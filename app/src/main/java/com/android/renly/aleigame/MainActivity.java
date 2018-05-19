package com.android.renly.aleigame;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Looper;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.renly.aleigame.constants.AvengerConstants;
import com.android.renly.aleigame.db.MySQLiteOpenHelper;
import com.android.renly.aleigame.entity.Box;
import com.android.renly.aleigame.entity.Bullet;
import com.android.renly.aleigame.entity.Dj;
import com.android.renly.aleigame.entity.Enemy;
import com.android.renly.aleigame.entity.UserScore;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
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
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.ButtonSprite;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.android.renly.aleigame.db.MySQLiteOpenHelper.TABLE_NAME;

public class MainActivity extends SimpleBaseGameActivity implements AvengerConstants,MenuScene.IOnMenuItemClickListener {
    // 摄像头尺寸
    private static final int CAMERA_WIDTH = 590;
    private static final int CAMERA_HEIGHT = 359;

    //暂停参数
    protected static final int MENU_CONTINUE = 0;
    protected static final int MENU_RESET = MENU_CONTINUE + 1;
    protected static final int MENU_QUIT= MENU_RESET + 1;
//    private static final int CAMERA_HEIGHT = CELLS_VERTICAL * CELL_HEIGHT; // 460
    private Camera mCamera;

    // 贴图
        // 人物贴图
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private BitmapTextureAtlas mBitmapBoxTextureAtlas;
    private BitmapTextureAtlas mBitmapEnemyTextureAtlas;
    private BitmapTextureAtlas mBitmapBulletTextureAtlas;
    private ITextureRegion mFaceTextureRegion;
    private ITextureRegion mBoxTextureRegion;
    private ITextureRegion mEnemyTextureRegion;
    private TiledTextureRegion mBulletTextureRegion;
         // 背景贴图
    private BitmapTextureAtlas mBackgroundTexture;
    private ITextureRegion mBackgroundTextureRegion;
        // 控制杆贴图
    private BitmapTextureAtlas mOnScreenControlTexture;
    private ITextureRegion mOnScreenControlBaseTextureRegion;
    private ITextureRegion mOnScreenControlKnobTextureRegion;
    private AnalogOnScreenControl velocityOnScreenControl;
        // 暂停键贴图
    private BitmapTextureAtlas mBitmapPauseTextureAtlas;
    private ITextureRegion mPauseTextureRegion;
        // Hp值贴图
    private BitmapTextureAtlas mBitmapHpTextureAtlas;
    private ITextureRegion mHpTextureRegion;
    private BitmapTextureAtlas mBitmapHpDecreaseTextureAtlas;
    private ITextureRegion mHpDecreaseTextureRegion;

    private boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;

    // 字体
    private Font mFont;

    // 场景
    private Scene mScene;

    // 声音
    private Sound mGameOverSound;
    private Sound[] mMunchSound;
    private Sound[] mBeAttackSound;
    private Sound[] mBeHealingSound;
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
    //暂停菜单
    protected MenuScene mMenuScene;

    // 主角对象
    private Dj face;
    // 金币对象
    private Box mBox;
    //  敌人对象
    private Enemy enemy;
    // 子弹对象
    private static Bullet bullet;
    // HP爱心对象
    private Sprite firstHp;
    private Sprite secondHp;
    private Sprite thirdHp;
    // 暂停按钮对象
    private ButtonSprite mPauseBtn;

    // 控制杆
    private DigitalOnScreenControl mDigitalOnScreenControl;

    // 游戏是否进行中
    protected boolean mGameRunning;
    // 记录分数
    private int mScore = 0;
    //记录生命值
    private int health = 3;

    //接收Intent数据
    private String skin;
    private String difficulty;

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
        getIntentInfo();
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
        //操纵角色
        this.mBitmapBoxTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapBoxTextureAtlas,this,"box2.png",0,0);
        this.mBitmapBoxTextureAtlas.load();

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        if(skin.equals("DJ"))
            this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "DJ.png", 0, 0);
        this.mBitmapTextureAtlas.load();

        //敌人
        this.mBitmapEnemyTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),45,45,TextureOptions.BILINEAR);
        this.mEnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapEnemyTextureAtlas,this,"reaper.png",0,0);
        this.mBitmapEnemyTextureAtlas.load();

        //子弹
        this.mBitmapBulletTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
        this.mBulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapBulletTextureAtlas, this, "face_circle_tiled.png", 0, 0, 2, 1);
        this.mBitmapBulletTextureAtlas.load();

        //爱心贴图
        this.mBitmapHpTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),45,45,TextureOptions.BILINEAR);
        this.mHpTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapHpTextureAtlas,this, "hp.png", 0, 0);
        this.mBitmapHpTextureAtlas.load();
        this.mBitmapHpDecreaseTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),45,45,TextureOptions.BILINEAR);
        this.mHpDecreaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapHpDecreaseTextureAtlas,this, "hpcrease.png", 0, 0);
        this.mBitmapHpDecreaseTextureAtlas.load();

        //暂停按钮
        this.mBitmapPauseTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),64,64,TextureOptions.BILINEAR);
        this.mPauseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapPauseTextureAtlas,this,"pause.png",0,0);
        this.mBitmapPauseTextureAtlas.load();

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
            this.mBeAttackSound = new Sound[4];
            this.mBeHealingSound = new Sound[4];
            for(int i = 1;i <= 6 ; i++){
                String path = "dj" + i + ".ogg";
                this.mMunchSound[i] = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, path);
            }
            for(int i = 1;i <= 3; i++) {
                String path = "beattack" + i + ".ogg";
                this.mBeAttackSound[i] = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, path);
            }
            for(int i = 1;i <= 3; i++) {
                String path = "behealing" + i + ".ogg";
                this.mBeHealingSound[i] = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, path);
            }
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    /* 加载控件*/
    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.mMenuScene = this.createMenuScene();


        this.mScene = new Scene();
        for(int i = 0;i < LAYER_COUNT;i++){
            this.mScene.attachChild(new Entity());
        }

        final int centerX = (int)(CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
        final int centerY = (int)(CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
        face = new Dj(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
        final PhysicsHandler physicsHandler = new PhysicsHandler(face);
        face.registerUpdateHandler(physicsHandler);
        this.mScene.getChildByIndex(LAYER_HERO).attachChild(face);

        /* No background color needed as we have a fullscreen background sprite. */
        this.mScene.setBackgroundEnabled(false);
        this.mScene.getChildByIndex(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager()));

        /* 分数 */
        this.mScoreText = new Text(5, 5, this.mFont, "Score: 0", "Score: XXXX".length(), this.getVertexBufferObjectManager());
        this.mScoreText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        this.mScoreText.setWidth(30);
        this.mScoreText.setAlpha(0.5f);
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.mScoreText);

        //补给箱
        this.mBox = new Box(0,0,this.mBoxTextureRegion,this.getVertexBufferObjectManager());
        this.setToRandomCell(mBox);
        this.mScene.getChildByIndex(LAYER_COIN).attachChild(this.mBox);

        //子弹
//        this.bullet = new Bullet(face.getmCellX(),face.getmCellY(),this.mBulletTextureRegion, this.getVertexBufferObjectManager());
//        this.mScene.getChildByIndex(LAYER_HERO).attachChild(this.bullet);

        //敌人
        this.enemy = new Enemy(0,0,this.mEnemyTextureRegion,this.getVertexBufferObjectManager());
        this.enemy.setmCellX(MathUtils.random(1, CELLS_HORIZONTAL - 2) * CELL_WIDTH);
        this.enemy.setmCellY(MathUtils.random(1, CELLS_VERTICAL - 2) * CELL_HEIGHT);
        this.enemy.setmNextX(MathUtils.random(1, CELLS_HORIZONTAL - 2) * CELL_WIDTH);
        this.enemy.setmNextY(MathUtils.random(1, CELLS_VERTICAL - 2) * CELL_HEIGHT);
        this.enemy.setPosition(this.enemy.getmCellX(),this.enemy.getmCellY());
        final PathModifier.Path path = new PathModifier.Path(100).to(this.enemy.getmCellX(), this.enemy.getmCellY()).to(this.enemy.getmNextX(), this.enemy.getmNextY());
        for(int i = 0;i < 98;i++){
            this.enemy.setmNextX(MathUtils.random(1, CELLS_HORIZONTAL - 2) * CELL_WIDTH);
            this.enemy.setmNextY(MathUtils.random(1, CELLS_VERTICAL - 2) * CELL_HEIGHT);
            path.to(this.enemy.getmNextX(), this.enemy.getmNextY());
        }
        final int enemyDuration;
        switch (difficulty){
            case "easy":
                enemyDuration = 150;
                break;
            case "hard":
                enemyDuration = 110;
                break;
            case "fxxk":
                enemyDuration = 70;
                break;
            default:
                enemyDuration = 150;
                break;
        }
        enemy.registerEntityModifier(new LoopEntityModifier(new PathModifier(enemyDuration, path, null, new PathModifier.IPathModifierListener() {
            @Override
            public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {

            }

            @Override
            public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
//                switch(pWaypointIndex){
//                    case 0:
//                        enemy.animate(new long[]{200, 200, 200}, 6, 8, true);
//                        break;
//                    case 1:
//                        enemy.animate(new long[]{200, 200, 200}, 3, 5, true);
//                        break;
//                    case 2:
//                        enemy.animate(new long[]{200, 200, 200}, 0, 2, true);
//                        break;
//                    case 3:
//                        enemy.animate(new long[]{200, 200, 200}, 9, 11, true);
//                        break;
//                }
            }

            @Override
            public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {

            }
        })));

        this.mScene.getChildByIndex(LAYER_COIN).attachChild(this.enemy);

        //Hp
        this.firstHp = new Sprite(350,5,this.mHpTextureRegion,this.getVertexBufferObjectManager());
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.firstHp);
        this.secondHp = new Sprite(400,5,this.mHpTextureRegion,this.getVertexBufferObjectManager());
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.secondHp);
        this.thirdHp = new Sprite(450,5,this.mHpTextureRegion,this.getVertexBufferObjectManager());
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.thirdHp);


        //暂停按钮
        this.mPauseBtn = new ButtonSprite(CAMERA_WIDTH - 70, 5, this.mPauseTextureRegion, this.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Log.e("log","点击事件");
                mScene.setChildScene(mMenuScene, false, true, true);
            }
        });
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.mPauseBtn);
        this.mScene.registerTouchArea(this.mPauseBtn);

/**
 *  start
 */
        /* Velocity control (left). */
        //左控制杆
        final float x1 = 5;
        final float y1 = CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight() - 5;
        velocityOnScreenControl = new AnalogOnScreenControl(x1, y1, this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new AnalogOnScreenControl.IAnalogOnScreenControlListener() {
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
        //右控制杆
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

                    synchronized(this){
                        MainActivity.this.handleNewHeroPosition();
                    }
                }
            }
        }));

        /*发射子弹频率为3s*/
//        this.mScene.registerUpdateHandler(new TimerHandler(3, true, new ITimerCallback() {
//            @Override
//            public void onTimePassed(final TimerHandler pTimerHandler) {
//                bullet = new Bullet(face.getmCellX(),face.getmCellY(),mBulletTextureRegion, getVertexBufferObjectManager());
//                mScene.getChildByIndex(LAYER_HERO).attachChild(bullet);
//            }
//        }));

        /* The title-text. */
        final Text titleText = new Text(0, 0, this.mFont, "Game\nStart!", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
        titleText.setPosition((CAMERA_WIDTH - titleText.getWidth()) * 0.5f, (CAMERA_HEIGHT - titleText.getHeight()) * 0.5f);
        titleText.setScale(0.0f);
        titleText.registerEntityModifier(new ScaleModifier(1, 0.0f, 1.0f));
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

//        refreshBullet();

        return this.mScene;
    }
    /*
     * Method
     */

    private void refreshBullet(){
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bullet = new Bullet(MathUtils.random(1, CELLS_HORIZONTAL - 2) * CELL_WIDTH,MathUtils.random(1, CELLS_VERTICAL - 2) * CELL_HEIGHT,mBulletTextureRegion, getVertexBufferObjectManager());
                mScene.getChildByIndex(LAYER_HERO).attachChild(bullet);
            }
        }.start();
    }

    private void setToRandomCell(Sprite sprite) {
        //再做一个防止敌人和金币同一个位置的
        sprite.setPosition(MathUtils.random(1, CELLS_HORIZONTAL - 2) * CELL_WIDTH, MathUtils.random(1, CELLS_VERTICAL - 2) * CELL_HEIGHT);
    }

    //受伤后缓冲时间
    private boolean isBufferTime = false;

    private void handleNewHeroPosition() {
//        final HeroHead heroHead = this.mHero.getHead();
        this.face.refresh();
        this.mBox.refresh();
        this.enemy.refresh();
        if(this.bullet != null)
            this.bullet.refresh();

//        Log.e("log","X = " + (int)((face.getX()+0.5)/CELL_WIDTH)+ "   Y = " + (int)((face.getY()+0.5)/CELL_WIDTH) +
//                "; bulletX = " + this.bullet.getmCellX() + " bulletY = " + this.bullet.getmCellY());
        if(face.getmCellX() < 0 || face.getmCellX() >= CELLS_HORIZONTAL || face.getmCellY() < 0 || face.getmCellY() >= CELLS_VERTICAL) {
            this.onGameOver();
            Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
            vibrator.vibrate(3000);
        } else if(face.isInSameCell(this.mBox)) {
            this.mScore += 50;
            this.mScoreText.setText("Score: " + this.mScore);
            int index = MathUtils.random(1,6);
            this.mMunchSound[index].play();
            this.setToRandomCell(this.mBox);
            Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        } else if(this.bullet != null && face.isInSameCell(this.bullet) && !isBufferTime){
            Log.e("bullet","refresh");
        //detach sprite
            runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    if(bullet == null){
                        return ;
                    }
                    //先将引擎锁起来，再删除，删除后，再释放锁。为了解决同步的问题
                    //得到引擎锁
                    final Engine.EngineLock engineLock = MainActivity.this.getEngine().getEngineLock();
                    //将引擎锁锁住
                    engineLock.lock();
                    //从场景中删除该精灵
                    mScene.getChildByIndex(LAYER_HERO).detachChild(bullet);
                    //销毁
                    bullet.dispose();
                    //置空
                    bullet = null;
                    //解锁
                    engineLock.unlock();
                }
            });
            BufferTime();
            int index = MathUtils.random(1,3);
            this.mBeHealingSound[index].play();
            if (this.health < 3)
                this.health++;
            switch (health){
                case 3:
                    this.thirdHp = new Sprite(450,5,this.mHpTextureRegion,this.getVertexBufferObjectManager());
                    this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.thirdHp);
                    break;
                case 2:
                    this.secondHp = new Sprite(400,5,this.mHpTextureRegion,this.getVertexBufferObjectManager());
                    this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.secondHp);
                    break;
                case 1:
                    this.firstHp = new Sprite(350,5,this.mHpTextureRegion,this.getVertexBufferObjectManager());
                    this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.firstHp);
                    break;
            }
//            Log.e("log","health == " + this.health);

        } else if(face.isInSameCell(this.enemy) && !isBufferTime) {
            BufferTime();
            int index = MathUtils.random(1,3);
            this.mBeAttackSound[index].play();
            this.health--;
            //受伤后刷新粉脸
            if(this.health != 3 && this.bullet == null)
                refreshBullet();
//            Log.e("log","health == " + this.health);
            switch (health){
                case 2:
                    this.thirdHp = new Sprite(450,5,this.mHpDecreaseTextureRegion,this.getVertexBufferObjectManager());
                    this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.thirdHp);
                    break;
                case 1:
                    this.secondHp = new Sprite(400,5,this.mHpDecreaseTextureRegion,this.getVertexBufferObjectManager());
                    this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.secondHp);
                    break;
                case 0:
                    this.firstHp = new Sprite(350,5,this.mHpDecreaseTextureRegion,this.getVertexBufferObjectManager());
                    this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.firstHp);
                    break;
            }
            //生命值降为0
            if(health == 0)
                this.onGameOver();
        }
    }

    private void BufferTime() {
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        isBufferTime = true;
        //3秒缓冲时间
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                    isBufferTime = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void onGameOver(){
        new Thread(){
            @Override
            public void run() {
                try {
                    this.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainActivity.this.mScene.getChildByIndex(LAYER_SCORE).detachChild(mGameOverText);
                mScene.setChildScene(mMenuScene, false, true, true);
                showInputDialogCustomInvalidation();
            }
        }.start();
        this.mGameBackgroundSound.pause();
        this.mGameOverSound.play();
        this.mScene.reset();
        this.mScene.getChildByIndex(LAYER_SCORE).attachChild(this.mGameOverText);
        this.mGameRunning = false;
        this.mScene.clearChildScene();

    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if(this.mScene.hasChildScene()) {
                /* Remove the menu and reset it. */
                this.mMenuScene.back();
            } else {
                /* Attach the menu. */
                this.mScene.setChildScene(this.mMenuScene, false, true, true);
            }
            return true;
        } else {
            return super.onKeyDown(pKeyCode, pEvent);
        }
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        switch(pMenuItem.getID()) {
            case MENU_RESET:
                /* Restart the animation. */
                this.mScene.reset();

                /* Remove the menu and reset it. */
                this.mScene.clearChildScene();
                this.mMenuScene.reset();
                this.mScore = 0;
                this.health = 3;
                onCreateGame();
                return true;
            case MENU_QUIT:
                /* End Activity. */
                this.finish();
                return true;
            case MENU_CONTINUE:
                if(!mGameRunning)
                    return false;
                this.mMenuScene.back();
                this.mScene.setChildScene(velocityOnScreenControl);
                return true;
            default:
                return false;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void getIntentInfo() {
        Intent intent = getIntent();
        difficulty = intent.getStringExtra("difficulty");
        skin = intent.getStringExtra("skin");
    }

    protected MenuScene createMenuScene() {
        final MenuScene menuScene = new MenuScene(this.mCamera);

        final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESET, this.mFont, "Restart", this.getVertexBufferObjectManager()), new org.andengine.util.color.Color(1,0,0), new org.andengine.util.color.Color(1,1,1));
        resetMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(resetMenuItem);

        final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mFont, "Quit", this.getVertexBufferObjectManager()), new org.andengine.util.color.Color(1,0,0), new org.andengine.util.color.Color(1,1,1));
        quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);

        final IMenuItem continueItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_CONTINUE, this.mFont, "Continue",this.getVertexBufferObjectManager()),new org.andengine.util.color.Color(1,0,0),new org.andengine.util.color.Color(1,1,1));
        continueItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(continueItem);

        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);

        return menuScene;
    }

    public void showInputDialogCustomInvalidation() {
        Looper.prepare();
        new MaterialDialog.Builder(MainActivity.this)
                .title("新高分")
                .content("请输入昵称")
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText("确认")
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .input(
                        R.string.username,
                        0,
                        false,
                        (dialog,input) -> {
                            if (input.toString().length() > 6) {
                                dialog.setContent("太长了人家受不了了!");
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            } else {
                                dialog.setContent(R.string.inputyourname);
                                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            }
                            insertDB(input.toString(),this.mScore);
                        }
                )
                .show();
        Looper.loop();
    }

    private void insertDB(String name,int score) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        db.beginTransaction();

        UserScore temp = new UserScore(name,score);
        db.execSQL(insertSql(temp));

        db.setTransactionSuccessful();

    }

    private String insertSql(UserScore temp){
        return "insert into " + TABLE_NAME + "(name,score) values('" + temp.getUserName() + "','" + temp.getScore() + "')";
    }

}
