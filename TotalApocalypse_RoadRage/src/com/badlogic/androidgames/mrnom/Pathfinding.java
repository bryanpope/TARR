package com.badlogic.androidgames.mrnom;

public class Pathfinding
{
	public boolean IAmAPathAndILikeCheese(tiles, start, goal, passableTiles)
	{
		//Create closed list
		closedList = new Array();
		//Create open list
		openList = new Array();
		
		currentNode = new Node();
		
		count = 0;
		
		while(!catchMeIfYouCan)
		{
			int row = currentNode.row;
			int col = currentNode.col + 1;
			
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			//left child
			col -= 2;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);
			
			//top child
			col++;
			row--;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			//bottom child
			row += 2;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			//bottom right
			col++;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			//bottom left
			col -= 2;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			//top left
			row -= 2;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			//top right
			col += 2;
			addChild(row, col, tiles, passableTiles, closedList, openList, currentNode, goal);

			closedList.push(currentNode);

			openList.sort(function (a, b) { return b.f - a.f });

			currentNode = openList.pop();
		}
		
		return currentNode;
	}

	public boolean catchMeIfYouCan()
	{
		return (currNode.col == goalNode.col) && (currNode.row == goalNode.row);
	}

	public int orderNodes(a, b)
	{
		if(a.fCost < b.fCost)
		{
			return 1;
		}
		else if(a.fCost > b.fCost)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

	public Node getLowestCostNode(openList)
	{
		openList.sort(Nodes);
		int retval = openList[openList.length - 1];
		openList.pop();
		return retval;
	}

	public boolean checkPassableTile(row, col, tiles, passableTiles)
	{
		int numCols = tiles[0].length;
		int numRows = tiles.length;
		
		if(row >= numRows || row < 0)
		{
			return false;
		}
		
		if(col >= numCols || col < 0)
		{
			return false;
		}
		
		for(int i = 0; i < passableTiles.length; ++i)
		{
			if(tiles[row][col] == passableTiles[i])
			{
				return true;
			}
		}
		
		return false;
	}

	public boolean isNodeClosed(row, col, closedList)
	{
		for(int i = 0; i < closedList.length; ++i)
		{
			if(closedList[i].pos.x == col && closedList[i].pos.y == row)
			{
				return true;
			}
		}
		return false;
	}

	public Node getChildFromOpen(row, col, openList)
	{
		for(int i = 0; i < openList.length; ++i)
		{
			if(openList[i].pos.x == col && openList[i].pos.y == row)
			{
				return openList[i];
			}
		}
		
		Node newNode;
		return newNode;
	}

	public void addChild(row, col, currentNode, target, openList, closedList)
	{
		if(checkPassableTile(row, col, tiles, passableTiles))
		{
			if(!isNodeClosed(row, col, closedList))
			{
			    int g = currentNode.g + getDistanceFromParent(row, col, currentNode);
			    int f = g + getDistance(row, col, target);
			    var child = getChildFromOpen(row, col, openList);
			    if (child == undefined)
			    {
			        //create child
			        child = Object.create(Node);
			        child.init(row, col, g, f, currentNode);
			        openList.push(child);
			    }
			    else if (child.g > g)
			    {
			        child.f = f;
			        child.g = g;
			        child.parentNode = currentNode;
			    }
			}
		}
		hCost = getHeuristic();
		addedChild.gCost = currentNode.gCost + 1;
		addedChild.fCost = addedChild.gCost + hCost;
		
		openList.push(addedChild);
		
	}

	public int getHeuristic(start, end)
	{
		return (Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
	}

	public int getManhattan()
	{
		return Math.abs(goal.row - row) + Math.abs(goal.col - col);
	}

	public int getDistance()
	{
		return Math.sqrt((goal.row - row) * (goal.row - row) + (goal.col - col) * (goal.col - col));
	}

	public int getDistanceFromParent()
	{
		return Math.sqrt((row - parent.row) * (row - parent.row) + (col - parent.col) * (col - parent.col));
	}
}