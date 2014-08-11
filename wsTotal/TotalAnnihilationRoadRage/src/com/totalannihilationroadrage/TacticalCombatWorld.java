package com.totalannihilationroadrage;

import java.util.List;
import java.util.Random;

public class TacticalCombatWorld 
{
    enum ShootingAt
    {
        Interior,
        Exterior,
        Tires
    }

    private int roundsPlayer;
    private int roundsEnemy;
    public List< TacticalCombatVehicle > tcvsPlayer;
    public List< TacticalCombatVehicle > tcvsEnemy;
    public TiledMap tmBattleGround;

    private boolean fields[][] = new boolean[40][15];
    private Random random = new Random();
    private Random randomTarget = new Random();
    TacticalCombatPathfinding tcPathfinding = new TacticalCombatPathfinding();
    private int enemyCounter = 0;
    public int enemyMoved = 0;

    public boolean allEnemiesDead ()
    {
        for(int i = 0; i < tcvsEnemy.size(); ++i)
        {
            if (!tcvsEnemy.get(i).isDead)
            {
                return false;
            }
        }
        return true;
    }

    public boolean allPlayersDead ()
    {
        for(int i = 0; i < tcvsPlayer.size(); ++i)
        {
            if (!tcvsPlayer.get(i).isDead)
            {
                return false;
            }
        }
        return true;
    }

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
            if (tcvsPlayer.get(i).isDead)
            {
                tcvsPlayer.get(i).isMoved = true;
                continue;
            }
            if (!tcvsPlayer.get(i).isMoved)
            {
                tcvsPlayer.get(i).move();
            }
        }
        for(int i = 0; i < tcvsPlayer.size(); ++i)
        {
            if (tcvsPlayer.get(i).isDead)
            {
                continue;
            }
            if (vehicleCrashed(tcvsPlayer.get(i)) || isAnotherVehicleHere(tcvsPlayer.get(i)))
            {
                tcvsPlayer.get(i).reverse();
                tcvsPlayer.get(i).die();
                makeVehicleDestroyedTile(tcvsPlayer.get(i));
                Assets.explosion.play(1);
            }
        }
    }

    public void moveVehicle(TacticalCombatVehicle vehicle)
    {
        if (vehicle.isDead)
        {
            vehicle.isMoved = true;
            return;
        }

        if (!vehicle.isMoved)
        {
            vehicle.move();
            vehicle.isMoved = true;
        }
        if (vehicleCrashed(vehicle) || isAnotherVehicleHere(vehicle))
        {
            vehicle.reverse();
            vehicle.die();
            vehicle.alive.remove(vehicle);
            makeVehicleDestroyedTile(vehicle);
            Assets.explosion.play(1);
        }
    }

    private boolean vehicleCrashed(TacticalCombatVehicle vehicle)
    {
        return !tmBattleGround.isPassable(vehicle.yPos, vehicle.xPos);
    }

    private boolean isAnotherVehicleHere (TacticalCombatVehicle vehicle)
    {
        for (int i = 0; i < tcvsPlayer.size(); ++i)
        {
            if ((tcvsPlayer.get(i).vehicle.id != vehicle.vehicle.id) && (tcvsPlayer.get(i).xPos == vehicle.xPos) && (tcvsPlayer.get(i).yPos == vehicle.yPos))
            {
                tcvsPlayer.get(i).die();
                makeVehicleDestroyedTile(tcvsPlayer.get(i));
                tcvsPlayer.get(i).alive.remove(tcvsPlayer.get(i));
                Assets.explosion.play(1);
                return true;
            }
        }
        for (int i = 0; i < tcvsEnemy.size(); ++i)
        {
            if ((tcvsEnemy.get(i).vehicle.id != vehicle.vehicle.id) && (tcvsEnemy.get(i).xPos == vehicle.xPos) && (tcvsEnemy.get(i).yPos == vehicle.yPos))
            {
                tcvsEnemy.get(i).die();
                makeVehicleDestroyedTile(tcvsEnemy.get(i));
                tcvsEnemy.get(i).alive.remove(tcvsEnemy.get(i));
                Assets.explosion.play(1);
                return true;
            }
        }
        return false;
    }

    private void makeVehicleDestroyedTile (TacticalCombatVehicle vehicle)
    {
        tmBattleGround.setDestroyedVehicle(vehicle.yPos, vehicle.xPos);
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

    public boolean allEnemyVehiclesAttacked()
    {
        for(int i = 0; i < tcvsEnemy.size(); ++i)
        {
            if(!tcvsEnemy.get(i).isAttacked)
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

    public void findPlayersInRange()
    {
        for(int i = 0; i < tcvsEnemy.size(); ++i)
        {
            tcvsEnemy.get(i).getEnemiesInRange(tcvsPlayer);
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
                    deployX = deployXOffset;
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
        for (int i = 0; i <= tcvsEnemy.size() - 1; ++i)
        {
            if(tcvsPlayer.get(tcvsEnemy.get(i).target).isDead)
            {
                tcvsEnemy.get(i).target = randomTarget.nextInt(tcvsPlayer.size() - 1);
            }
            Node enemyNode = new Node(tcvsEnemy.get(i).yPos, tcvsEnemy.get(i).xPos, 0, 0, null, tcvsEnemy.get(i).facing);
            Node targetNode = new Node(tcvsPlayer.get(tcvsEnemy.get(i).target).yPos, tcvsPlayer.get(tcvsEnemy.get(i).target).xPos, 0, 0, null, tcvsPlayer.get(tcvsEnemy.get(i).target).facing);

            tcvsEnemy.get(i).thePath = tcPathfinding.IAmAPathAndILikeCheese(tmBattleGround, enemyNode, targetNode);
        }
        if(enemyCounter == tcvsEnemy.size())
        {
            enemyCounter = 0;
        }
        if(tcvsEnemy.get(enemyCounter).thePath != null && tcvsEnemy.get(enemyCounter).thePath.size() != 0)
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
        if(enemyCounter < tcvsEnemy.size())
        {
            if (!tcvsEnemy.get(enemyCounter).isDead)
            {
                tcvsEnemy.get(enemyCounter).facing = tcvsEnemy.get(enemyCounter).thePath.get(0).facing;
                Direction.getDirectionVector(tcvsEnemy.get(enemyCounter).facing);
                tcvsEnemy.get(enemyCounter).xPos = tcvsEnemy.get(enemyCounter).thePath.get(0).col;
                tcvsEnemy.get(enemyCounter).yPos = tcvsEnemy.get(enemyCounter).thePath.get(0).row;
                if (vehicleCrashed(tcvsEnemy.get(enemyCounter)) || isAnotherVehicleHere(tcvsEnemy.get(enemyCounter)))
                {
                    tcvsEnemy.get(enemyCounter).reverse();
                    tcvsEnemy.get(enemyCounter).isDead = true;
                    tcvsEnemy.get(enemyCounter).alive.remove(tcvsEnemy.get(enemyCounter));
                    Assets.explosion.play(1);
                }
            }
            enemyCounter++;
        }
        else
        {
            return;
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

    private int calculateTiresHit (int numAttackers, int attackBonus)
    {
        int numTiresHit, attackRoll;

        numTiresHit = 0;
        for (int i = 0; i < numAttackers; ++i)
        {
            attackRoll = randInt (1, 20);
            if ((attackRoll + attackBonus) > (10 + 5))
            {
                ++numTiresHit;
            }
        }

        return numTiresHit;
    }

    public CombinedGangMembers shootRound(GangMembers gmAttacking, GangMembers gmDefending, VehicleStatsCurrent defVehicle, int distance, boolean isCrossbows, int attackModifier, boolean attackTires)
    {
        CombinedGangMembers gangs = new CombinedGangMembers();
        int rangePenalty = 0;
        GangMembers defenderAdjust = new GangMembers(gmDefending);
        int tiresShot = 0;

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
            if (attackTires)
            {
                tiresShot = calculateTiresHit (gmAttacking.armsmasters, 2 + attackModifier + rangePenalty);
                tiresShot += calculateTiresHit (gmAttacking.bodyguards, 1 + attackModifier + rangePenalty);
                tiresShot += calculateTiresHit (gmAttacking.commandos, 0 + attackModifier + rangePenalty);
                tiresShot += calculateTiresHit (gmAttacking.dragoons, -1 + attackModifier + rangePenalty);
                tiresShot += calculateTiresHit (gmAttacking.escorts, -2 + attackModifier + rangePenalty);
            }
            else
            {
                calculateDeaths (gmAttacking.armsmasters, 2 + attackModifier + rangePenalty, gangs.defender);
                calculateDeaths (gmAttacking.bodyguards, 1 + attackModifier + rangePenalty, gangs.defender);
                calculateDeaths (gmAttacking.commandos, 0 + attackModifier + rangePenalty, gangs.defender);
                calculateDeaths (gmAttacking.dragoons, -1 + attackModifier + rangePenalty, gangs.defender);
                calculateDeaths (gmAttacking.escorts, -2 + attackModifier + rangePenalty, gangs.defender);

                defenderAdjust.armsmasters -= gangs.defender.armsmasters;
                defenderAdjust.bodyguards -= gangs.defender.bodyguards;
                defenderAdjust.commandos -= gangs.defender.commandos;
                defenderAdjust.dragoons -= gangs.defender.dragoons;
                defenderAdjust.escorts -= gangs.defender.escorts;
            }

            if (tiresShot < defVehicle.tires)
            {
                calculateDeaths (defenderAdjust.armsmasters, 2 + attackModifier + rangePenalty, gangs.attacker);
                calculateDeaths (defenderAdjust.bodyguards, 1 + attackModifier + rangePenalty, gangs.attacker);
                calculateDeaths (defenderAdjust.commandos, 0 + attackModifier + rangePenalty, gangs.attacker);
                calculateDeaths (defenderAdjust.dragoons, -1 + attackModifier + rangePenalty, gangs.attacker);
                calculateDeaths (defenderAdjust.escorts, -2 + attackModifier + rangePenalty, gangs.attacker);
            }
        }

        defVehicle.tires = stopAtZero(defVehicle.tires - tiresShot);

        gmAttacking.armsmasters = stopAtZero(gmAttacking.armsmasters - gangs.attacker.armsmasters);
        gmAttacking.bodyguards = stopAtZero(gmAttacking.bodyguards - gangs.attacker.bodyguards);
        gmAttacking.commandos = stopAtZero(gmAttacking.commandos - gangs.attacker.commandos);
        gmAttacking.dragoons = stopAtZero(gmAttacking.dragoons - gangs.attacker.dragoons);
        gmAttacking.escorts = stopAtZero(gmAttacking.escorts - gangs.attacker.escorts);
        gmDefending.armsmasters = stopAtZero(gmDefending.armsmasters - gangs.defender.armsmasters);
        gmDefending.bodyguards = stopAtZero(gmDefending.bodyguards - gangs.defender.bodyguards);
        gmDefending.commandos = stopAtZero(gmDefending.commandos - gangs.defender.commandos);
        gmDefending.dragoons = stopAtZero(gmDefending.dragoons - gangs.defender.dragoons);
        gmDefending.escorts = stopAtZero(gmDefending.escorts - gangs.defender.escorts);

        gangs.attacker.armsmasters = gmAttacking.armsmasters;
        gangs.attacker.bodyguards = gmAttacking.bodyguards;
        gangs.attacker.commandos = gmAttacking.commandos;
        gangs.attacker.dragoons = gmAttacking.dragoons;
        gangs.attacker.escorts = gmAttacking.escorts;
        gangs.defender.armsmasters = gmDefending.armsmasters;
        gangs.defender.bodyguards = gmDefending.bodyguards;
        gangs.defender.commandos = gmDefending.commandos;
        gangs.defender.dragoons = gmDefending.dragoons;
        gangs.defender.escorts = gmDefending.escorts;

        return gangs;
    }

    private int stopAtZero (int value)
    {
        return (value < 0) ? 0 : value;
    }
}
/*
                       /\             /\
                      |`\\_,--="=--,_//`|
                      \ ."  :'. .':  ". /
                     ==)  _ :  '  : _  (==
                       |>/O\   _   /O\<|
                       | \-"~` _ `~"-/ |
                      >|`===. \_/ .===`|<
            jgs .-"-.   \==='  |  '===/   .-"-.
         .-----{'. '`}---\,  .-'-.  ,/---{.'. '}-----.
         |     `"---"`     `~-===-~`     `"---"`     |
         |     .-------------------------------.     |
         |     |                               |     |
         |     |                               |     |
         |     |                    .--.       |     |
         |     |                  ."  o \__    |     |
         |     |               _.-"    ,(  `   |     |
         |     |           _.-"      ,;;|      |     |
         |     |      _.-=" _,"    ,,;;;'      |     |\
         |     |  .-"`_.-"``-..,,;;;;:'        |     |'\
         |     |  `"'`          `\`\           |     |.'`\
         |     |                 /^\\\         |     |'.'`}
         |     |                               |     } '. }
         |     |                               |    /.'`./
         |     '-------------------------------'   /. './
        _|  _                                     /.`'./
       (.\-/.)--------.__________________.-------{.'. {
        \6 6/         /                  \       `\.'`\
    (   =\ /=  ______/....................\______  \.'.\
     \   /O\  |                                  |  }'.'}
      ) /   \ | [[[[[[[[[[ ===       ()  ()  ()  | {'.'/
     (  U   U |                       ________   |  `~`
      \(|   |)| [[[[[[[[[[ ===       [________]  |
       _w___w_:----------------------------------:_______
      |  []  [][][][] [][][][] [][][][] [][][]  == ===== |
      |                                                  |
      |   [][][][][][][][][][][][][][]_ [][][] [][][][]  |
      |   [_][][][][][][][][][][][][]| |[][][] [][][]||  |
      |   []  [][][][][][][][][][][][__|       [][][]||  |
      |   [__] [][][][][][][][][][][___]  []   [][][]||  |
      |   [_]  [_][_____________][_] [_][][][] [__][]||  |
      '--------------------------------------------------'
 */