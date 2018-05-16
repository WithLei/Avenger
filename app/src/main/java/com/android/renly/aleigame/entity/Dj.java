package com.android.renly.aleigame.entity;

import com.android.renly.aleigame.adt.Direction;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static com.android.renly.aleigame.constants.AvengerConstants.CELL_HEIGHT;
import static com.android.renly.aleigame.constants.AvengerConstants.CELL_WIDTH;

public class Dj extends Sprite implements mSprite{
    private int mCellX;
    private int mCellY;
    public Dj(int pX, int pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.mCellX = pX;
        this.mCellY = pY;
    }

    @Override
    public int getmCellX() {
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
    public boolean isInSameCell(final mSprite mSprite) {
        refresh();
        if(mCellX == mSprite.getmCellX() && mCellY == mSprite.getmCellY())
            return true;
        else if(mCellX == mSprite.getmCellX() + 1 && mCellY == mSprite.getmCellY())
            return true;
        else if(mCellX == mSprite.getmCellX() - 1 && mCellY == mSprite.getmCellY())
            return true;
        else if(mCellX == mSprite.getmCellX() && mCellY == mSprite.getmCellY() + 1)
            return true;
        else if(mCellX == mSprite.getmCellX() && mCellY == mSprite.getmCellY() - 1)
            return true;
        else
            return false;
    }
}
