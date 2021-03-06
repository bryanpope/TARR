package com.totalannihilationroadrage;
import com.framework.impl.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.VelocityTracker;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;
import com.framework.impl.AndroidPixmap;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TacticalCombatScreen extends Screen
{
    private final float TIME_BUTTON_FLASH_TOTAL = 1.0f;
    private final float TIME_BUTTON_FLASH_ON_OFF = 0.1f;

	enum GameState
	{
		Ready,
		Running,
		Paused
	}
        //test
    enum PhaseStates
    {
        notActive,
        Moving,
        Attack,
        DisplayCasualties,
        EnemyAttack,
        CrewTransfer,
    }

    enum StateCombatReport
    {
        DefenderDeaths,
        AttackerDeaths,
        TireDeaths
    }

    enum FlashingButton
    {
        NotActive,
        FlashMoveAll,
        FlashAttackOutside,
        FlashAttackInside,
        FlashAttackTires;
    }

    private FlashingButton currentFlashingButtonValue;

	GameState state = GameState.Running;
    PhaseStates pState = PhaseStates.Moving;
    StateCombatReport stateCombatReport = StateCombatReport.DefenderDeaths;
    FlashingButton flashingButton = FlashingButton.NotActive;
    Direction dir = Direction.EAST;
    TacticalCombatWorld tcWorld;
    TiledMap tMap;
    PhaseStates prevState;
    private CombinedGangMembers killList;
    private int mTiresLeft;

    boolean updateAttackAreaSelection = false;
    boolean drawSpark = false;
    boolean enemyHasAttacked = false;
    boolean isTouched = false;
    boolean isTouched2 = false;
    boolean isTouched3 = false;

    private int cameraTopRow = 0;     // For scrolling purposes, the start column in the tile map to display from
    private int cameraLeftCol = 0;
    private int cameraX = 0;
    private int cameraY = 0;
    private int tileOffsetX = 0;
    private int tileOffsetY = 0;
    private int numRows = 0;
    private int numCols = 0;
    private float previousTouchX = 0;
    private float previousTouchY = 0;
    private int pointerId;
    private TacticalCombatVehicle selectedVehicle = null;
    private TacticalCombatVehicle selectedVehicleEnemy = null;
    private int enemyAttackCounter = 0;

    private List<Vector> currentBulletsPos;
    private int currentBulletsSpeed;
    private Vector bulletsEndPos;

    private VelocityTracker vTracker;
    private Vector velocity;

    private boolean isButtonFlashing = false;
    private boolean isButtonDisplayed = true;
    private float timeButtonFlashTotal = 0.0f;
    private float timeButtonFlashOnOff = 0.0f;


	public TacticalCombatScreen(Game game, TacticalCombatWorld tacticalCombatWorld, TiledMap tacticalMap)
	{
		super(game);
		tcWorld = tacticalCombatWorld;
        tMap = tacticalMap;
        numRows = (game.getGraphics().getHeight() / tcWorld.tmBattleGround.tileHeight) + 1;
        numCols = (game.getGraphics().getWidth() / tcWorld.tmBattleGround.tileWidth) + 1;
        velocity = new Vector();
	}

    /*public boolean attackPhase()
    {
        for(int j = 0; j < tcWorld.tcvsEnemy.get(j).vehicle.id; ++j)
        {
            Node enemyTarget = new Node(tcWorld.tcvsEnemy.get(j).yPos, tcWorld.tcvsEnemy.get(j).xPos, 0, 0, null);
            Node playerTarget = new Node(selectedVehicle.yPos, selectedVehicle.xPos, 0, 0, null);
            //if (selectedVehicle.isMoved && tcWorld.checkWithinShotRange(playerTarget, enemyTarget, 6))
            if (selectedVehicle.isMoved && tcWorld.checkWithinShotRange(playerTarget, enemyTarget, 6))
            {
                return true;
            }
        }
        return false;
    }*/

	public void update(float deltaTime)
	{
        Graphics g = game.getGraphics();
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        float x, y;
        if(selectedVehicle != null)
        {
            if(pState == PhaseStates.Moving)
            {
                updateMove(touchEvents, (selectedVehicle.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicle.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, selectedVehicle.facing);
            }
            if (pState == PhaseStates.Attack)
            {
                updateFire(touchEvents,(selectedVehicle.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicle.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, selectedVehicle.facing);
            }
        }
        if (selectedVehicleEnemy != null)
        {
            if (updateAttackAreaSelection && (pState == PhaseStates.Attack))
            {
                updateSelectAreaToAttack(touchEvents, (selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY);
            }
        }
        if(pState == PhaseStates.Moving)
        {
            updateMoveAll(touchEvents);
        }
        if(pState == PhaseStates.Attack)
        {
            updateSkip(touchEvents);
        }
        if(pState == PhaseStates.DisplayCasualties)
        {
            updateCombatReport(touchEvents);
        }
        if(pState == PhaseStates.EnemyAttack)
        {
            updateEnemyFire();
        }
        int len = touchEvents.size();
        if(!updateAttackAreaSelection)
        {
            for (int i = 0; i < len; i++) {
                Input.TouchEvent event = touchEvents.get(i);
                x = event.x;
                y = event.y;
                if (event.type == Input.TouchEvent.TOUCH_DOWN)
                {
                    previousTouchX = x;
                    previousTouchY = y;
                    pointerId = event.pointer;

                    /*if (vTracker == null)
                    {
                        vTracker = VelocityTracker.obtain();
                    }
                    else
                    {
                        vTracker.clear();
                    }*/
                    touchEvents.remove(i);
                    break;
                }

                if (event.type == Input.TouchEvent.TOUCH_UP)
                {
                    //if (!event.wasDragged)
                    //{
                        selectedVehicle = isVehicleTouched(event);
                    //}
                    //vTracker.recycle();
                    touchEvents.remove(i);
                    break;
                }

                if (event.type == Input.TouchEvent.TOUCH_DRAGGED)
                {
                    if (event.pointer != pointerId)
                    {
                        continue;
                    }

                    /*vTracker.addMovement(event.mEvent);
                    vTracker.computeCurrentVelocity(1000);
                    velocity.x = (int)vTracker.getXVelocity();
                    velocity.y = (int)vTracker.getYVelocity();*/

                    float dispX = x - previousTouchX;
                    float dispY = y - previousTouchY;
                    setCameraPosition(x - previousTouchX, y - previousTouchY);
                    //setCameraPosition(velocity.x * deltaTime, velocity.y * deltaTime);


                /*cameraTopRow += cameraOffsetY / tcWorld.tmBattleGround.tileHeight;
                cameraOffsetY %= tcWorld.tmBattleGround.tileHeight;

                cameraLeftCol += cameraOffsetX / tcWorld.tmBattleGround.tileWidth;
                cameraOffsetX %= tcWorld.tmBattleGround.tileWidth;*/
                }
                previousTouchX = x;
                previousTouchY = y;
                touchEvents.remove(i);
                break;
            }
        }
        if (updateAttackAreaSelection)
        {
            /*TacticalCombatVehicle vEnemy;
            for (int i = 0; i < tcWorld.tcvsEnemy.size(); ++i)
            {
                vEnemy = tcWorld.tcvsEnemy.get(i);
                updateSelectAreaToAttack(touchEvents, (vEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (vEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY);
            }*/
            //updateSelectAreaToAttack(touchEvents, (selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY);
        }

        if ((pState == PhaseStates.Moving) && tcWorld.allPlayerVehiclesMoved())
        {
            for(int i = 0; i < tcWorld.tcvsEnemy.size(); ++i)
            {
                tcWorld.generatePath();
            }
            switchToAttackPhase();
        }
        if ((pState == PhaseStates.Attack) && tcWorld.allPlayerVehiclesAttacked())
        {
            switchToMovePhase();
        }

        if (tcWorld.allPlayersDead() || tcWorld.allEnemiesDead())
        {
            game.setScreen(new WinLossScreen(game, !tcWorld.allPlayersDead()));
        }

        updateButtonAnimation(deltaTime);
	}

    private int moveToZero (int moveValue, int moveBy)
    {
        int ret = 0;

        if (moveValue == 0)
        {
            return moveValue;
        }
        if (moveValue < 0)
        {
            ret += moveBy;
            ret = (ret > 0) ? 0 : moveBy;
        }
        else if (moveValue > 0)
        {
            ret -= moveBy;
            ret = (ret < 0) ? 0 : moveBy;

        }
        return ret;
    }

    private void setCameraPosition (float dispX, float dispY)
    {
        Graphics g = game.getGraphics();
        int maxCameraX = (tcWorld.tmBattleGround.width * tcWorld.tmBattleGround.tileWidth) - g.getWidth();
        int maxCameraY = (tcWorld.tmBattleGround.height * tcWorld.tmBattleGround.tileHeight) - g.getHeight();

        cameraX -= Math.round(dispX);
        if (cameraX < 0)
        {
            cameraX = 0;
        }
        if (cameraX > maxCameraX)
        {
            cameraX = maxCameraX;
        }

        cameraY -= Math.round(dispY);
        if (cameraY < 0)
        {
            cameraY = 0;
        }
        if (cameraY > maxCameraY)
        {
            cameraY = maxCameraY;
        }

        cameraLeftCol = cameraX / tcWorld.tmBattleGround.tileWidth;
        tileOffsetX = -(cameraX % tcWorld.tmBattleGround.tileWidth);
        cameraTopRow = cameraY / tcWorld.tmBattleGround.tileHeight;
        tileOffsetY = -(cameraY % tcWorld.tmBattleGround.tileWidth);

        numRows = g.getHeight() / tcWorld.tmBattleGround.tileHeight;
        numRows += (tileOffsetY < 0) ? 1 : 0;
        //numRows = (int)Math.ceil(g.getHeight() / (double)tcWorld.tmBattleGround.tileHeight);
        //numRows += ((tcWorld.tmBattleGround.height - numRows) >= numRows) ? 1 : 0;

        numCols = g.getWidth() / tcWorld.tmBattleGround.tileWidth;
        numCols += (tileOffsetX < 0) ? 1 : 0;
        //numCols = (int)Math.ceil(g.getWidth() / (double)tcWorld.tmBattleGround.tileWidth);
        //numCols += ((tcWorld.tmBattleGround.width - numCols) >= numCols) ? 1 : 0;
    }

    private void switchToAttackPhase ()
    {
        tcWorld.resetVehicles();
        tcWorld.findEnemiesInRange();
        tcWorld.findPlayersInRange();
        pState = PhaseStates.Attack;
    }

    private void switchToMovePhase ()
    {
        tcWorld.enemyMoved = 0;
        enemyAttackCounter = 0;
        tcWorld.resetVehicles();
        pState = PhaseStates.Moving;
    }

    @Override
	public void present(float deltaTime)
	{
		Graphics g = game.getGraphics();
		//g.drawPixmap(Assets.background, 0, 0);
        g.clear(0);
		drawTacticalMap();
        drawVehicles(tcWorld.tcvsPlayer, tcWorld.tmBattleGround, false);
        drawVehicles(tcWorld.tcvsEnemy, tcWorld.tmBattleGround, true);

        if (selectedVehicle != null)
        {
            if (pState == PhaseStates.Moving)
            {
                drawUIPhaseMovement((selectedVehicle.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicle.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, selectedVehicle.facing);
            }
            else if (pState == PhaseStates.Attack)
            {

                if(isTouched)
                {
                    g.drawRect((selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX - 128, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, tMap.tileset.tileWidth, tMap.tileset.tileHeight, Color.GREEN);
                }
                if(isTouched2)
                {
                    g.drawRect((selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY - 128, tMap.tileset.tileWidth, tMap.tileset.tileHeight, Color.GREEN);
                }
                if(isTouched3)
                {
                    g.drawRect((selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX + 128, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, tMap.tileset.tileWidth, tMap.tileset.tileHeight, Color.GREEN);
                }
                drawUIPhaseFire((selectedVehicle.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicle.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, selectedVehicle.facing);
                if(updateAttackAreaSelection)
                {
                    //TacticalCombatVehicle vEnemy;
                    //for (int i = 0; i <  tcWorld.tcvsEnemy.size(); ++i)
                    //{
                        //vEnemy = tcWorld.tcvsEnemy.get(i);
                        //drawSelectAreaToAttack(vEnemy.xPos - cameraX, vEnemy.yPos - cameraY);

                    drawSelectAreaToAttack((selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY);
                    //}
                }
            }
            if(drawSpark)
            {
                drawDamage((selectedVehicleEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicleEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY);
            }
        }
        if(pState == PhaseStates.Moving)
        {
            drawMoveAll();
            if(selectedVehicle != null)
            {
               drawPlayerReport();
            }
        }
        if(pState == PhaseStates.Attack)
        {
            drawSkip();
        }
        if (pState == PhaseStates.DisplayCasualties)
        {
             drawCombatReport();
        }
        if(enemyHasAttacked)
        {
            for(int i = 0; i < tcWorld.tcvsPlayer.size(); ++i)
            {
                drawDamage((tcWorld.tcvsPlayer.get(i).xPos* tcWorld.tmBattleGround.tileWidth) - cameraX, (tcWorld.tcvsPlayer.get(i).yPos* tcWorld.tmBattleGround.tileHeight) - cameraY);
            }
        }
	}

    private void drawTacticalMap()
	{
		Graphics g = game.getGraphics();

	   // int tilesheetsize = (tMap.image.width / tMap.tileset.tileWidth) * (tMap.image.height / tMap.tileset.tileHeight);
		int tileSheetCol = tMap.image.width / tMap.tileset.tileWidth;
		//int mapsize = tMap.width * tMap.height;
		int destX, destY;
		int srcX, srcY;


        //int numRows = g.getHeight() / tMap.tileHeight;
        //int numCols = g.getWidth() / tMap.tileWidth;
        int indexTile;

        for (int i = 0; i < tMap.layers.size(); i++)
        {
            for (int row = cameraTopRow; (row - cameraTopRow) < numRows; ++row)
            {
                for (int col = cameraLeftCol; (col - cameraLeftCol) < numCols; ++col)
                {
                    destX = ((col - cameraLeftCol) * tMap.tileWidth) + tileOffsetX;
                    destY = ((row - cameraTopRow) * tMap.tileHeight) + tileOffsetY;
                    if (((row * tMap.layers.get(i).width) + col) >= tMap.layers.get(i).data.size())
                    {
                        //System.out.print("Out of range!");
                    }
                    indexTile = tMap.layers.get(i).getTile(row, col);
                    srcX = (indexTile % tileSheetCol) * tMap.tileWidth;
                    srcY = (indexTile / tileSheetCol) * tMap.tileWidth;
                    g.drawPixmap(tMap.image.pmImage, destX, destY, srcX, srcY, tMap.tileWidth, tMap.tileHeight);
                }
            }
        }
	}

    public boolean inBoundaryCheck(int touchXPos, int touchYPos, int boxX, int boxY, int boxWidth, int boxHeight)
    {
        //logic
        if((touchXPos >= boxX) && (touchXPos <= boxX + boxWidth) &&
                (touchYPos >= boxY) && (touchYPos <= boxY + boxHeight))
        {
            return true;
        }
        else
            return false;

    }

	private void drawVehicles(List< TacticalCombatVehicle > vehicles, TiledMap tMap, boolean isEnemy)
	{
		Graphics g = game.getGraphics();

		int tileSheetCol = tMap.image.width / tMap.tileset.tileWidth;
		int destX, destY;
		int srcX, srcY;
        int tileColour = Color.BLACK;

		for (int i = 0; i < vehicles.size(); ++i)
		{
			destX = (vehicles.get(i).xPos * tMap.tileset.tileWidth) - cameraX;
			destY = (vehicles.get(i).yPos * tMap.tileset.tileHeight) - cameraY;
			int t_element =  vehicles.get(i).isDead ? Assets.vehicleStats.INDEX_DESTROYED_CAR_TILES + 1 : vehicles.get(i).vehicle.statsBase.type.ordinal() + Assets.vehicleStats.INDEX_START_CAR_TILES;
			srcY = (t_element / tileSheetCol) * tMap.tileset.tileWidth;
			srcX = (t_element % tileSheetCol) * tMap.tileset.tileHeight;
            vehicles.get(i).alive.add(vehicles.get(i));
            if (isEnemy)
            {
                tileColour = Color.RED;
                if (pState == PhaseStates.Attack)
                {
                    tileColour = Color.rgb(237,100,109);
                    if ((selectedVehicle != null) && !selectedVehicle.isAttacked)
                    {
                        for (int j = 0; j < selectedVehicle.enemiesInRange.size(); ++j)
                        {
                            if (selectedVehicle.enemiesInRange.get(j).vehicle.id == vehicles.get(i).vehicle.id)
                            {
                                tileColour = Color.RED;
                                break;
                            }
                        }
                    }
                }
            }
            else
            {
                if (pState == PhaseStates.Moving)
                {
                    if (vehicles.get(i).isMoved)
                    {
                        tileColour = Color.rgb(255,250,130);
                    }
                    else
                    {
                        tileColour = Color.YELLOW;
                    }
                }
                if (pState == PhaseStates.Attack)
                {
                    int test = vehicles.get(i).enemiesInRange.size();
                    if (vehicles.get(i).isAttacked || (vehicles.get(i).enemiesInRange.size() == 0))
                    {
                        tileColour = Color.rgb(255,250,130);
                    }
                    else
                    {
                        tileColour = Color.YELLOW;
                    }
                }
            }
            if (vehicles.get(i).isDead)
            {
                tileColour = Color.TRANSPARENT;

            }
            g.drawRect(destX, destY, tMap.tileset.tileWidth, tMap.tileset.tileHeight, tileColour);
            //g.drawRect(destX, destY, tMap.tileset.tileWidth, tMap.tileset.tileHeight, isEnemy ? Color.RED : vehicles.get(i).isMoved ?  Color.rgb(255,250,130) : Color.YELLOW);
			//g.drawPixmap(Assets.vehicleStats.tileSheetVehicles, destX, destY, srcX, srcY, tMap.tileset.tileWidth, tMap.tileset.tileHeight);
            g.drawPixmap(Assets.vehicleStats.tileSheetVehicles, destX, destY, srcX, srcY, tMap.tileset.tileWidth, tMap.tileset.tileHeight, Direction.getRotationTransformation(vehicles.get(i).facing));

            //drawUIPhaseMovement(destX, destY);
		}
	}

    private void drawBmap(Canvas c, Bitmap bMap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        Rect srcRect = new Rect();
        Rect dstRect = new Rect();

        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        c.drawBitmap(bMap, srcRect, dstRect, null);
    }

    private void drawUIPhaseMovement (int posX, int posY, Direction facing)
    {
        if (selectedVehicle.isMoved  || selectedVehicle.isDead)
        {
            return;
        }

        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 23;
        int numColumns = 4;
        int srcX, srcY;

        g.drawText(String.valueOf(selectedVehicle.speedCurrent), posX + (int)(tileWidth * 0.5), posY - (int)(tileHeight * 0.1), Color.WHITE, 72, Paint.Align.CENTER);

        Bitmap bUI = Bitmap.createBitmap(tileWidth * 4, tileHeight * 3, Bitmap.Config.ARGB_8888);
        Canvas cUI = new Canvas(bUI);

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        //if(selectedVehicle.isStraight)
        //{
            //g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY, srcX, srcY, tileWidth, tileHeight);            //move straight
        if (selectedVehicle.allowMoving())
        {
            drawBmap(cUI, ((AndroidPixmap)Assets.roadTileSheet).bitmap, tileWidth * 3, tileHeight, srcX, srcY, tileWidth, tileHeight);
        }
        //}

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        /*if(selectedVehicle.isLeft)
        {
            //g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY - tileHeight, srcX, srcY, tileWidth, tileHeight);      //move left
            drawBmap(cUI, ((AndroidPixmap)Assets.roadTileSheet).bitmap, tileWidth * 3, 0, srcX, srcY, tileWidth, tileHeight);
        }*/

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        /*if(selectedVehicle.isRight)
        {
            //g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY + tileHeight, srcX, srcY, tileWidth, tileHeight);       // move right
            drawBmap(cUI, ((AndroidPixmap)Assets.roadTileSheet).bitmap, tileWidth * 3, tileHeight * 2, srcX, srcY, tileWidth, tileHeight);
        }*/

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        if(selectedVehicle.allowTurning())
        {
            //if (selectedVehicle.isTurnedLeft)
            //{
                //g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);       //left turn
                drawBmap(cUI, ((AndroidPixmap) Assets.roadTileSheet).bitmap, tileWidth * 2, 0, srcX, srcY, tileWidth, tileHeight);
            //}
        }

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        if(selectedVehicle.allowTurning())
        {
            //if (selectedVehicle.isTurnedRight)
            //{
                //g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);       //right turn
                drawBmap(cUI, ((AndroidPixmap) Assets.roadTileSheet).bitmap, tileWidth * 2, tileHeight * 2, srcX, srcY, tileWidth, tileHeight);
            //}
        }

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        if(selectedVehicle.allowAcceleration())
        {
            //g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //accelerate
            drawBmap(cUI, ((AndroidPixmap)Assets.roadTileSheet).bitmap, tileWidth * 2, tileHeight, srcX, srcY, tileWidth, tileHeight);
        }

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        if(selectedVehicle.allowBreaking())
        {
            //g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //break
            drawBmap(cUI, ((AndroidPixmap)Assets.roadTileSheet).bitmap, 0, tileHeight, srcX, srcY, tileWidth, tileHeight);
        }

        g.drawPixmap(bUI, posX - tileWidth, posY - tileHeight, 0, 0, tileWidth * 4, tileHeight * 3, posX + (int)(tileWidth * 0.5), posY + (int)(tileHeight * 0.5), Direction.getRotationTransformation(facing));

        /*g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, 32, 224, tileWidth, tileHeight);            //break
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, 96, 192, tileWidth, tileHeight);            //accelerate
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY - 32, 64, 192, tileWidth, tileHeight);       //left turn
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY + 32, 32, 192, tileWidth, tileHeight);       //right turn
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY, 0, 256, tileWidth, tileHeight);            //move straight
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY + 32, 0, 192, tileWidth, tileHeight);       // move right
        g.drawPixmap(Assets.roadTileSheet, posX + (tileWidth * 2), posY - 32, 96, 160, tileWidth, tileHeight);      //move left
        */
    }

    public TacticalCombatVehicle isVehicleTouched(Input.TouchEvent event)
    {
        //int tileWidth = 128;
        //int tileHeight = 128;
        int x, y;

        for(int j = 0; j < tcWorld.tcvsPlayer.size(); ++j)
        {
            x = (tcWorld.tcvsPlayer.get(j).xPos * tcWorld.tmBattleGround.tileWidth) - cameraX;
            y = (tcWorld.tcvsPlayer.get(j).yPos * tcWorld.tmBattleGround.tileHeight) - cameraY;
            if(inBoundaryCheck(event.x, event.y, x, y, tcWorld.tmBattleGround.tileWidth, tcWorld.tmBattleGround.tileHeight) && !tcWorld.tcvsPlayer.get(j).isDead)
            {
                return tcWorld.tcvsPlayer.get(j);
            }
        }
        return null;
    }

    public void updateMove(List<Input.TouchEvent> touchEvents, int posX, int posY, Direction facing)
    {
        if (selectedVehicle.isMoved || selectedVehicle.isDead)
        {
            return;
        }

        Graphics g = game.getGraphics();
        Canvas c = g.getCanvas();
        //update the moving
        int tileWidth = 128;
        int tileHeight = 128;
        int eX, eY;

        int len = touchEvents.size();

        Matrix matrix = new Matrix();
        float angle = -(Direction.getRotationTransformation(facing).angle);
        matrix.setRotate(angle, posX + (int)(tileWidth * 0.5), posY + (int)(tileHeight * 0.5));
        float[] points = new float[2];
        //c.save(Canvas.MATRIX_SAVE_FLAG);
        //g.rotateCanvas(posX + (int)(tileWidth * 0.5), posY + (int)(tileHeight * 0.5), Direction.getAngle(facing));

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                points[0] = event.x;
                points[1] = event.y;
                matrix.mapPoints(points);
                eX = (int)points[0];
                eY = (int)points[1];
                if(selectedVehicle.allowMoving())
                {
                    if (inBoundaryCheck(eX, eY, posX + (tileWidth * 2), posY, tileWidth, tileHeight)) {
                        //Move straight
                        System.out.println("moved straight");
                        //selectedVehicle.move();
                        //selectedVehicle.isMoved = true;
                        tcWorld.moveVehicle(selectedVehicle);
                        touchEvents.remove(i);
                        break;
                    }
                }

                if(selectedVehicle.allowTurning())
                {
                    if (inBoundaryCheck(eX, eY, posX + tileWidth, posY - tileHeight, tileWidth, tileHeight))
                    {
                        //Left turn
                        dir = Direction.turnLeft(dir);
                        selectedVehicle.turnLeft();
                        System.out.println("left turn " + dir);
                        touchEvents.remove(i);
                        break;
                    }
                }

                if(selectedVehicle.allowTurning())
                {
                    if (inBoundaryCheck(eX, eY, posX + tileWidth, posY + tileHeight, tileWidth, tileHeight))
                    {
                        //right turn
                        dir = Direction.turnRight(dir);
                        selectedVehicle.turnRight();
                        System.out.println("right turn " + dir);
                        touchEvents.remove(i);
                        break;
                    }
                }
                if(inBoundaryCheck(eX, eY, posX + tileWidth, posY, tileWidth, tileHeight))
                {
                    //accelerate
                    System.out.println("accelerate");
                    selectedVehicle.accelerate();
                    touchEvents.remove(i);
                    break;
                }
                if(inBoundaryCheck(eX, eY, posX - tileWidth, posY, tileWidth, tileHeight))
                {
                    //break
                    System.out.println("break");
                    selectedVehicle.isBraked = true;
                    selectedVehicle.isAccelerated = false;
                    selectedVehicle.brake();
                    touchEvents.remove(i);
                    break;
                }

            }
        }
        //c.restore();
    }

    private void drawUIPhaseFire(int posX, int posY, Direction facing)
    {
        if (selectedVehicle.isAttacked || selectedVehicle.isDead)
        {
            return;
        }

        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 19;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);            //attack down

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //attack left

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //attack up

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //attack right

        index += 9;
        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, srcX, srcY, tileWidth, tileHeight);            //skip
    }

    private void updateFire(List<Input.TouchEvent> touchEvents, int posX, int posY, Direction facing)
    {
        if (selectedVehicle.isAttacked)
        {
            return;
        }

        //update the fire attack
        int tileWidth = 128;
        int tileHeight = 128;

        int len = touchEvents.size();
        TacticalCombatVehicle vEnemy;

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {
                for (int j = 0; j < selectedVehicle.enemiesInRange.size(); ++j)
                {
                    vEnemy = selectedVehicle.enemiesInRange.get(j);
                    if (inBoundaryCheck(event.x, event.y, (vEnemy.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (vEnemy.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, tileWidth, tileHeight))
                    {
                        //playGunSound();
                        //executeCombat(selectedVehicle, vEnemy);
                        //selectedVehicle.isAttacked = true;
                        //prevState = PhaseStates.Attack;
                        //pState = PhaseStates.DisplayCasualties;
                        selectedVehicleEnemy = vEnemy;
                        updateAttackAreaSelection = true;
                        isTouched = false;
                        isTouched2 = false;
                        isTouched3 = false;
                        touchEvents.remove(i);
                        break;
                    }
                }
            }
            if (updateAttackAreaSelection)
            {
                break;
            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                Settings.soundEnabled = true;
                if(inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight))
                {
                    //attack up is selected
                    //System.out.println("attack up");
                }
                if (inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight))
                {
                    //attack right is selected
                   // System.out.println("attack right");
                }
                if(inBoundaryCheck(event.x, event.y, posX,  posY + tileHeight, tileWidth, tileHeight))
                {
                    //attack down is selected
                    //System.out.println("attack down");
                }
                if(inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight))
                {
                    //attack left is selected
                    //System.out.println("attack left");
                }
            }
        }
    }

    private void updateEnemyFire()
    {
        int gumbyFive;
        if(enemyAttackCounter >= tcWorld.tcvsEnemy.size())
        {
            switchToMovePhase();
            return;
        }

        if(tcWorld.tcvsEnemy.get(enemyAttackCounter).enemiesInRange.size() > 0 && enemyAttackCounter < tcWorld.tcvsEnemy.size())
        {
            if(!tcWorld.tcvsEnemy.get(enemyAttackCounter).isDead)
            {
                gumbyFive = tcWorld.randInt(0, tcWorld.tcvsEnemy.get(enemyAttackCounter).enemiesInRange.size() - 1);
                enemyHasAttacked = true;
                TacticalCombatWorld.ShootingAt shotDest;
                int shotLoc = TacticalCombatWorld.randInt(1, 100);
                if (shotLoc > 25)
                {
                    shotDest = TacticalCombatWorld.ShootingAt.Interior;
                }
                else if (shotLoc > 5)
                {
                    shotDest = TacticalCombatWorld.ShootingAt.Exterior;
                }
                else
                {
                    shotDest = TacticalCombatWorld.ShootingAt.Tires;
                }
                executeCombat(tcWorld.tcvsEnemy.get(enemyAttackCounter), tcWorld.tcvsEnemy.get(enemyAttackCounter).enemiesInRange.get(gumbyFive), shotDest);
                prevState = PhaseStates.EnemyAttack;
                pState = PhaseStates.DisplayCasualties;
                if(shotDest == TacticalCombatWorld.ShootingAt.Tires)
                {
                    stateCombatReport = StateCombatReport.TireDeaths;
                }
                enemyAttackCounter++;
            }
        }
        enemyAttackCounter++;

        if(enemyAttackCounter == tcWorld.tcvsEnemy.size())
        {
            //pState = PhaseStates.Moving;
            //enemyAttackCounter = 0;
            switchToMovePhase();
        }

    }

    private void playGunSound()
    {
        int gunShots = TacticalCombatWorld.randInt(1, 3);
        for(int i = 0; i < gunShots; ++i)
        {
            if(Settings.soundEnabled)
            {
                Assets.gunShot.play(gunShots);
            }
        }
    }

    private void executeCombat (TacticalCombatVehicle attacker, TacticalCombatVehicle defender, TacticalCombatWorld.ShootingAt shotDest)
    {
        Vector start = new Vector();
        Vector goal = new Vector();
        start.x = attacker.xPos;
        start.y = attacker.yPos;
        goal.x = defender.xPos;
        goal.y = defender.yPos;
        int distanceAway = (int)Math.round(attacker.getDistanceFromGoal(start, goal));

        switch (shotDest)
        {
            case Interior:
                killList = tcWorld.shootRound(attacker.interior, defender.interior, defender.vehicle, distanceAway, false, 1, false);
                break;

            case Exterior:
                killList = tcWorld.shootRound(attacker.interior, defender.exterior, defender.vehicle, distanceAway, false, 3, false);
                break;

            case Tires:
                killList = tcWorld.shootRound(attacker.interior, defender.exterior, defender.vehicle, distanceAway, false, -1, true);
                break;
        }

        attacker.checkIfDead();
        defender.checkIfDead();
        mTiresLeft = defender.vehicle.tires;
        if(selectedVehicleEnemy != null)
        {
            if (selectedVehicleEnemy.isDead)
            {
                Assets.explosion.play(1);
            }
        }
    }

    private void drawSkip()
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 32;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, srcX, srcY, tileWidth, tileHeight);            //skip
    }

    private  void updateSkip(List<Input.TouchEvent> touchEvents)
    {
        //update the skip
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;

        int len = touchEvents.size();

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y,  g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, tileWidth, tileHeight))
                {
                    System.out.println("skip");
                    touchEvents.remove(i);
                    //switchToMovePhase();
                    pState = PhaseStates.EnemyAttack;
                    break;
                }
            }
        }

    }

    private void drawMoveAll()
    {
        if (!isButtonDisplayed && flashingButton == FlashingButton.FlashMoveAll)
        {
            return;
        }
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 52;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, srcX, srcY, tileWidth, tileHeight);            //skip
    }

    private void updateMoveAll(List<Input.TouchEvent> touchEvents)
    {
        //update the skip
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;

        int len = touchEvents.size();

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y,  g.getWidth() - tileWidth - 10, g.getHeight() - tileHeight - 10, tileWidth, tileHeight))
                {
                    System.out.println("move all");
                    isTouched = false;
                    isTouched2 = false;
                    isTouched3 = false;
                    touchEvents.remove(i);
                    isButtonFlashing = true;
                    flashingButton = FlashingButton.FlashMoveAll;
                    tcWorld.moveAllVehicles();
                    break;
                }
            }
        }

    }

    private void drawUIPhaseCrewTransfer(int posX, int posY)
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 30;
        int numColumns = 4;
        int srcX, srcY;

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY + tileHeight, srcX, srcY, tileWidth, tileHeight);            //transfer down

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //transfer up

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //skip
        /*
        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 0, 224, tileWidth, tileHeight);    //transfer crew down
        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 96, 224, tileWidth, tileHeight);    //transfer crew up
        */
    }

    private void updateCrewDeploymentUI(List<Input.TouchEvent> touchEvents, int posX, int posY)
    {
        //update the crew deployment
        int tileWidth = 128;
        int tileHeight = 128;

        int len = touchEvents.size();

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight))
                {
                    //transfer up image is selected
                    System.out.println("transfer up");
                }
                if(inBoundaryCheck(event.x, event.y, posX, posY + tileHeight, tileWidth, tileHeight))
                {
                    //transfer down image is selected
                    System.out.println("transfer down");
                }
                if(inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight))
                {
                    //skip image is selected
                    System.out.println("skip");
                }
            }
        }
    }

    private void drawPlayerReport()
    {
        //used to tell what crew each player vehicle has
        Graphics g = game.getGraphics();
        int fontSize = 44;
        int rectWidth = 1800;
        int rectHeight = 135;
        int xPos = (int)((g.getWidth() * 0.5) - (rectWidth * 0.55));
        int yPos = (int)(g.getHeight() - (rectHeight * 0.5));
        int line = 0;
        String text;

        g.drawRect(xPos, yPos, rectWidth, rectHeight, Color.BLACK);

        xPos += fontSize * 1.5;
        if(selectedVehicle != null)
        {
            line += 1;
            text = "Armsmasters: " + selectedVehicle.interior.armsmasters;
            yPos = yPos + (line * fontSize);
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);

            text = "Bodyguards: " + selectedVehicle.interior.bodyguards;
            xPos += 370;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);

            text = "Commandos: " + selectedVehicle.interior.commandos;
            xPos += 370;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);

            text = "Dragoons: " + selectedVehicle.interior.dragoons;
            xPos += 370;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);

            text = "Escorts: " + selectedVehicle.interior.escorts;
            xPos += 370;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
        }
    }

    private void drawCombatReport()
    {
        Graphics g = game.getGraphics();
        int fontSize = 72;
        int rectWidth = 768;
        int rectHeight = fontSize * 8;
        int xPos = (int)((g.getWidth() * 0.5) - (rectWidth * 0.5));
        int yPos = (int)((g.getHeight() * 0.5) - (rectHeight * 0.5));
        int line = 0;
        String text;

        g.drawRect(xPos, yPos, rectWidth, rectHeight, Color.BLACK);

        xPos += fontSize * 1.5;

        if (stateCombatReport == StateCombatReport.DefenderDeaths)
        {
            line += 5;
            yPos = line * fontSize;

            text = "Defenders Left";
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Armsmasters: " + killList.defender.armsmasters;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Bodyguards: " + killList.defender.bodyguards;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Commandos: " + killList.defender.commandos;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Dragoons: " + killList.defender.dragoons;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Escorts: " + killList.defender.escorts;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;
        }
        if (stateCombatReport == StateCombatReport.AttackerDeaths)
        {
            line += 5;
            yPos = line * fontSize;

            text = "Attackers Left";
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Armsmasters: " + killList.attacker.armsmasters;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Bodyguards: " + killList.attacker.bodyguards;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Commandos: " + killList.attacker.commandos;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Dragoons: " + killList.attacker.dragoons;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "Escorts: " + killList.attacker.escorts;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;
        }
        if (stateCombatReport == StateCombatReport.TireDeaths)
        {
            line += 5;
            yPos = line * fontSize;

            text = "Tires Left";
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;

            text = "" + mTiresLeft;
            yPos = line * fontSize;
            g.drawText(text, xPos, yPos, Color.WHITE, fontSize, Paint.Align.LEFT);
            ++line;
        }
    }

    private void updateCombatReport(List<Input.TouchEvent> touchEvents)
    {
        Graphics g = game.getGraphics();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if ((stateCombatReport == StateCombatReport.DefenderDeaths) || (stateCombatReport == StateCombatReport.TireDeaths))
                {
                    stateCombatReport = StateCombatReport.AttackerDeaths;
                    touchEvents.remove(i);
                    drawSpark = false;
                    enemyHasAttacked = false;
                    break;
                }
                if (stateCombatReport == StateCombatReport.AttackerDeaths)
                {
                    stateCombatReport = StateCombatReport.DefenderDeaths;
                    if(prevState == PhaseStates.Attack)
                    {
                        pState = PhaseStates.Attack;
                        drawSpark = false;
                        enemyHasAttacked = false;
                    }
                    if(prevState == PhaseStates.EnemyAttack)
                    {
                        pState = PhaseStates.EnemyAttack;
                        drawSpark = false;
                        enemyHasAttacked = false;
                    }
                    drawSpark = false;
                    enemyHasAttacked = false;
                    touchEvents.remove(i);
                    break;
                }
            }
        }
    }


    private void drawSelectAreaToAttack(int posX, int posY)
    {
        if (selectedVehicleEnemy.isDead)
        {
            return;
        }

        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 56;
        int numColumns = 4;
        int srcX, srcY;

        if (!isButtonDisplayed && flashingButton == FlashingButton.FlashAttackOutside)
        {
            return;
        }
        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX - tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //outside of vehicle

        if (!isButtonDisplayed && flashingButton == FlashingButton.FlashAttackInside)
        {
            return;
        }
        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX, posY - tileHeight, srcX, srcY, tileWidth, tileHeight);            //inside of vehicle

        if (!isButtonDisplayed && flashingButton == FlashingButton.FlashAttackTires)
        {
            return;
        }
        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //tires
    }

    private void updateSelectAreaToAttack(List<Input.TouchEvent> touchEvents, int posX, int posY)
    {
        if (selectedVehicleEnemy.isDead)
        {
            return;
        }

        int tileWidth = 128;
        int tileHeight = 128;

        int len = touchEvents.size();
        TacticalCombatVehicle vEnemy;

        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DRAGGED)
            {
                if (inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight)) {
                    //attack left
                    isTouched = true;
                    isTouched2 = false;
                    isTouched3 = false;
                    touchEvents.clear();
                    break;
                }
                if (inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight)) {
                    //attack up
                    isTouched2 = true;
                    isTouched = false;
                    isTouched3 = false;
                    touchEvents.clear();
                    break;
                }
                if (inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight)) {
                    //attack right
                    isTouched3 = true;
                    isTouched = false;
                    isTouched2 = false;
                    touchEvents.clear();
                    break;
                }
            }
            if (event.type == Input.TouchEvent.TOUCH_DOWN)
            {
                if (inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight)) {
                    //attack left
                    isTouched = true;
                    isTouched2 = false;
                    isTouched3 = false;
                    touchEvents.clear();
                    break;
                }
                if (inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight)) {
                    //attack up
                    isTouched2 = true;
                    isTouched = false;
                    isTouched3 = false;
                    touchEvents.clear();
                    break;
                }
                if (inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight)) {
                    //attack right
                    isTouched3 = true;
                    isTouched = false;
                    isTouched2 = false;
                    touchEvents.clear();
                    break;
                }
            }
            if (event.type == Input.TouchEvent.TOUCH_UP)
            {
                if (inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight)) {
                    //attack up is selected
                    System.out.println("selected outside of vehicle");
                    updateAttackAreaSelection = false;
                    isButtonFlashing = true;
                    isTouched = false;
                    flashingButton = FlashingButton.FlashAttackOutside;
                    playGunSound();
                    executeCombat(selectedVehicle, selectedVehicleEnemy, TacticalCombatWorld.ShootingAt.Exterior);
                    selectedVehicle.isAttacked = true;
                    prevState = PhaseStates.Attack;
                    pState = PhaseStates.DisplayCasualties;
                    drawSpark = true;
                    // *ALL* touch events need to be removed here to get rid of a dragged.
                    touchEvents.clear();
                    break;
                }
                if (inBoundaryCheck(event.x, event.y, posX, posY - tileHeight, tileWidth, tileHeight)) {
                    //attack right is selected
                    System.out.println("selected inside of vehicles");
                    flashingButton = FlashingButton.FlashAttackInside;
                    updateAttackAreaSelection = false;
                    isButtonFlashing = true;
                    isTouched2 = false;
                    playGunSound();
                    executeCombat(selectedVehicle, selectedVehicleEnemy, TacticalCombatWorld.ShootingAt.Interior);
                    selectedVehicle.isAttacked = true;
                    prevState = PhaseStates.Attack;
                    pState = PhaseStates.DisplayCasualties;
                    drawSpark = true;
                    // *ALL* touch events need to be removed here to get rid of a dragged.
                    touchEvents.clear();
                    break;
                }
                if (inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight)) {
                    //attack down is selected
                    System.out.println("selected tires");
                    updateAttackAreaSelection = false;
                    flashingButton = FlashingButton.FlashAttackTires;
                    isButtonFlashing = true;
                    isTouched3 = false;
                    playGunSound();
                    executeCombat(selectedVehicle, selectedVehicleEnemy, TacticalCombatWorld.ShootingAt.Tires);
                    selectedVehicle.isAttacked = true;
                    prevState = PhaseStates.Attack;
                    pState = PhaseStates.DisplayCasualties;
                    drawSpark = true;
                    // *ALL* touch events need to be removed here to get rid of a dragged.
                    touchEvents.clear();
                    break;
                }
            }
        }
    }

    private void updateButtonAnimation (float deltaTime)
    {
        if (isButtonFlashing)
        {
            timeButtonFlashTotal += deltaTime;
            if (timeButtonFlashTotal >= TIME_BUTTON_FLASH_TOTAL)
            {
                isButtonFlashing = false;
                isButtonDisplayed = true;
                timeButtonFlashTotal = 0.0f;
                timeButtonFlashOnOff = 0.0f;
            }
            else
            {
                timeButtonFlashOnOff += deltaTime;
                if (timeButtonFlashOnOff >= TIME_BUTTON_FLASH_ON_OFF)
                {
                    timeButtonFlashOnOff = 0.0f;
                    isButtonDisplayed = !isButtonDisplayed;
                }
            }
        }
    }

    private void drawDamage(int posX, int posY)
    {
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;

        g.drawPixmap(Assets.spark, posX, posY, 0, 0, tileWidth, tileHeight);            //outside of vehicle
    }

    @Override
	public void pause()
	{
		if(state == GameState.Running)
		{
			state = GameState.Paused;
		}
	}

	@Override
	public void resume()
	{

	}

	@Override
	public void dispose()
	{

	}
}


/*
Does this annoy you Andrew?
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
 */


/*
Nope

░░░░░▄▄▄▄▀▀▀▀▀▀▀▀▄▄▄▄▄▄░░░░░░░
░░░░░█░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░▀▀▄░░░░
░░░░█░░░▒▒▒▒▒▒░░░░░░░░▒▒▒░░█░░░
░░░█░░░░░░▄██▀▄▄░░░░░▄▄▄░░░░█░░
░▄▀▒▄▄▄▒░█▀▀▀▀▄▄█░░░██▄▄█░░░░█░
█░▒█▒▄░▀▄▄▄▀░░░░░░░░█░░░▒▒▒▒▒░█
█░▒█░█▀▄▄░░░░░█▀░░░░▀▄░░▄▀▀▀▄▒█
░█░▀▄░█▄░█▀▄▄░▀░▀▀░▄▄▀░░░░█░░█░
░░█░░░▀▄▀█▄▄░█▀▀▀▄▄▄▄▀▀█▀██░█░░
░░░█░░░░██░░▀█▄▄▄█▄▄█▄████░█░░░
░░░░█░░░░▀▀▄░█░░░█░█▀██████░█░░
░░░░░▀▄░░░░░▀▀▄▄▄█▄█▄█▄█▄▀░░█░░
░░░░░░░▀▄▄░▒▒▒▒░░░░░░░░░░▒░░░█░
░░░░░░░░░░▀▀▄▄░▒▒▒▒▒▒▒▒▒▒░░░░█░
░░░░░░░░░░░░░░▀▄▄▄▄▄░░░░░░░░█░░
 */