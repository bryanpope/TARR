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
    Node testTarget = new Node(2, 2, 0, 0, null);

    private boolean fields[][] = new boolean[40][15];
    private Random random = new Random();
    private Random randomTarget = new Random();
    Pathfinding pathfinding = new Pathfinding();
    private int j = 0;

	TacticalCombatWorld (TiledMap tmBG, List< TacticalCombatVehicle > tcvsP, List< TacticalCombatVehicle > tcvsE)
	{
        tcvsPlayer = tcvsP;
        tcvsEnemy = tcvsE;
        tmBattleGround = tmBG;

        deployVehicles();
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

    public void moveEnemy()
    {
        int randomEnemyTarget;
        Node path;
        Node enemyNode;
        Node enemyTarget;

        List<Node> enemyNodeList = new ArrayList<Node>();
        List<Node> enemyTargetList = new ArrayList<Node>();
        List<Node> pathList = new ArrayList<Node>();
        int i = 0;

        enemyNode = new Node(tcvsEnemy.get(i).yPos, tcvsEnemy.get(i).xPos, 0, 0, null);
        randomEnemyTarget = randomTarget.nextInt(tcvsPlayer.size());
        enemyTarget = new Node(tcvsPlayer.get(randomEnemyTarget).yPos, tcvsPlayer.get(randomEnemyTarget).xPos, 0, 0, null);
        enemyNodeList.add(enemyNode);
        enemyTargetList.add(enemyTarget);

        path = pathfinding.IAmAPathAndILikeCheese(tmBattleGround, enemyNode, testTarget);

        while(path != null)
        {
            pathList.add(path);
            path = path.parentNode;
        }
        Collections.reverse(pathList);

        tcvsEnemy.get(i).xPos = pathList.get(j).col;
        tcvsEnemy.get(i).yPos = pathList.get(j).row;
        if(j < pathList.size())
        {
            j++;
        }
    }
}
