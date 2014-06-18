package com.totalannihilationroadrage;

import java.util.List;

import android.graphics.Color;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input.TouchEvent;
import com.framework.Pixmap;
import com.framework.Screen;
import com.framework.Input.TouchEvent;

public class TacticalUI
{
    enum TacticalState
    {
        MovementState,
        CrewState,
        FireState
    }
    Game game;
    int attackDamage;
    int attackUp;
    int attackRight;
    int attackDown;
    int attackLeft;

    int transferCrew;
    int skip;
    int crewMembers;
    int numSpaces;
    int takeOver;
    boolean isDead;
    boolean isFriendly;

    int movesRemaining;
    int brake;
    int turnLeft;
    int moveLeft;
    int turnRight;
    int moveRight;
    int move;
    int accelerate = 5;

    int tileWidth = 32;
    int tileHeight = 32;

    public void updateCrewDeploymentUI(List<TouchEvent> touchEvents, int posX, int posY)
    {
        //update the crew deployment
        int len = touchEvents.size();
        for(int i = 0; i < len; i++)
        {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y, posX, posY - 32, tileWidth, tileHeight))
                {
                    //transfer up image is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX, posY + 32, tileWidth, tileHeight))
                {
                    //transfer down image is selected
                }
            }
        }
    }

    private void drawUIPhaseCrewTransfer(int posX, int posY)
    {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 0, 224, tileWidth, tileHeight);    //transfer crew down
        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 96, 224, tileWidth, tileHeight);    //transfer crew up
    }

    public void updateFire(List<TouchEvent> touchEvents, int posX, int posY)
    {
        //update the fire attack
        int len = touchEvents.size();
        for(int i = 0; i < len; i++)
        {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y, posX, posY - 32, tileWidth, tileHeight))
                {
                    //attack up is selected
                }
                if (inBoundaryCheck(event.x, event.y, posX + 32, posY, tileWidth, tileHeight))
                {
                    //attack right is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX, posY + 32, tileWidth, tileHeight))
                {
                    //attack down is selected
                }
                if(inBoundaryCheck(event.x, event.y, posX - 32, posY, tileWidth, tileHeight))
                {
                    //attack left is selected
                }
            }
        }
    }

    private void drawUIPhaseFire(int posX, int posY)
    {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 32, 160, tileWidth, tileHeight);    //up arrow
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 64, 160, tileWidth, tileHeight);    //right arrow
        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 96, 128, tileWidth, tileHeight);   //down arrow
        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 0, 160, tileWidth, tileHeight);    //left arrow
    }

    public boolean inBoundaryCheck(int touchXPos, int touchYPos, int boxX, int boxY, int boxWidth, int boxHeight)
    {
        //logic
        if(touchXPos >= boxX + boxWidth && touchXPos <= boxX + boxWidth && touchYPos >= boxY + boxHeight && touchYPos <= boxY + boxHeight)
        {
            return true;
        }
        else
            return false;

    }

    public void updateMove(List<TouchEvent> touchEvents, int posX, int posY)
    {
        //update the moving
        int len = touchEvents.size();
        for(int i = 0; i < len; i++)
        {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_DOWN)
            {


            }
            if(event.type == TouchEvent.TOUCH_UP)
            {
                if(inBoundaryCheck(event.x, event.y, posX - 32, posY, tileWidth, tileHeight))
                {
                    //break
                    accelerate -= 5;
                }
                if(inBoundaryCheck(event.x, event.y, posX + 32, posY, tileWidth, tileHeight))
                {
                    //accelerate
                    accelerate += 5;
                }
                if(inBoundaryCheck(event.x, event.y, posX + 32, posY - 32, tileWidth, tileHeight))
                {
                    //Left turn
                }
                if(inBoundaryCheck(event.x, event.y, posX + 32, posY + 32, tileWidth, tileHeight))
                {
                    //right turn
                }
                if(inBoundaryCheck(event.x, event.y, posX + 64, posY, tileWidth, tileHeight))
                {
                    //Move straight
                    posX += 32;
                }
                if(inBoundaryCheck(event.x, event.y, posX + 64, posY + 32, tileWidth, tileHeight))
                {
                    //Move right
                    posX += 32;
                    posY += 32;
                }
                if(inBoundaryCheck(event.x, event.y, posX + 64, posY - 32, tileWidth, tileHeight))
                {
                    //Move left
                    posX += 32;
                    posY -= 32;
                }

            }
        }
    }

    private void drawUIPhaseMovement (int posX, int posY)
    {
        Graphics g = game.getGraphics();


        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 32, 224, tileWidth, tileHeight);            //break
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 96, 192, tileWidth, tileHeight);            //accelerate
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY - 32, 64, 192, tileWidth, tileHeight);       //left turn
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY + 32, 32, 192, tileWidth, tileHeight);       //right turn
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY, 0, 256, tileWidth, tileHeight);            //move straight
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY + 32, 0, 192, tileWidth, tileHeight);       // move right
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY - 32, 96, 160, tileWidth, tileHeight);      //move left
    }

    TacticalState tacticalState = TacticalState.MovementState;


    public void updateTacticalUI(float deltaTime, List<TouchEvent> touchEvents, int posX, int posY)
    {
        if(tacticalState == TacticalState.MovementState)
        {
            updateMove(touchEvents, posX, posY);
        }
        if(tacticalState == TacticalState.CrewState)
        {
            updateCrewDeploymentUI(touchEvents, posX, posY);
        }
        if(tacticalState == TacticalState.FireState)
        {
            updateFire(touchEvents, posX, posY);
        }
    }

    public void drawTacticalUI(int posX, int posY)
    {
        if(tacticalState == TacticalState.MovementState)
        {
            drawUIPhaseMovement(posX, posY);
        }
        if(tacticalState == TacticalState.CrewState)
        {
            drawUIPhaseCrewTransfer(posX, posY);
        }
        if(tacticalState == TacticalState.FireState)
        {
            drawUIPhaseFire(posX, posY);
        }
    }
}


