package com.android.renly.aleigame.entity;

import com.android.renly.aleigame.adt.Direction;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class HeroHead extends AnimatedCellEntity {
    public HeroHead(final int pCellX, final int pCellY, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pCellX, pCellY, CELL_WIDTH, 2 * CELL_HEIGHT, pTiledTextureRegion, pVertexBufferObjectManager);

        this.setRotationCenterY(CELL_HEIGHT / 2);
    }

    public void setRotation(final Direction pDirection) {
        switch(pDirection) {
            case UP:
                this.setRotation(180);
                break;
            case DOWN:
                this.setRotation(0);
                break;
            case LEFT:
                this.setRotation(90);
                break;
            case RIGHT:
                this.setRotation(270);
                break;
        }
    }
}
