package com.android.renly.aleigame;

import com.android.renly.aleigame.constants.AvengerConstants;

import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class MainActivity extends SimpleBaseGameActivity implements AvengerConstants {

    @Override
    protected void onCreateResources() {

    }

    @Override
    protected Scene onCreateScene() {
        return null;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        return null;
    }

    @Override
    public synchronized void onGameCreated() {
        super.onGameCreated();
    }
}
