package com.android.renly.aleigame.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static com.android.renly.aleigame.constants.AvengerConstants.CELL_HEIGHT;
import static com.android.renly.aleigame.constants.AvengerConstants.CELL_WIDTH;

public class Box extends Sprite {
    private int mCellX;
    private int mCellY;
    public Box(int pX, int pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.mCellX = pX;
        this.mCellY = pY;
    }

    public int getmCellX() {
        return this.mCellX;
    }

    public int getmCellY(){
        return this.mCellY;
    }

    public void refresh(){
        this.mCellX = (int)((this.getX()+0.5)/CELL_WIDTH);
        this.mCellY = (int)((this.getY()+0.5)/CELL_HEIGHT);
    }

}
