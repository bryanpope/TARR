package com.totalannihilationroadrage;

public class Node implements Comparable<Node>
{
    public int row;
    public int col;
    public double gCost;
    public double fCost;
    public Node parentNode = null;

    public Node (int row, int col, double gCost, double fCost, Node parentNode)
    {
        this.row = row;
        this.col = col;
        this.gCost = gCost;
        this.fCost = fCost;
        this.parentNode = parentNode;
    }

    public int compareTo(Node other)
    {
        if(this.fCost < other.fCost)
        {
            return 1;
        }
        else if(this.fCost > other.fCost)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}