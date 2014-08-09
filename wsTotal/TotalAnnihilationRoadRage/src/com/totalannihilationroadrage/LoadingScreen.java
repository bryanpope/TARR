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

        Assets.background = g.newPixmap("Titlescreen1920x1080.jpg", PixmapFormat.ARGB8888);
        Assets.mainMenu = g.newPixmap("buttons2.png", PixmapFormat.ARGB4444);
        Assets.gameOver = g.newPixmap("gameover.png", PixmapFormat.ARGB4444);
        Assets.victory = g.newPixmap("victory.png", PixmapFormat.ARGB4444);
        Assets.menu = g.newPixmap("mainMenu.png", PixmapFormat.ARGB4444);
        Assets.overWorldUI = g.newPixmap("OverworldUI.png", PixmapFormat.ARGB8888);
        Assets.howToScreen = g.newPixmap("screen.jpg", PixmapFormat.ARGB8888);
        Assets.roadTileSheet = g.newPixmap("roadTileSheet.png", PixmapFormat.ARGB8888);
        Assets.spark = g.newPixmap("spark.png", PixmapFormat.ARGB8888);
        Assets.gunShot = game.getAudio().newSound("gunShot.wav");
        Assets.explosion = game.getAudio().newSound("explosion.wav");
        Assets.music = game.getAudio().newMusic("Pentakill - The Hex Core.mp3");
        Settings.load(game.getFileIO());



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
