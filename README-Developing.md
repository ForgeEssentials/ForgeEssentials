#Developing ForgeEssentials#
____________________________
###This is a guide for setting up a clean environment for WORKING ON ForgeEssentials. Not the API.###
_____________________________________________________________________________________________________________

You do not need to follow these instructions if you wish to use the API. The API is downloadable from Jenkins.

<br>

## Setting up Forge and AT ##


1 Get a clean Forge [install](http://files.minecraftforge.net/minecraftforge/minecraftforge-universal-1.5.1-7.7.1.611.zip). DO NOT DECOMPILE YET, the AT has to be applied!

3 Make a directory in the forge folder (where install.cmd/sh is), named 'accesstransformers'.

3 COPY src/FE_SRC_COMMON/forgeessentials_at.cfg to the accesstransformers folder you just created.

4 Run install.sh.

<br>

## Setting up MCP ##


1 From the folder you have cloned the repo into (or extracted a zipball), COPY all the files there into the forge/mcp folder.

2 Install [Eclipse](http://www.eclipse.org/)

3 Set the typical MCP workspace as your workspace. There should be 3 linked sources under the Minecraft project, namely src (minecraft code), FE_SRC_COMMON (FE common/server code), and FE_SRC_CLIENT (client addon code)

<br>

## Setting up Eclipse ##

#### Only for the event you launch MC and you crash with a java.lang.IllegalAccessError ####
____________________________________________________________________________________________

1 Load Eclipse and click on the down arrow next to the green play button. Under it, select Run Configurations.

2 Under Client, select the Arguments tab

3 In VM Arguments, copy in the following code: <code>-Dfml.coreMods.load=com.ForgeEssentials.core.preloader.FEPreLoader</code>

4 Do the same, but for Server this time. This time, add: <code>-Dforgeessentials.client.developermode=true</code>

You're good to go!
