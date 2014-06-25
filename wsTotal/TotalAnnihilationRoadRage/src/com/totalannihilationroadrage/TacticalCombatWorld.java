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
    public List<List> pathListContainer = new ArrayList<List>();
    public TiledMap tmBattleGround;
    Node testTarget = new Node(2, 2, 0, 0, null);

    private boolean fields[][] = new boolean[40][15];
    private Random random = new Random();
    private Random randomTarget = new Random();
    Pathfinding pathfinding = new Pathfinding();
    private int pathListCounter = 0;
    private int enemyListCounter = 0;

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

    public List<List> generatePaths()
    {
        int randomEnemyTarget;
        Node path;
        Node enemyNode;
        Node enemyTarget;

        List<Node> enemyNodeList = new ArrayList<Node>();
        List<Node> enemyTargetList = new ArrayList<Node>();

        for(int i = 0; i < 1 ; ++i)
        {
            List<Node> pathList = new ArrayList<Node>();
            enemyNode = new Node(tcvsEnemy.get(i).yPos, tcvsEnemy.get(i).xPos, 0, 0, null);
            randomEnemyTarget = randomTarget.nextInt(tcvsPlayer.size());
            enemyTarget = new Node(tcvsPlayer.get(randomEnemyTarget).yPos, tcvsPlayer.get(randomEnemyTarget).xPos, 0, 0, null);
            enemyNodeList.add(enemyNode);
            enemyTargetList.add(enemyTarget);

            path = pathfinding.IAmAPathAndILikeCheese(tmBattleGround, enemyNode, enemyTarget);
            while(path != null)
            {
                pathList.add(path);
                path = path.parentNode;
            }
            Collections.reverse(pathList);

            pathListContainer.add(pathList);
        }

        return pathListContainer;
    }

    public void moveEnemy(List<List> plContainer)
    {
        /*if(!checkWithinShotRange(pathListContainer, 1))
        {*/
            if (enemyListCounter < tcvsEnemy.size())
            {
                /*tcvsEnemy.get(enemyListCounter).xPos = plContainer.get(pathListCounter).;
                tcvsEnemy.get(enemyListCounter).yPos = plContainer.get(pathListCounter);*/
                enemyListCounter++;
                pathListCounter++;
            }
        //}
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
