package com.android.renly.aleigame.entity;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Coin extends AnimatedCellEntity{
    /*
    ** 金币
     */
    public Coin(final int pCellX, final int pCellY, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pCellX, pCellY, CELL_WIDTH, CELL_HEIGHT, pTiledTextureRegion, pVertexBufferObjectManager);
    }
}
