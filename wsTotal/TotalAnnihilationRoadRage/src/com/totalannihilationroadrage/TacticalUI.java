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
    int accelerate;

    public void updateCrewDeploymentUI(List<TouchEvent> touchEvents)
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
                if (event.x < 0 && event.y > 0)
                {
                    //transfer up image is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //transfer down image is selected
                }
            }
        }
    }

    private void drawUIPhaseCrewTransfer(int posX, int posY)
    {
        Graphics g = game.getGraphics();
        Assets.roadTileSheet = g.newPixmap("roadTileSheet.png", Graphics.PixmapFormat.ARGB8888);

        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 0, 224, 32, 32);    //transfer crew down
        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 96, 224, 32, 32);    //transfer crew up
    }

    public void updateFire(List<TouchEvent> touchEvents)
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
                if (event.x < 0 && event.y > 0)
                {
                    //attack is selected
                }
            }
        }
    }

    private void drawUIPhaseFire(int posX, int posY)
    {
        Graphics g = game.getGraphics();
        Assets.roadTileSheet = g.newPixmap("roadTileSheet.png", Graphics.PixmapFormat.ARGB8888);

        g.drawPixmap(Assets.roadTileSheet,posX, posY - 32, 32, 160, 32, 32);    //up arrow
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 64, 160, 32, 32);    //right arrow
        g.drawPixmap(Assets.roadTileSheet,posX, posY + 32, 96, 128, 32, 32);   //down arrow
        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 0, 160, 32, 32);    //left arrow
    }

    public void updateMove(List<TouchEvent> touchEvents)
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
                if (event.x < 0 && event.y > 0)
                {
                    //Right arrow is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Down arrow is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Left arrow is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Top arrow is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Skip is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //cannot move is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Forward is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Left angle is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Left turn is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Right angle is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //Right turn is selected
                }
                if (event.x < 0 && event.y > 0)
                {
                    //speed up is selected
                }
            }
        }
    }

    private void drawUIPhaseMovement (int posX, int posY)
    {
        Graphics g = game.getGraphics();
        Assets.roadTileSheet = g.newPixmap("roadTileSheet.png", Graphics.PixmapFormat.ARGB8888);

        g.drawPixmap(Assets.roadTileSheet,posX - 32, posY, 32, 224, 32, 32);            //break
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY, 96, 192, 32, 32);            //accelerate
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY - 32, 64, 192, 32, 32);       //left turn
        g.drawPixmap(Assets.roadTileSheet,posX + 32, posY + 32, 32, 192, 32, 32);       //right turn
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY, 0, 256, 32, 32);            //move straight
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY + 32, 0, 192, 32, 32);       // move right
        g.drawPixmap(Assets.roadTileSheet,posX + 64, posY - 32, 96, 160, 32, 32);      //move left
    }

    TacticalState tacticalState = TacticalState.MovementState;


    public void updateTacticalUI(float deltaTime, List<TouchEvent> touchEvents)
    {
        if(tacticalState == TacticalState.MovementState)
        {
            updateMove(touchEvents);

        }
        if(tacticalState == TacticalState.CrewState)
        {
            updateCrewDeploymentUI(touchEvents);
        }
        if(tacticalState == TacticalState.FireState)
        {
            updateFire(touchEvents);
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


