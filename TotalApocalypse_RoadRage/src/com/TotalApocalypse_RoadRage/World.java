package com.TotalApocalypse_RoadRage;

import java.util.Random;

import com.totalapocalypse_roadrage.R.string;

public class World 
{
	//public tileSheet;
	public int x;
	public int y;
	public int width;
	public int height;
		
	public enum GameObjectData
	{
		PLAYER,
		x,
		y,
	}
	
	public enum TileData
	{
		SWAMP,
		FOREST,
	}	
	
		
	public World(tileSheet, x, y, width, height)
	{
		this.tileSheet = tileSheet;
        this.left = x;
        this.top = y;
        this.right = x + width;
        this.bottom = y + height;
        this.tilesWide = width / this.tileSize;
        this.tilesHigh = height / this.tileSize;
        this.sourceTilesWide = tileSheet.width / this.tileSize;
        this.tiles = new Array();
        this.foregroundTiles = new Array();
        this.gameObjects = new Array();

        this.gravity = Object.create(VectorClass);
        this.gravity.x = 0;
        this.gravity.y = this.GRAVITY_Y;

        this.loadLevelData(loadXMLDoc("Assets/Levels/level1.tmx"));
        

        var test = new Array();

        var node1 = Object.create(Node);
        node1.g = 1;
        node1.f = 1;
        test.push(node1);
     
        var node2 = Object.create(Node);
        node2.g = 6;
        node2.f = 2;
        test.push(node2);

        var node3 = Object.create(Node);
        node3.g = 1;
        node3.f = 3;
        test.push(node3);

        //test.sort(function(a,b){return b.f - a.f});

        for(var i = 0; i < test.length; ++i)
        {
            console.log(test[i].g);
        }
	}
	
	public void isTileGameObject(gameObject)
	{
		if (gameObject == GameObjectData.PLAYER)
			return true;
	}
	
	public void loadLevelData(levelFile)
    {
        var xmlDoc;
        var parser;

        if (window.DOMParser)
        {
            parser = new DOMParser();
            xmlDoc = parser.parseFromString(levelFile, "text/xml");
        }
        else // Internet Explorer
        {
            xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = false;
            xmlDoc.loadXML(levelFile);
        }
        var mapNode = xmlDoc.getElementsByTagName("map")[0];
        this.tilesWide = parseInt(mapNode.attributes.getNamedItem("width").value);
        this.tilesHigh = parseInt(mapNode.attributes.getNamedItem("height").value);
        this.tileSize = parseInt(mapNode.attributes.getNamedItem("tilewidth").value);

        var backgroundTilesNode;
        var foregroundTilesNode;
        var objectTilesNode;

        for (var i = 0; i < xmlDoc.getElementsByTagName("layer").length; ++i)
        {
            var currLayer = xmlDoc.getElementsByTagName("layer")[i];
            if (currLayer.attributes.getNamedItem("name").value == this.foregroundLayerName)
            {
                foregroundTilesNode = currLayer.childNodes[1];
            }

            else if (currLayer.attributes.getNamedItem("name").value == this.backgroundLayerName)
            {
                backgroundTilesNode = currLayer.childNodes[1];
            }

            else if (currLayer.attributes.getNamedItem("name").value == this.objectsLayerName)
            {
                objectTilesNode = currLayer.childNodes[1];
            }
        }

        var index = 0;
        for (var row = 0; row < this.tilesHigh; ++row)
        {
            this.tiles.push(new Array());
            this.foregroundTiles.push(new Array());
            for (var col = 0; col < this.tilesWide; ++col)
            {

                while(backgroundTilesNode.childNodes[index].nodeName != "tile")
                {
                    index++;
                }

                var newTile = Object.create(TileData);
                newTile.type = parseInt(backgroundTilesNode.childNodes[index].attributes.getNamedItem("gid").value) - 1;
                newTile.friction = this.getTileFriction(newTile.type);
                newTile.acceleration = this.getTileAcceleration(newTile.type);
                this.tiles[row].push(newTile);
                

                newTile = Object.create(TileData);
                newTile.type = parseInt(foregroundTilesNode.childNodes[index].attributes.getNamedItem("gid").value) - 1; 
                newTile.friction = this.getTileFriction(newTile.type);
                newTile.acceleration = this.getTileAcceleration(newTile.type);
                this.foregroundTiles[row].push(newTile);

                var object = parseInt(objectTilesNode.childNodes[index].attributes.getNamedItem("gid").value) - 1;
                index++;
                if (this.isTileGameObject(object))
                {
                    var gameObjectData = Object.create(GameObjectData);
                    gameObjectData.type = object;
                    gameObjectData.x = this.left + col * this.tileSize;
                    gameObjectData.y = this.top + row * this.tileSize;
                    this.gameObjects.push(gameObjectData);
                }

            }
        }

        this.collisionRects = new Array();
        for (var row = 0; row < this.tilesHigh; ++row)
        {
            for (var col = 0; col < this.tilesWide; ++col)
            {

                if (this.isTileCollidable(this.tiles[row][col].type))
                {
                    //add collision rect
                    var collisionRect = Object.create(RectClass);
                    collisionRect.init(this.left + col * this.tileSize,
                                         this.top + row * this.tileSize,
                                         this.tileSize,
                                         this.tileSize, 0);
                    this.collisionRects.push(collisionRect);

                }

            }
        }

        var goal = Object.create(Node);
        goal.init(4,14,0,0,undefined);
        var start = Object.create(Node);
        start.init(4,1,0,0, undefined);
        var passableTiles = new Array();
        passableTiles.push(6);
        passableTiles.push(7);
        passableTiles.push(8);
        this.debugDraw = findPath(this.tiles, goal, start, passableTiles);
    }
	  
}    

