package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.framework.Game;

public class TacticalCombatWorld 
{
	private int roundsPlayer;
    private int roundsEnemy;
    public List< TacticalCombatVehicle > tcvsPlayer;
    public List< TacticalCombatVehicle > tcvsEnemy;
    public TiledMap tmBattleGround;

    private boolean fields[][] = new boolean[100][30];
    private Random random = new Random();

	TacticalCombatWorld (TiledMap tmBG, List< TacticalCombatVehicle > tcvsP, List< TacticalCombatVehicle > tcvsE)
	{
        tcvsPlayer = tcvsP;
        tcvsEnemy = tcvsE;
        tmBattleGround = tmBG;

        deployVehicles();
	}
	
    private void deployVehicles ()
    {
        for (int y = 0; y < 30; ++y)
        {
            for (int x = 0; x < 100; ++x)
            {
                fields[x][y] = tmBattleGround.layers.get(0).isPassable (y, x);
            }
        }

        deploySide(tcvsPlayer, 0, 20);
        deploySide(tcvsEnemy, 80, 20);
    }

    private void deploySide (List< TacticalCombatVehicle > side, int deployXOffset, int deployXWidth)
    {
        int deployX;
        int deployY;
        for (int i = 0; i < side.size(); ++i)
        {
            deployX = random.nextInt(deployXWidth) + deployXOffset;
            deployY = random.nextInt(30);
            while (true)
            {
                if (fields[deployX][deployY])
                {
                    break;
                }
                ++deployX;
                if (deployX >= (deployXOffset + deployXWidth))
                {
                    deployX = 0;
                    ++deployY;
                    if (deployY >= 30)
                    {
                        deployY = 0;
                    }
                }
            }
            fields[deployX][deployY] = false;
            side.get(i).xPos = deployX;
            side.get(i).yPos = deployY;
        }
    }
	
}
