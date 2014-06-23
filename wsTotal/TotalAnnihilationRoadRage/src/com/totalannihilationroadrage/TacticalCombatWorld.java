package com.totalannihilationroadrage;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                fields[x][y] = tmBattleGround.layers.get(0).isPassable (y, x);
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

        for(int i = 0; i < 1; ++i)
        {
            enemyNode = new Node(tcvsEnemy.get(i).yPos, tcvsEnemy.get(i).xPos, 0, 0, null);
            randomEnemyTarget = randomTarget.nextInt(tcvsPlayer.size());
            enemyTarget = new Node(tcvsPlayer.get(randomEnemyTarget).yPos, tcvsPlayer.get(randomEnemyTarget).xPos, 0, 0, null);

            path = pathfinding.IAmAPathAndILikeCheese(tmBattleGround, enemyNode, enemyTarget);
            enemyNodeList.add(enemyNode);
            enemyTargetList.add(enemyTarget);

            /*while(path != null)
            {
                tmBattleGround.drawRect(path.col * tmBattleGround.tileset.tileWidth, path.row * tmBattleGround.tileset.tileHeight, tmBattleGround.tileset.tileWidth, tmBattleGround.tileset.tileHeight, Color.RED);
                //System.out.println("Node Row " + node.col + ", Node Col " + node.row);
                path = path.parentNode;
            }*/
            /*tcvsEnemy.get(i).xPos = path.col;
            tcvsEnemy.get(i).yPos = path.row;*/
        }

    }
	
}
