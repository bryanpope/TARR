package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Pathfinding
{
    List<Node> closedList = new ArrayList<Node>();
    List<Node> openList = new ArrayList<Node>();
    TiledMap world = new TiledMap();

	public Node IAmAPathAndILikeCheese(List tiles, Node start, Node goal, List impassable)
	{

		Node currentNode = new Node(start.row, start.col, start.gCost, start.fCost, null);

		while(!catchMeIfYouCan(currentNode, goal))
		{
			int row = currentNode.row;
			int col = currentNode.col + 1;
			
			addChild(row, col, tiles, currentNode, goal, impassable);

			//left child
			col -= 2;
            addChild(row, col, tiles, currentNode, goal, impassable);
			
			//top child
			col++;
			row--;
            addChild(row, col, tiles, currentNode, goal, impassable);

			//bottom child
			row += 2;
            addChild(row, col, tiles, currentNode, goal, impassable);

			//bottom right
			col++;
            addChild(row, col, tiles, currentNode, goal, impassable);

			//bottom left
			col -= 2;
            addChild(row, col, tiles, currentNode, goal, impassable);

            //top left
			row -= 2;
            addChild(row, col, tiles, currentNode, goal, impassable);

			//top right
			col += 2;
            addChild(row, col, tiles, currentNode, goal, impassable);

            //Put currentNode in the closedList
            closedList.add(currentNode);
            //Sort the openList
            Collections.sort(openList);
            //Assign currentNode to the last element in the List
            currentNode = openList.remove(openList.size() - 1);
            //System.out.println("Curr Node Row " +  currentNode.row + ", Curr Node Col " + currentNode.col);
		}

        return currentNode;
	}

	public boolean catchMeIfYouCan(Node currentNode, Node goalNode)
	{
		return (currentNode.col == goalNode.col) && (currentNode.row == goalNode.row);
	}

	public boolean checkPassableTile(int row, int col, List tiles, List impassable)
	{
       /* int numCols = world.width;
        int numRows = world.height;

        if(row >= numRows || row < 0)
        {
            return false;
        }

        if(col >= numCols || col < 0)
        {
            return false;
        }*/

        return true;

    }

	public boolean isNodeClosed(int row, int col)
	{
		for(int i = 0; i < closedList.size(); ++i)
		{
			if(closedList.get(i).col == col && closedList.get(i).row == row)
			{
				return true;
			}
		}
		return false;
	}

	public Node getChildFromOpen(int row, int col, List<Node> openList)
	{
		for(int i = 0; i < openList.size(); ++i)
		{
			if(openList.get(i).col == col && openList.get(i).row == row)
			{
				return openList.get(i);
			}
		}
		return null;
	}

	public void addChild(int row, int col, List tiles, Node currentNode, Node target, List impassable)
	{
		if(checkPassableTile(row, col, tiles, impassable))
		{
			if(!isNodeClosed(row, col))
			{
			    int g = currentNode.gCost + getDistanceFromParent(row, col, currentNode);
			    int f = g + getDistance(row, col, target);
			    Node child = getChildFromOpen(row, col, openList);
			   
			    if(child == null)
			    {
                    child = new Node(row, col, g, f, currentNode);
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

	public int getHeuristic(Node start, Node end)
	{
		return (Math.abs(start.col - end.col) + Math.abs(start.row - end.row));
	}

	public int getManhattan(int row, int col, Node goal)
	{
		return Math.abs(goal.row - row) + Math.abs(goal.col - col);
	}

	public int getDistance(int row, int col, Node goal)
	{
		return (int)Math.sqrt((goal.row - row) * (goal.row - row) + (goal.col - col) * (goal.col - col));
	}

	public int getDistanceFromParent(int row, int col, Node parent)
	{
		return (int)Math.sqrt((row - parent.row) * (row - parent.row) + (col - parent.col) * (col - parent.col));
	}
	
	/*public boolean BresenhamsLOSCheck(int x1, int y1, int x2, int y2)
	{
		int deltaX = x2 - x1;
		int deltaY = y2 - y1;

		if(deltaX >= 0 && deltaY >= 0)
		{
			if(deltaX < deltaY)
			{
				int slope = Math.abs(deltaX / deltaY);
		
				int error = 0;
				int currX = x1;
				
				for(int currY = y1; currY <= y2; ++currY)
				{
					if(checkPassableTile(currX / 32, currY / 32))
					{
						return false;
					}
					
					error += slope;
					if(error >= 0.5)
					{
						currX++;
						error -= 1.0;
					}
				}
				
				return true;
			}
			else
			{
				int slope = Math.abs(deltaY / deltaX);
		
				int error = 0;
				int currY = y1;
				
				for(int currX = x1; currX <= x2; ++currX)
				{
					if(checkPassableTile(Math.floor(currX / 32), Math.floor(currY / 32)))
					{
						return false;
					}
					
					error += slope;
					if(error >= 0.5)
					{
						currY++;
						error -= 1.0;
					}
				}
				
				return true;
			}
		}
		
		else if(deltaX < 0 && deltaY >= 0)
		{
			if(-deltaX < deltaY)
			{
				int slope = Math.abs(-deltaX / deltaY);
		
				int error = 0;
				int currX = x1;
				
				for(int currY = y1; currY <= y2; ++currY)
				{
					if(checkPassableTile(Math.floor(currX / 32), Math.floor(currY / 32)))
					{
						return false;
					}
					
					error += slope;
					if(error >= 0.5)
					{
						currX--;
						error -= 1.0;
					}
				}
				
				return true;
			}
			else
			{
				int slope = Math.abs(deltaY / -deltaX);
		
				int error = 0;
				int currY = y1;
				
				
				for(int currX = x1; currX >= x2; --currX)
				{
					if(checkPassableTile(Math.floor(currX / 32), Math.floor(currY / 32)))
					{
						return false;
					}
				
					error += slope;
					if(error >= 0.5)
					{
						currY++;
						error -= 1.0;
					}
				}
				
				return true;
			}
		}
		return true;
	}*/
}