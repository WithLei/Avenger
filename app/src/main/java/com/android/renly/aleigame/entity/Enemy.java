package com.android.renly.aleigame.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static com.android.renly.aleigame.constants.AvengerConstants.CELL_HEIGHT;
import static com.android.renly.aleigame.constants.AvengerConstants.CELL_WIDTH;

public class Enemy extends Sprite implements mSprite{
    private int mCellX;
    private int mCellY;
    private int mNextX;
    private int mNextY;

    public Enemy(int pX, int pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.mCellX = pX;
        this.mCellY = pY;
    }

    public int getmNextX() {
        return mNextX;
    }

    public void setmNextX(int mNextX) {
        this.mNextX = mNextX;
    }

    public int getmNextY() {
        return mNextY;
    }

    public void setmNextY(int mNextY) {
        this.mNextY = mNextY;
    }

    @Override
    public int getmCellX(){
        return this.mCellX;
    }

    @Override
    public int getmCellY(){
        return this.mCellY;
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
    public void refresh(){
        this.mCellX = (int)((this.getX()+0.5)/CELL_WIDTH);
        this.mCellY = (int)((this.getY()+0.5)/CELL_HEIGHT);
    }

    @Override
    public boolean isInSameCell(mSprite mSprite) {
        refresh();
        return mCellX == mSprite.getmCellX() && mCellY == mSprite.getmCellY();
    }

}
