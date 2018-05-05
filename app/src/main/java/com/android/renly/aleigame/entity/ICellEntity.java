package com.android.renly.aleigame.entity;

public interface ICellEntity {
    // ===========================================================
    // Constants
    // ===========================================================

    public abstract int getCellX();
    public abstract int getCellY();

    public abstract void setCell(final ICellEntity pCellEntity);
    public abstract void setCell(final int pCellX, final int pCellY);

    public abstract boolean isInSameCell(final ICellEntity pCellEntity);
}