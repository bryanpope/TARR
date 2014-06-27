package com.totalannihilationroadrage;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class TacticalCombatWorld 
{
	private int roundsPlayer;
    private int roundsEnemy;
    public List< TacticalCombatVehicle > tcvsPlayer;
    public List< TacticalCombatVehicle > tcvsEnemy;
    public TiledMap tmBattleGround;

    private boolean fields[][] = new boolean[40][15];
    private Random random = new Random();
    private Random randomTarget = new Random();
    Pathfinding pathfinding = new Pathfinding();
    private int enemyCounter = 0;

	TacticalCombatWorld (TiledMap tmBG, List< TacticalCombatVehicle > tcvsP, List< TacticalCombatVehicle > tcvsE)
	{
        tcvsPlayer = tcvsP;
        tcvsEnemy = tcvsE;
        tmBattleGround = tmBG;
        deployVehicles();
        chooseTarget();
	}
	
    private void deployVehicles ()
    {
        for (int y = 0; y < 15; ++y)
        {
            for (int x = 0; x < 40; ++x)
            {
                fields[x][y] = tmBattleGround.isPassable(y, x);
            }
        }

        deploySide(tcvsPlayer, 0, 13);
        deploySide(tcvsEnemy, 27, 13);
    }



    private void deploySide (List< TacticalCombatVehicle > side, int deployXOffset, int deployXWidth)
    {
        int deployX;
        int deployY;
        for (int i = 0; i < side.size(); ++i)
        {
            deployX = random.nextInt(deployXWidth) + deployXOffset;
            deployY = random.nextInt(15);
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
                    if (deployY >= 15)
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

    public void chooseTarget()
    {
        for(int i = 0; i < tcvsEnemy.size(); ++i)
        {
            tcvsEnemy.get(i).target = randomTarget.nextInt(tcvsPlayer.size());
        }
    }

    public void generatePath(int target)
    {
        for(int i = 0; i < tcvsEnemy.size(); ++i)
        {
            Node enemyNode = new Node(tcvsEnemy.get(i).yPos, tcvsEnemy.get(i).xPos, 0 ,0, null);
            Node targetNode = new Node(tcvsPlayer.get(target).yPos, tcvsPlayer.get(target).xPos, 0 ,0, null);
            tcvsEnemy.get(i).thePath = pathfinding.IAmAPathAndILikeCheese(tmBattleGround, enemyNode, targetNode);
        }
        if(tcvsEnemy.get(enemyCounter).thePath != null)
        {
            moveEnemy();
        }
    }

    public void moveEnemy()
    {
        if (enemyCounter < tcvsEnemy.size() - 1)
        {
            tcvsEnemy.get(enemyCounter).xPos = tcvsEnemy.get(enemyCounter).thePath.get(1).col;
            tcvsEnemy.get(enemyCounter).yPos = tcvsEnemy.get(enemyCounter).thePath.get(1).row;
            enemyCounter++;
        }
        if (enemyCounter == tcvsEnemy.size() - 1)
        {
            enemyCounter = 0;
        }
    }

    public boolean checkWithinShotRange(Node start, Node goal, double weaponRange)
    {
       if(getDistanceFromGoal(start, goal) <= weaponRange)
       {
           System.out.println("Within range");
           return true;
       }
        else
       {
           System.out.println("Not within range");
           return false;
       }
    }

    public double getDistanceFromGoal(Node start, Node goal)
    {
        return Math.sqrt((goal.row - start.row) * (goal.row - start.row) + (goal.col - start.col) * (goal.col - start.col));
    }
}
