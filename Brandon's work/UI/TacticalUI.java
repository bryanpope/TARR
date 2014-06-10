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
	
	public void drawCrewDeployment()
	{
		//draw images related to crew deployment
        Graphics g = game.getGraphics();
		//							srcx, srcy, x, y, width, height
        g.drawPixmap(Assets.transferUp, 40, 240, 0, 0, 20, 20);
		g.drawPixmap(Assets.transferDown, 0, 260, 0, 0, 20, 20);
		g.drawPixmap(Assets.skip, 40, 260, 0, 0, 20, 20);
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
	
	public void drawFire()
	{
		//draw images related to fire 
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
	
	public void drawMove()
	{
		//draw images related to moving functions
		Graphics g = game.getGraphics();
		//							srcx, srcy, x, y, width, height
        g.drawPixmap(Assets.upArrow, 0, 200, 0, 0, 20, 20);
		g.drawPixmap(Assets.downArrow, 20, 180, 0, 0, 20, 20);
		g.drawPixmap(Assets.leftArrow, 40, 180, 0, 0, 20, 20);
		g.drawPixmap(Assets.rightArrow, 20, 200, 0, 0, 20, 20);
		
		g.drawPixmap(Assets.skip, 40, 260, 0, 0, 20, 20);
		
		g.drawPixmap(Assets.cannotMove, 20, 260, 0, 0, 20, 20);
		g.drawPixmap(Assets.forward, 20, 240, 0, 0, 20, 20);
		g.drawPixmap(Assets.leftAngle, 40, 220, 0, 0, 20, 20);
		g.drawPixmap(Assets.leftTurn, 40, 200, 0, 0, 20, 20);
		g.drawPixmap(Assets.rightAngle, 20, 220, 0, 0, 20, 20);
		g.drawPixmap(Assets.rightTurn, 0, 220, 0, 0, 20, 20);
		g.drawPixmap(Assets.speedUp, 0, 240, 0, 0, 20, 20);
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
    
    public void drawTacticalUI()
    {
        if(tacticalState == TacticalState.MovementState) 
        {
        	drawMove();
        }
        if(tacticalState == TacticalState.CrewState)
        {
        	drawCrewDeployment();
        }
        if(tacticalState == TacticalState.FireState)
        {
        	drawFire();
        }
    }
}

