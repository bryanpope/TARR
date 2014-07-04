package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrew on 04/07/2014.
 */

public class OverworldPathfinding
{
    List<Node> openList = new ArrayList<Node>();
    Set<Node> closedList = new HashSet<Node>();

    Direction directionLeft;
    Direction directionRight;

    public List<Node> IAmAPathAndILikeCheese(TiledMap tiles, Node start, Node goal)
    {
        closedList.clear();
        openList.clear();
        Node currentNode = new Node(start.row, start.col, start.gCost, start.fCost, null, start.facing);
        int row;
        int col;
        Node tempChildNode;

        if(currentNode == goal)
        {
            return null;
        }

        while (!catchMeIfYouCan(currentNode, goal))
        {
            row = currentNode.row;
            col = currentNode.col;
            tempChildNode = new Node(row, col, currentNode.gCost, currentNode.fCost, currentNode, null);

            //right child
            tempChildNode.col++;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.EAST);

            //left child
            tempChildNode.col -= 2;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.WEST);

            //top child
            tempChildNode.col++;
            tempChildNode.row--;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.NORTH);

            //bottom child
            tempChildNode.row += 2;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.SOUTH);

            //bottom right
            tempChildNode.col++;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.SOUTHEAST);

            //bottom left
            tempChildNode.col -= 2;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.SOUTHWEST);

            //top left
            tempChildNode.row -= 2;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.NORTHWEST);

            //top right
            tempChildNode.col += 2;
            addChild(tempChildNode, tiles, currentNode, goal, Direction.NORTHEAST);

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

    public boolean isClosed(Node n, Set<Node> closed)
    {
        return closed.contains(n);
    }

    public boolean isOpen(Node n)
    {
        return openList.contains(n);
    }

    public void addChild(Node child, TiledMap tiles, Node currentNode, Node goal, Direction facing)
    {
        if((child.row >= 0 && child.col >= 0) && (child.row <= tiles.height && child.col <= tiles.width))
        {
            if (tiles.isPassable(child.row, child.col))
            {
                if (!isClosed(child, closedList))
                {
                    child.facing = facing;
                    double g = currentNode.gCost + getDistanceFromParent(child, currentNode);
                    double f = g + getDistance(child.row, child.col, goal);

                    if(isOpen(child))
                    {
                        if(child.gCost > g)
                        {
                            child.fCost = f;
                            child.gCost = g;
                            child.parentNode = currentNode;
                        }
                    }
                    else
                    {
                        child = new Node(child.row, child.col, g, f, currentNode, facing);

                        openList.add(child);
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
    public double getDistanceFromParent(Node childNode, Node parent)
    {
        directionLeft = Direction.turnLeft(parent.facing);
        directionRight = Direction.turnRight(parent.facing);

        if(childNode.facing != parent.facing)
        {
            if(childNode.facing == directionLeft || childNode.facing == directionRight)
            {
                return Math.sqrt((childNode.row - parent.row) * (childNode.row - parent.row) + (childNode.col - parent.col) * (childNode.col - parent.col));
            }
            else
            {
                return 1;
            }
        }
        return 1;
    }

    public double getDistance(double row, double col, Node goal)
    {
        return Math.sqrt((goal.row - row) * (goal.row - row) + (goal.col - col) * (goal.col - col));
    }
}
