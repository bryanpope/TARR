package com.totalannihilationroadrage;

import com.framework.Game;
import com.framework.Graphics;
import com.framework.Screen;
import com.framework.Graphics.PixmapFormat;

public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        Assets.background = g.newPixmap("background.png", PixmapFormat.RGB565);
        Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
        Assets.mainMenu = g.newPixmap("buttons2.png", PixmapFormat.ARGB4444);
        Assets.mainMenu = g.newPixmap("mainmenu.png", PixmapFormat.ARGB4444);
        Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.ARGB4444);
        Assets.help1 = g.newPixmap("help1.png", PixmapFormat.ARGB4444);
        Assets.help2 = g.newPixmap("help2.png", PixmapFormat.ARGB4444);
        Assets.help3 = g.newPixmap("help3.png", PixmapFormat.ARGB4444);
        Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);
        Assets.ready = g.newPixmap("ready.png", PixmapFormat.ARGB4444);
        Assets.pause = g.newPixmap("pausemenu.png", PixmapFormat.ARGB4444);
        Assets.gameOver = g.newPixmap("gameover.png", PixmapFormat.ARGB4444);
        Assets.headUp = g.newPixmap("headup.png", PixmapFormat.ARGB4444);
        Assets.headLeft = g.newPixmap("headleft.png", PixmapFormat.ARGB4444);
        Assets.headDown = g.newPixmap("headdown.png", PixmapFormat.ARGB4444);
        Assets.headRight = g.newPixmap("headright.png", PixmapFormat.ARGB4444);
        Assets.tail = g.newPixmap("tail.png", PixmapFormat.ARGB4444);
        Assets.stain1 = g.newPixmap("stain1.png", PixmapFormat.ARGB4444);
        Assets.stain2 = g.newPixmap("stain2.png", PixmapFormat.ARGB4444);
        Assets.stain3 = g.newPixmap("stain3.png", PixmapFormat.ARGB4444);
        Assets.click = game.getAudio().newSound("click.ogg");
        Assets.eat = game.getAudio().newSound("eat.ogg");
        Assets.bitten = game.getAudio().newSound("bitten.ogg");
        Settings.load(game.getFileIO());

        Assets.roadTileSheet = g.newPixmap("roadTileSheet.png", PixmapFormat.ARGB8888);

        Assets.tmOverWorld = new OverworldTiledMap();
        game.getTMXParse().setHandler(Assets.tmOverWorld);
        game.getTMXParse().load("overWorldMap.tmx");
        Assets.pmOverWorld = g.newPixmap(Assets.tmOverWorld.image.source, PixmapFormat.ARGB8888);
        Assets.tmOverWorld.image.pmImage = Assets.pmOverWorld;

        int test = Assets.tmOverWorld.layers.get(0).getTile(5, 5);

        Assets.tmHighway = new TacticalCombatTiledMap();
        game.getTMXParse().setHandler(Assets.tmHighway);
        game.getTMXParse().load("roadMap.tmx");
        Assets.pmHighway = g.newPixmap(Assets.tmHighway.image.source, PixmapFormat.ARGB8888);
        Assets.tmHighway.image.pmImage = Assets.pmHighway;

        Assets.vehicleStats = new VehicleStatsBaseAll();
        game.getTMXParse().setHandler(Assets.vehicleStats);
        game.getTMXParse().load("vehicleStats.xml");
        Assets.vehicleStats.tileSheetVehicles = Assets.roadTileSheet;

        game.setScreen(new MainMenuScreen(game));
    }
    
    public void present(float deltaTime) {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }
}
