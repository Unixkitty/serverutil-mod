A rather simple command utility mod, adds a few commands I felt could be useful on their own. Can be, but doesn't need to be installed on client if playing in a client-server scenario.

**Commands**:

1. "tpx" for inter-dimensional teleportation
    
    /**tpx** (target entity) (destination entity)
    OR
    /**tpx** (target entity) (dimensionId) [ \<x> \<y> \<z> ]
    
2. "dimensionlist" to list registered dimensions with id, name and subfolder name in save directory
    
    /**dimensionlist**
    
    Example output:
        
        overworld, id: 0
        the_nether, id: -1, subfolder: "DIM-1"
        the_end, id: 1, subfolder: "DIM1"

3. "regioncoord" to determine region file and chunk coordinates containing specified x and z

    /**regioncoord** \<x> \<z>
    
    Example output:
    
        Region file name: r.3.4.mca, chunk coord: 112, 132
        
4. "motd" - enables simplistic server MOTD functionality, which by default shows the configured message to players on login. File (MINECRAFTDIR/serverutil-motd.txt) is generated on startup automatically, or can be created manually. Has to be UTF-8, but additionally supports standard [Minecraft format codes](https://minecraft.gamepedia.com/Formatting_codes). Players can opt out from receiving motd, ops can opt out others.

    /**motd** [reload]
    
    /**motd** ( enable | disable ) [ username | uuid ]
    
    Example output:

    ![](http://static.unixkitty.com/19ccdn7g65.png)

    Example file contents:
    
        §nWelcome to the server!
        This is an §aexample§r greeting message
        §nMinecraft Formatting

        §r§00 §11 §22 §33
        §44 §55 §66 §77
        §88 §99 §aa §bb
        §cc §dd §ee §ff

        §r§0k §kMinecraft
        §rl §lMinecraft
        §rm §mMinecraft
        §rn §nMinecraft
        §ro §oMinecraft
        §rr §rMinecraft

5. "playerid" - get either a player's current username and their unique id (Only works properly with online-mode=true in [server.properties](https://minecraft.gamepedia.com/Server.properties)), caches results.

    /**playerid** ( username | uuid )
    
    Example output:
    
        Notch, 069a79f4-44e9-4726-a5be-fca90e38aaf5

6. "mod_bugs" - utility to store known bugs in a json file for players to be aware of, using a generalized chat command. Configurable permissions on command functions, by default anyone can view bugs with /**mod_bugs**, but only ops can do manipulations.

    /**mod_bugs** add (name) (who discovered/associated player) [description]
    
    /**mod_bugs** person \<name>
    
    /**mod_bugs** reload
    
    /**mod_bugs** remove \<name>
    
    /**mod_bugs** update \<name> \< description | status >
    
    status can be one of: "relevant", "fixed"

    Example output:
    
    ![](http://static.unixkitty.com/rp8agq6q07.png)
    
P.S. This is my first mod release, please don't yell if something's wrong :P