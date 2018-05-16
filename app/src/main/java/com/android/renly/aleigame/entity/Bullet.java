package com.android.renly.aleigame.entity;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static com.android.renly.aleigame.constants.AvengerConstants.CELL_HEIGHT;
import static com.android.renly.aleigame.constants.AvengerConstants.CELL_WIDTH;

public class Bullet extends AnimatedSprite implements mSprite{
    private final PhysicsHandler mPhysicsHandler;
    private static final float DEMO_VELOCITY = 100.0f;
    private static final int CAMERA_WIDTH = 590;
    private static final int CAMERA_HEIGHT = 359;

    private int mCellX ;
    private int mCellY;

    public Bullet(final int pX, final int pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.mPhysicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(this.mPhysicsHandler);
        this.mPhysicsHandler.setVelocity(DEMO_VELOCITY, DEMO_VELOCITY);
        this.mCellX = pX;
        this.mCellY = pY;
    }

    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        if(this.mX < 0) {
            this.mPhysicsHandler.setVelocityX(DEMO_VELOCITY);
        } else if(this.mX + this.getWidth() > CAMERA_WIDTH) {
            this.mPhysicsHandler.setVelocityX(-DEMO_VELOCITY);
        }

        if(this.mY < 0) {
            this.mPhysicsHandler.setVelocityY(DEMO_VELOCITY);
        } else if(this.mY + this.getHeight() > CAMERA_HEIGHT) {
            this.mPhysicsHandler.setVelocityY(-DEMO_VELOCITY);
        }

        super.onManagedUpdate(pSecondsElapsed);
    }

    @Override
    public int getmCellX() {
        return mCellX;
    }

    @Override
    public int getmCellY() {
        return mCellY;
    }

    @Override
    public void setmCellX(int mCellX) {
        this.mCellX = mCellX;
    }

    @Override
    public void setmCellY(int mCellY) {
        this.mCellY = mCellY;
    }

    @Override
    public void refresh() {
        this.mCellX = (int)((this.getX()+0.5)/CELL_WIDTH);
        this.mCellY = (int)((this.getY()+0.5)/CELL_HEIGHT);
    }

    @Override
    public boolean isInSameCell(mSprite mSprite) {
        refresh();
        return mCellX == mSprite.getmCellX() && mCellY == mSprite.getmCellY();
    }
}
