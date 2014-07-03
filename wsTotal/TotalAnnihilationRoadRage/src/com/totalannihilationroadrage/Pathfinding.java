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
    Direction directionLeft;
    Direction directionRight;

    public List<Node> IAmAPathAndILikeCheese(TiledMap tiles, Node start, Node goal)
    {
        Node currentNode = new Node(start.row, start.col, start.gCost, start.fCost, null, start.facing);
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
            addChild(row, col, tiles, currentNode, goal, Direction.EAST);

            //left child
            col -= 2;
            addChild(row, col, tiles, currentNode, goal, Direction.WEST);

            //top child
            col++;
            row--;
            addChild(row, col, tiles, currentNode, goal, Direction.NORTH);

            //bottom child
            row += 2;
            addChild(row, col, tiles, currentNode, goal, Direction.SOUTH);

            //bottom right
            col++;
            addChild(row, col, tiles, currentNode, goal, Direction.SOUTHEAST);

            //bottom left
            col -= 2;
            addChild(row, col, tiles, currentNode, goal, Direction.SOUTHWEST);

            //top left
            row -= 2;
            addChild(row, col, tiles, currentNode, goal, Direction.NORTHWEST);

            //top right
            col += 2;
            addChild(row, col, tiles, currentNode, goal, Direction.NORTHEAST);

            //Put currentNode in the closedList
            closedList.add(currentNode);
            //Sort the openList
            Collections.sort(openList);
            //Assign currentNode to the last element in the List
            if(openList.size() > 0)
            {
                currentNode = openList.remove(openList.size() - 1);
            }
            else
            {
                return null;
            }
        }

        List<Node> path = new ArrayList<Node>();
        while (currentNode.parentNode != null)
        {
            path.add(currentNode);
            currentNode = currentNode.parentNode;
        }
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

    public void addChild(int row, int col, TiledMap tiles, Node currentNode, Node goal, Direction facing)
    {
        if((row >= 0 && col >= 0) && (row <= tiles.height && col <= tiles.width))
        {
            if (tiles.isPassable(row, col))
            {
                if (!isNodeClosed(row, col))
                {
                    Node tempChildNode = new Node(row, col, currentNode.gCost, currentNode.fCost, currentNode, facing);
                    double g = currentNode.gCost + getDistanceWithFacing(tempChildNode, currentNode);
                    double f = g + getDistance(row, col, currentNode);
                    Node child = getChildFromOpen(row, col);

                    if (child == null)
                    {
                        child = new Node(row, col, g, f, currentNode, facing);

                        openList.add(child);
                    }
                    else if(child.gCost > g)
                    {
                        child.fCost = f;
                        child.gCost = g;
                        child.parentNode = currentNode;
                    }
                }
            }
        }
    }

    /*
        This function uses facing to determine the g cost from the parent node.
        The goal is to have the AI only be able to go three directions. The way they are facing,
        turning to the right of that direction, or turning to the left of that direction.
     */
    public double getDistanceWithFacing(Node tempChildNode, Node parent)
    {
        directionLeft = Direction.turnLeft(tempChildNode.facing);
        directionRight = Direction.turnRight(tempChildNode.facing);

        if(tempChildNode.facing != parent.facing)
        {
            if(tempChildNode.facing == directionLeft || tempChildNode.facing == directionRight)
            {
                return Math.sqrt((tempChildNode.row - parent.row) * (tempChildNode.row - parent.row) + (tempChildNode.col - parent.col) * (tempChildNode.col - parent.col));
            }
            return ((tempChildNode.row - parent.row) * (tempChildNode.row - parent.row) + (tempChildNode.col - parent.col) * (tempChildNode.col - parent.col)) * 2;
        }
        return 1;
    }

    public double getDistance(double row, double col, Node goal)
    {
        return Math.sqrt((goal.row - row) * (goal.row - row) + (goal.col - col) * (goal.col - col));
    }

    public double getDistanceFromParent(double row, double col, Node parent)
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

    /*public void removeRedundant(Node path, TiledMap tiles)
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
    }*/
}