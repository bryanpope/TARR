package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;


public class Pathfinding
{
    List<Node> closedList = new ArrayList<Node>();
    List<Node> openList = new ArrayList<Node>();
    private boolean open[][] = new boolean[40][15];

    public List<Node> IAmAPathAndILikeCheese(TiledMap tiles, Node start, Node goal)
    {
        Node currentNode = new Node(start.row, start.col, start.gCost, start.fCost, null);
        closedList.clear();
        openList.clear();

        while (!catchMeIfYouCan(currentNode, goal))
        {
            if(currentNode == goal)
            {
                return null;
            }

            int row = currentNode.row;
            int col = currentNode.col;

            //right child
            col++;
            addChild(row, col, tiles, currentNode, goal);

            //left child
            col -= 2;
            addChild(row, col, tiles, currentNode, goal);

            //top child
            col++;
            row--;
            addChild(row, col, tiles, currentNode, goal);

            //bottom child
            row += 2;
            addChild(row, col, tiles, currentNode, goal);

            //bottom right
            col++;
            addChild(row, col, tiles, currentNode, goal);

            //bottom left
            col -= 2;
            addChild(row, col, tiles, currentNode, goal);

            //top left
            row -= 2;
            addChild(row, col, tiles, currentNode, goal);

            //top right
            col += 2;
            addChild(row, col, tiles, currentNode, goal);

            //Put currentNode in the closedList
            closedList.add(currentNode);
            //Sort the openList
            Collections.sort(openList);
            //Assign currentNode to the last element in the List
            currentNode = openList.remove(openList.size() - 1);
            //System.out.println("Curr Node Row " +  currentNode.row + ", Curr Node Col " + currentNode.col);
        }

        List<Node> path = new ArrayList<Node>();
        while (currentNode.parentNode != null)
        {
            path.add(currentNode);
            currentNode = currentNode.parentNode;
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }


    public boolean catchMeIfYouCan(Node currentNode, Node goalNode)
    {
        return (currentNode.col == goalNode.col) && (currentNode.row == goalNode.row);
    }

    public boolean isNodeClosed(double row, double col)
    {
        for (int i = 0; i < closedList.size(); ++i)
        {
            if (closedList.get(i).col == col && closedList.get(i).row == row)
            {
                return true;
            }
        }
        return false;
    }

    public Node getChildFromOpen(double row, double col)
    {
        for (int i = 0; i < openList.size(); ++i)
        {
            if (openList.get(i).col == col && openList.get(i).row == row)
            {
                return openList.get(i);
            }
        }
        return null;
    }

    public void addChild(int row, int col, TiledMap tiles, Node currentNode, Node goal)
    {
        if((row >= 0 && col >= 0) && (row <= 14 && col <= 39))
        {
            if (tiles.isPassable(row, col))
            {
                if (!isNodeClosed(row, col))
                {
                    double g = currentNode.gCost + getDistanceFromParent(row, col, currentNode);
                    double f = g + getDistance(row, col, goal);
                    Node child = getChildFromOpen(row, col);

                    if (child == null)
                    {
                        child = new Node(row, col, g, f, currentNode);

                        openList.add(child);
                    }
                    else if (child.gCost > g)
                    {
                        child.fCost = f;
                        child.gCost = g;
                        child.parentNode = currentNode;
                    }
                }
            }
        }
    }

    public double getDistance(int row, int col, Node goal)
    {
        return Math.sqrt((goal.row - row) * (goal.row - row) + (goal.col - col) * (goal.col - col));
    }

    public double getDistanceFromParent(int row, int col, Node parent)
    {
        return Math.sqrt((row - parent.row) * (row - parent.row) + (col - parent.col) * (col - parent.col));
    }

    public boolean LOSCheck(Node start, Node goal, TiledMap tiles)
    {
        double deltaX = goal.col - start.col;
        double deltaY = goal.row - start.row;

        //determine which octant we are in
        if (deltaX >= 0 && deltaY >= 0)
        {
            if (deltaX < deltaY)
            {
                double slope = Math.abs(deltaX / deltaY);

                double error = 0;
                double currX = start.col;

                for (int currY = start.row; currY <= goal.row; ++currY)
                {
                    //check tile
                    if (tiles.isPassable((int) Math.floor(currX / tiles.tileHeight), (int) Math.floor(currY / tiles.tileHeight)))
                    {
                        return false;
                    }

                    error += slope;
                    if (error >= 0.5)
                    {
                        currX++;
                        error -= 1.0;
                    }
                }

                return true;
            }
            else
            {
                double slope = Math.abs(deltaY / deltaX);

                double error = 0;
                double currY = start.row;

                for (int currX = start.col; currX <= goal.col; ++currX)
                {
                    //check tile
                    if (tiles.isPassable((int) Math.floor(currX / tiles.tileHeight), (int) Math.floor(currY / tiles.tileHeight)))
                    {
                        return false;
                    }

                    error += slope;
                    if (error >= 0.5)
                    {
                        currY++;
                        error -= 1.0;
                    }
                }

                return true;
            }
        } else if (deltaX < 0 && deltaY >= 0)
        {
            if (-deltaX < deltaY)
            {
                double slope = Math.abs(-deltaX / deltaY);

                double error = 0;
                double currX = start.col;

                for (int currY = start.row; currY <= goal.row; ++currY)
                {
                    //check tile
                    if (tiles.isPassable((int) Math.floor(currX / tiles.tileHeight), (int) Math.floor(currY / tiles.tileHeight)))
                    {
                        return false;
                    }

                    error += slope;
                    if (error >= 0.5)
                    {
                        currX--;
                        error -= 1.0;
                    }
                }

                return true;
            }
            else
            {
                double slope = Math.abs(deltaY / -deltaX);

                double error = 0;
                double currY = start.row;


                for (int currX = start.col; currX >= goal.col; --currX)
                {
                    //check tile
                    if (tiles.isPassable((int) Math.floor(currX / tiles.tileHeight), (int) Math.floor(currY / tiles.tileHeight)))
                    {
                        return false;
                    }

                    error += slope;
                    if (error >= 0.5)
                    {
                        currY++;
                        error -= 1.0;
                    }
                }

                return true;
            }
        }
        return true;
    }

    public void removeRedundant(Node path, TiledMap tiles)
    {
        Node currNode = path.parentNode;
        //while there is a line of sight  from start to current node, set path parent to node and go to next node
        while (currNode.parentNode != null)
        {
            if (LOSCheck(path, currNode, tiles))
            {
                path.parentNode = currNode;
            }
            currNode = currNode.parentNode;
        }
        if (path.parentNode.parentNode != null)
        {
            removeRedundant(path.parentNode, tiles);
        }
    }
}