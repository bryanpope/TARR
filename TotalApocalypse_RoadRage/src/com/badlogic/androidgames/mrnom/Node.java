package com.badlogic.androidgames.mrnom;

public class Node
{
    private int row;
    private int col;
    private int g;
    private int f;
    private Node parentNode = null;

    public Node (row, col, g, f, parentNode)
    {
        this.row = row;
        this.col = col;
        this.g = g; 
        this.f = f;
        this.parentNode = parentNode;
    }
}