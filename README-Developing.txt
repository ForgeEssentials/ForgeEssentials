This is a guide for setting up a clean environment for WORKING ON FE. 

You do not need to download the repo if you merely want to use the API, that can be downloaded from jenkins.

1. Setting up Forge and AT

1.1: Get a clean Forge install. DO NOT DECOMPILE YET, the AT has to be applied!

1.2: Make a directory in the forge folder (where install.cmd/sh is), named 'accesstransformers'.

1.3: COPY forgeessentials_at.cfg from src/FE_SRC_COMMON to the accesstransformers folder you just created.

1.4: Run install.sh.

2. Setting up MCP

2.1: From the folder you have cloned the repo into (or extracted a zipball), COPY all the files there into the forge/mcp folder.

2.2: Install Eclipse and use the typical MCP workspace as your workspace.

There should be 3 linked sources under the Minecraft project, namely src (minecraft code), FE_SRC_COMMON (FE common/server code), and FE_SRC_CLIENT (client addon code)

3. Setting up Eclipse (only if you launch MC and you crash with a java.lang.IllegalAccessError)

3.1: Load Eclipse and click on the down arrow next to the green play button. Under it, select Run Configurations.

3.2: Under Client, select the Arguments tab

3.3: In VM Arguments, copy in the following code (without the double quotes) "-Dfml.coreMods.load=com.ForgeEssentials.core.preloader.FEPreLoader"

3.4: Do the same, but for Server this time.

You're good to go!