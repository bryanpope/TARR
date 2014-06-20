package com.totalannihilationroadrage;

/**
 * Created by Bryan-Alien on 19/06/2014.
 */
public class TacticalCombatTiledMap extends TiledMap
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
            case 15:
                return true;
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
                return false;
        }

        return false;
    }
}
