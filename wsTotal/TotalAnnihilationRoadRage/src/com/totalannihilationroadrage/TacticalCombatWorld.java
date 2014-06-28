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
    public int enemyMoved = 0;

	TacticalCombatWorld (TiledMap tmBG, List< TacticalCombatVehicle > tcvsP, List< TacticalCombatVehicle > tcvsE)
	{
        tcvsPlayer = tcvsP;
        tcvsEnemy = tcvsE;
        tmBattleGround = tmBG;
        deployVehicles();
        chooseTarget();
	}

    public void moveAllVehicles()
    {
        for(int i = 0; i < tcvsPlayer.size(); ++i)
        {
            if (!tcvsPlayer.get(i).isMoved)
            {
                tcvsPlayer.get(i).move();
            }
        }
    }

    public boolean allPlayerVehiclesMoved ()
    {
        for (int i = 0; i < tcvsPlayer.size(); ++i)
        {
            if (!tcvsPlayer.get(i).isMoved)
            {
                return false;
            }
        }
        return true;
    }

    public boolean allPlayerVehiclesAttacked ()
    {
        for (int i = 0; i < tcvsPlayer.size(); ++i)
        {
            if (!tcvsPlayer.get(i).isAttacked)
            {
                return false;
            }
        }
        return true;
    }

    public void findEnemiesInRange ()
    {
        for (int i = 0; i < tcvsPlayer.size(); ++i)
        {
            tcvsPlayer.get(i).getEnemiesInRange(tcvsEnemy);
        }
    }

    public void resetVehicles ()
    {
        for (int i = 0; i < tcvsPlayer.size(); ++i)
        {
            tcvsPlayer.get(i).resetValues();
        }
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
            tcvsEnemy.get(i).target = randomTarget.nextInt(tcvsPlayer.size() - 1);
        }
    }

    public void generatePath()
    {
        for (int i = 0; i < tcvsEnemy.size() - 1; ++i)
        {
            Node enemyNode = new Node(tcvsEnemy.get(i).yPos, tcvsEnemy.get(i).xPos, 0, 0, null);
            Node targetNode = new Node(tcvsPlayer.get(tcvsEnemy.get(i).target).yPos, tcvsPlayer.get(tcvsEnemy.get(i).target).xPos, 0, 0, null);
            tcvsEnemy.get(i).thePath = pathfinding.IAmAPathAndILikeCheese(tmBattleGround, enemyNode, targetNode);
        }

        if(tcvsEnemy.get(enemyCounter).thePath != null)
        {
            moveEnemy();
            enemyMoved++;
        }
        else
        {
            enemyCounter++;
        }
    }

    public void moveEnemy()
    {
        if(tcvsEnemy.get(enemyCounter).thePath == null)
        {
            return;
        }

        if(enemyCounter < tcvsEnemy.size() - 1)
        {
            tcvsEnemy.get(enemyCounter).xPos = tcvsEnemy.get(enemyCounter).thePath.get(0).col;
            tcvsEnemy.get(enemyCounter).yPos = tcvsEnemy.get(enemyCounter).thePath.get(0).row;
            enemyCounter++;
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

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max)
    {

        // Usually this should be a field rather than a method variable so
        // that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private void calculateDeaths (int numAttackers, int attackBonus, GangMembers killedGangMembers)
    {
        int shotAt, attackRoll;

        for (int i = 0; i < numAttackers; ++i)
        {
            attackRoll = randInt (1, 20);
            shotAt = randInt (0, 4);
            switch (shotAt)
            {
                case 0:
                    if ((attackRoll + attackBonus) > (10 + 4))
                    {
                        killedGangMembers.armsmasters++;
                    }
                    break;
                case 1:
                    if ((attackRoll + attackBonus) > (10 + 3))
                    {
                        killedGangMembers.bodyguards++;
                    }
                    break;
                case 2:
                    if ((attackRoll + attackBonus) > (10 + 2))
                    {
                        killedGangMembers.commandos++;
                    }
                    break;
                case 3:
                    if ((attackRoll + attackBonus) > (10 + 1))
                    {
                        killedGangMembers.dragoons++;
                    }
                    break;
                case 4:
                    if ((attackRoll + attackBonus) > (10 + 0))
                    {
                        killedGangMembers.escorts++;
                    }
                    break;
            }
        }
    }

    public CombinedGangMembers shootRound(GangMembers gmAttacking, GangMembers gmDefending, int distance, boolean isCrossbows)
    {
        CombinedGangMembers gangs = new CombinedGangMembers();
        int rangePenalty = 0;
        GangMembers defenderAdjust = new GangMembers(gmDefending);

        if (isCrossbows)
        {
            if (distance < 6)
            {
                rangePenalty = (distance - 1) * -2;
            }
        }
        else
        {
            rangePenalty = -(distance - 1);
        }

        if (!isCrossbows || (isCrossbows && (distance < 6)))
        {
            calculateDeaths (gmAttacking.armsmasters, 2 + rangePenalty, gangs.defender);
            calculateDeaths (gmAttacking.bodyguards, 1 + rangePenalty, gangs.defender);
            calculateDeaths (gmAttacking.commandos, 0 + rangePenalty, gangs.defender);
            calculateDeaths (gmAttacking.dragoons, -1 + rangePenalty, gangs.defender);
            calculateDeaths (gmAttacking.escorts, -2 + rangePenalty, gangs.defender);

            defenderAdjust.armsmasters -= gangs.defender.armsmasters;
            defenderAdjust.bodyguards -= gangs.defender.bodyguards;
            defenderAdjust.commandos -= gangs.defender.commandos;
            defenderAdjust.dragoons -= gangs.defender.dragoons;
            defenderAdjust.escorts -= gangs.defender.escorts;

            calculateDeaths (defenderAdjust.armsmasters, 2 + rangePenalty, gangs.attacker);
            calculateDeaths (defenderAdjust.bodyguards, 1 + rangePenalty, gangs.attacker);
            calculateDeaths (defenderAdjust.commandos, 0 + rangePenalty, gangs.attacker);
            calculateDeaths (defenderAdjust.dragoons, -1 + rangePenalty, gangs.attacker);
            calculateDeaths (defenderAdjust.escorts, -2 + rangePenalty, gangs.attacker);
        }

        return gangs;
    }
}
