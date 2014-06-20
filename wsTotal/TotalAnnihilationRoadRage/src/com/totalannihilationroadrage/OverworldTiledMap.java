package com.totalannihilationroadrage;

/**
 * Created by Bryan-Alien on 19/06/2014.
 */
public class OverworldTiledMap extends TiledMap
{
    public boolean isPassable (int row, int col)
    {
        int layerOneVal = layers.get(0).getTile(row, col);
        int layerTwoVal = layers.get(1).getTile(row, col);

        return !(isBlockTile(layerOneVal) || isBlockTile(layerTwoVal));
    }

    private boolean isBlockTile (int tileVal)
    {
        switch (tileVal)
        {
            case 1:
            case 2:
            case 3:
            case 19:
            case 22:
                return true;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return false;
        }

        return false;
    }
}
