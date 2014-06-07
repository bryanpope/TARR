package com.totalannihilationroadrage;

import com.framework.Screen;
import com.framework.impl.AndroidGame;

public class TARRgame extends AndroidGame 
{
    public Screen getStartScreen() 
    {
        return new LoadingScreen(this); 
    }
}
