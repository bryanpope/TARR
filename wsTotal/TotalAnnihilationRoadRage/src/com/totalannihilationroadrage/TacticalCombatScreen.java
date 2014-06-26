package com.totalannihilationroadrage;
import com.framework.impl.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input;
import com.framework.Screen;
import com.framework.impl.AndroidPixmap;
import com.totalannihilationroadrage.Pathfinding;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TacticalCombatScreen extends Screen
{
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
        CrewTransfer
    }

	GameState state = GameState.Running;
    PhaseStates pState = PhaseStates.notActive;
    Direction dir = Direction.EAST;
	TacticalCombatWorld tcWorld;
    TiledMap tMap;
    //Pathfinding pathfinding = new Pathfinding();
    List<Node> pathList;

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

	public TacticalCombatScreen(Game game, TacticalCombatWorld tacticalCombatWorld, TiledMap tacticalMap)
	{
		super(game);
		tcWorld = tacticalCombatWorld;
        tMap = tacticalMap;
        numRows = (game.getGraphics().getHeight() / tcWorld.tmBattleGround.tileHeight) + 1;
        numCols = (game.getGraphics().getWidth() / tcWorld.tmBattleGround.tileWidth) + 1;
	}

    public boolean attackPhase(List <TacticalCombatVehicle> player, List<TacticalCombatVehicle> enemy)
    {
        Node selectedPlayer = new Node(selectedVehicle.yPos, selectedVehicle.xPos, 0, 0, null);

        if(selectedVehicle.isMoved )
        {
            return true;
        }
        return false;
    }

	public void update(float deltaTime)
	{
        Graphics g = game.getGraphics();
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int maxCameraX = (tcWorld.tmBattleGround.width * tcWorld.tmBattleGround.tileWidth) - g.getWidth();
        int maxCameraY = (tcWorld.tmBattleGround.height * tcWorld.tmBattleGround.tileHeight) - g.getHeight();
        float x, y;
        if((selectedVehicle != null) && (pState == PhaseStates.Moving))
        {
            updateMove(touchEvents,(selectedVehicle.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicle.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, selectedVehicle.facing);
        }
        int len = touchEvents.size();
        for(int i = 0; i < len; i++)
        {
            Input.TouchEvent event = touchEvents.get(i);
            x = event.x;
            y = event.y;
            if(event.type == Input.TouchEvent.TOUCH_DOWN)
            {
                previousTouchX = x;
                previousTouchY = y;
                pointerId = event.pointer;
            }

            if(event.type == Input.TouchEvent.TOUCH_UP)
            {
                if (!event.wasDragged)
                {
                    selectedVehicle = isVehicleTouched(event);
                }
            }

            if(event.type == Input.TouchEvent.TOUCH_DRAGGED)
            {
                if (event.pointer != pointerId)
                {
                    continue;
                }

                float dispX = x - previousTouchX;
                float dispY = y - previousTouchY;

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

                /*cameraTopRow += cameraOffsetY / tcWorld.tmBattleGround.tileHeight;
                cameraOffsetY %= tcWorld.tmBattleGround.tileHeight;

                cameraLeftCol += cameraOffsetX / tcWorld.tmBattleGround.tileWidth;
                cameraOffsetX %= tcWorld.tmBattleGround.tileWidth;*/
            }
            previousTouchX = x;
            previousTouchY = y;
        }
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
            drawUIPhaseMovement((selectedVehicle.xPos * tcWorld.tmBattleGround.tileWidth) - cameraX, (selectedVehicle.yPos * tcWorld.tmBattleGround.tileHeight) - cameraY, selectedVehicle.facing);
            pState = PhaseStates.Moving;
        }

        /*for(int i = 0; i < tcWorld.tcvsEnemy.size(); ++i)
        {
            tcWorld.generatePath(tcWorld.tcvsEnemy.get(i).target);
        }*/
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

        /*
            The following is debug code to test Pathfinding.
            It will draw red squares from the start node to the end node.
        */
        /*while(node != null)
        {
            g.drawRect(node.col * tMap.tileset.tileWidth, node.row * tMap.tileset.tileHeight, tMap.tileset.tileWidth, tMap.tileset.tileHeight, Color.RED);
            //System.out.println("Node Row " + node.col + ", Node Col " + node.row);
            node = node.parentNode;
        }*/
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

		for (int i = 0; i < vehicles.size(); ++i)
		{
			destX = (vehicles.get(i).xPos * tMap.tileset.tileWidth) - cameraX;
			destY = (vehicles.get(i).yPos * tMap.tileset.tileHeight) - cameraY;
			int t_element = vehicles.get(i).vehicle.statsBase.type.ordinal() + Assets.vehicleStats.INDEX_START_CAR_TILES;
			srcY = (t_element / tileSheetCol) * tMap.tileset.tileWidth;
			srcX = (t_element % tileSheetCol) * tMap.tileset.tileHeight;
            g.drawRect(destX, destY, tMap.tileset.tileWidth, tMap.tileset.tileHeight, isEnemy ? Color.RED : vehicles.get(i).isMoved ?  Color.rgb(255,250,130) : Color.YELLOW);
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
        Graphics g = game.getGraphics();
        int tileWidth = 128;
        int tileHeight = 128;
        int index = 23;
        int numColumns = 4;
        int srcX, srcY;

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
        if(selectedVehicle.isAccelerated)
        {
            //g.drawPixmap(Assets.roadTileSheet, posX + tileWidth, posY, srcX, srcY, tileWidth, tileHeight);            //accelerate
            drawBmap(cUI, ((AndroidPixmap)Assets.roadTileSheet).bitmap, tileWidth * 2, tileHeight, srcX, srcY, tileWidth, tileHeight);
        }

        srcX = (index % numColumns) * tileHeight;
        srcY = (index++ / numColumns) * tileWidth;
        if(selectedVehicle.isBraked)
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
            if(inBoundaryCheck(event.x, event.y, x, y, tcWorld.tmBattleGround.tileWidth, tcWorld.tmBattleGround.tileHeight))
            {
                return tcWorld.tcvsPlayer.get(j);
            }
        }
        return null;
    }

    public void updateMove(List<Input.TouchEvent> touchEvents, int posX, int posY, Direction facing)
    {
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
                        //selectedVehicle.isStraight = true;
                        //selectedVehicle.xPos += 1;
                        selectedVehicle.move();
                        touchEvents.remove(i);
                        break;
                    }
                }
                /*if(selectedVehicle.isLeft)
                {
                    if (inBoundaryCheck(eX, eY, posX + (tileWidth * 2), posY - tileHeight, tileWidth, tileHeight)) {
                        //Move left
                        Vector vectorDirection = Direction.getDirectionVector(selectedVehicle.facing);
                        System.out.println("moved left " + selectedVehicle.xPos + " " + " " + selectedVehicle.yPos + selectedVehicle.facing + " " + vectorDirection.x + " " + vectorDirection.y);
                        selectedVehicle.xPos = selectedVehicle.xPos + vectorDirection.x;
                        selectedVehicle.yPos = selectedVehicle.yPos + vectorDirection.y;
                        selectedVehicle.isMoved = true;
                        touchEvents.remove(i);
                        break;
                    }
                }
                if(selectedVehicle.isRight)
                {
                    if (inBoundaryCheck(eX, eY, posX + (tileWidth * 2), posY + tileHeight, tileWidth, tileHeight)) {
                        //Move right
                        Vector vectorDirection = Direction.getDirectionVector(selectedVehicle.facing);
                        System.out.println("moved right " + selectedVehicle.xPos + " " + selectedVehicle.yPos + " " + vectorDirection.x + " " + vectorDirection.y);
                        selectedVehicle.xPos = selectedVehicle.xPos + vectorDirection.x;
                        selectedVehicle.yPos = selectedVehicle.yPos + vectorDirection.y;
                        selectedVehicle.isMoved = true;
                        touchEvents.remove(i);
                        break;
                    }
                }*/

                if(selectedVehicle.allowTurning())
                {
                    if (inBoundaryCheck(eX, eY, posX + tileWidth, posY - tileHeight, tileWidth, tileHeight))
                    {
                        //Left turn
                        dir = Direction.turnLeft(dir);
                        selectedVehicle.turnLeft();
                        System.out.println("left turn " + dir);
                        //selectedVehicle.isTurnedRight = false;
                        //selectedVehicle.isTurnedLeft = true;
                        //selectedVehicle.isStraight = false;
                        //selectedVehicle.isLeft = true;
                        //selectedVehicle.isRight = false;
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
                        //selectedVehicle.isTurnedRight = true;
                        //selectedVehicle.isTurnedLeft = false;
                        //selectedVehicle.isStraight = false;
                        //selectedVehicle.isLeft = false;
                        //selectedVehicle.isRight = true;
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

    private void drawUIPhaseFire(int posX, int posY)
    {
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

        /*
        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 32, 160, tileWidth, tileHeight);    //up arrow
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 64, 160, tileWidth, tileHeight);    //right arrow
        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 96, 128, tileWidth, tileHeight);   //down arrow
        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 0, 160, tileWidth, tileHeight);    //left arrow
        */
    }

    private void updateFire(List<Input.TouchEvent> touchEvents, int posX, int posY)
    {
        //update the fire attack
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
                    //attack up is selected
                    System.out.println("attack up");
                }
                if (inBoundaryCheck(event.x, event.y, posX + tileWidth, posY, tileWidth, tileHeight))
                {
                    //attack right is selected
                    System.out.println("attack right");
                }
                if(inBoundaryCheck(event.x, event.y, posX,  posY + tileHeight, tileWidth, tileHeight))
                {
                    //attack down is selected
                    System.out.println("attack down");
                }
                if(inBoundaryCheck(event.x, event.y, posX - tileWidth, posY, tileWidth, tileHeight))
                {
                    //attack left is selected
                    System.out.println("attack left");
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
