package com.android.renly.aleigame.entity;

public interface mSprite{
    public abstract int getmCellX();
    public abstract int getmCellY();

    public abstract void setmCellX(int mCellX);
    public abstract void setmCellY(int mCellY);

    public abstract void refresh();
    public abstract boolean isInSameCell(mSprite mSprite);
}
