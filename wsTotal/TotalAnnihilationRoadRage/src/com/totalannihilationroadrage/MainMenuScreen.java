package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Input.TouchEvent;
import com.framework.Screen;

public class MainMenuScreen extends Screen {
    public MainMenuScreen(Game game)
    {
        super(game);
        Assets.music.play();
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();       
        
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(inBounds(event, 861, 466, 393, 112) ) {
                    startTacticalCombat();
                    return;
                }

                if(inBounds(event, 861, 466 + 112, 393, 112) ) {
                    game.setScreen(new HowToScreen(game));
                   return;
                }
                if(inBounds(event, 861, 466 + 224, 393, 112) )
                {
                    game.setScreen((new Credits(game)));
                    return;
                }
            }
        }
    }

    private void startTacticalCombat ()
    {
        TacticalCombatWorld tcWorld;
        List< TacticalCombatVehicle > tcvPlayer = new ArrayList< TacticalCombatVehicle >();
        List< TacticalCombatVehicle > tcvEnemy = new ArrayList< TacticalCombatVehicle >();

        for (int i = 0; i < 10; ++i)
        {
            tcvPlayer.add(setupVehicle(true));
            tcvEnemy.add(setupVehicle(false));
        }

        tcWorld = new TacticalCombatWorld(Assets.tmHighway, tcvPlayer, tcvEnemy);
        game.setScreen(new TacticalCombatScreen(game, tcWorld, tcWorld.tmBattleGround));
    }

    private TacticalCombatVehicle setupVehicle (boolean isPlayer)
    {
        Random random = new Random();
        GangMembers aGangE = new GangMembers();
        aGangE.armsmasters = random.nextInt(10) + 1;
        aGangE.bodyguards = random.nextInt(10) + 1;
        aGangE.commandos = random.nextInt(10) + 1;
        aGangE.dragoons = random.nextInt(10) + 1;
        aGangE.escorts = random.nextInt(10) + 1;
        GangMembers aGangI = new GangMembers();
        aGangI.armsmasters = random.nextInt(10) + 1;
        aGangI.bodyguards = random.nextInt(10) + 1;
        aGangI.commandos = random.nextInt(10) + 1;
        aGangI.dragoons = random.nextInt(10) + 1;
        aGangI.escorts = random.nextInt(10) + 1;
        VehicleStatsCurrent aVehicle = new VehicleStatsCurrent(Assets.vehicleStats.vehicles.get(random.nextInt(19)));
        TacticalCombatVehicle aTacticalVehicle = new TacticalCombatVehicle(aVehicle, aGangE, aGangI, isPlayer);

        return aTacticalVehicle;
    }
    
    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        if(event.x > x && event.x < x + width - 1 && 
           event.y > y && event.y < y + height - 1) 
            return true;
        else
            return false;
    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        
        g.drawPixmap(Assets.background, 0, 0);
        g.drawPixmap(Assets.mainMenu, 780, 466);
    }

    public void pause()
    {
        Settings.save(game.getFileIO());
        Assets.music.pause();
    }

    public void resume()
    {
        Assets.music.play();
    }

    public void dispose()
    {
        Assets.music.stop();
    }
}

