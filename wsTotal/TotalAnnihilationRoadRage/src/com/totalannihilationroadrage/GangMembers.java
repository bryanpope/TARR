package com.totalannihilationroadrage;

public class GangMembers 
{
	int armsmasters;
	int bodyguards;
	int commandos;
	int dragoons;
	int escorts;

    GangMembers ()
    {
        armsmasters = 0;
        bodyguards = 0;
        commandos = 0;
        dragoons = 0;
        escorts = 0;
    }

    GangMembers (GangMembers roster)
    {
        armsmasters = roster.armsmasters;
        bodyguards = roster.bodyguards;
        commandos = roster.commandos;
        dragoons = roster.dragoons;
        escorts = roster.escorts;
    }

    public boolean allDead ()
    {
        return (armsmasters == 0) && (bodyguards == 0) && (commandos == 0) && (dragoons == 0) && (escorts == 0);
    }
}
