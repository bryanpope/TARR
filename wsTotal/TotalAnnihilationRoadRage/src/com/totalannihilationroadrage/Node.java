package com.totalannihilationroadrage;

public class Node
{
    public int row;
    public int col;
    public int gCost;
    public int fCost;
    public Node parentNode = null;

    public Node (int row, int col, int gCost, int fCost, Node parentNode)
    {
        this.row = row;
        this.col = col;
        this.gCost = gCost;
        this.fCost = fCost;
        this.parentNode = parentNode;
    }
}