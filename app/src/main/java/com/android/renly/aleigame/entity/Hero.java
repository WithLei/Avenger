package com.android.renly.aleigame.entity;

import com.android.renly.aleigame.adt.AvengerSuicideException;
import com.android.renly.aleigame.adt.Direction;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Hero extends Entity {
    private final HeroHead mHead;
    private final ITextureRegion mTailPartTextureRegion;
    private Direction mDirection;
    private Direction mLastMoveDirection;
    private boolean mGrow;

    public Hero(final Direction pInitialDirection, final int pCellX, final int pCellY, final TiledTextureRegion pHeadTextureRegion, final ITextureRegion pTailPartTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(0, 0);
        this.mTailPartTextureRegion = pTailPartTextureRegion;
        this.mHead = new HeroHead(pCellX, pCellY, pHeadTextureRegion, pVertexBufferObjectManager);
        this.attachChild(this.mHead);
        this.setDirection(pInitialDirection);
    }
    public Direction getDirection() {
        return this.mDirection;
    }

    public void setDirection(final Direction pDirection) {
        if(this.mLastMoveDirection != Direction.opposite(pDirection)) {
            this.mDirection = pDirection;
            this.mHead.setRotation(pDirection);
        }
    }
    public HeroHead getHead(){return this.mHead;}

    //Methods
    public void grow(){this.mGrow = true;}

    public int getNextX(){return Direction.addToX(this.mDirection,this.mHead.getCellX());}

    public int getNextY(){return Direction.addToY(this.mDirection,this.mHead.getCellY());}

    public void move() throws AvengerSuicideException {

    }

}
