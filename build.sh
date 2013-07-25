VERSION="SNAPSHOT"
MC="1.5.2"
VERSION="`head -n 1 VERSION.TXT`"
VERSIONBUILD="${VERSION}.${bamboo_buildNumber}"
MC="`head -2 VERSION.TXT | tail -1 VERSION.TXT`"
echo "Building ForgeEssentials ${VERSIONBUILD} for MC ${MC} in ${bamboo_build_working_directory}"

echo "Downloading Forge..."

#wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip
wget -nv http://files.minecraftforge.net/minecraftforge/minecraftforge-src-1.5.2-7.8.1.738.zip

unzip -q minecraftforge-src-*.zip
rm minecraftforge-src-*.zip
rm -rf ./oldcode/*
chmod +x **/*.sh
cd forge

echo "Copying FE AccessTransformer..."
mkdir accesstransformers
cp -rf ${bamboo_build_working_directory}/src/FE_SRC_COMMON/forgeessentials_at.cfg ${bamboo_build_working_directory}/forge/accesstransformers/

echo "Installing Forge..."
./install.sh
cd mcp

echo "Copying ForgeEssentials and related libraries into MCP..."
cd src
cp -rf ${bamboo_build_working_directory}/src/FE_SRC_COMMON/* ./minecraft/
cp -rf ${bamboo_build_working_directory}/src/FE_SRC_CLIENT/* ./minecraft/
cd ..
cd lib
cp -rf ${bamboo_build_working_directory}/lib/* .
cd ..

echo "Injecting version number into places"
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' ${bamboo_build_working_directory}/resources/server/mcmod.info
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' ${bamboo_build_working_directory}/resources/client/mcmod.info
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' ${bamboo_build_working_directory}/resources/api/FEAPIReadme.txt
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' ${bamboo_build_working_directory}/resources/servercomplete/FEReadme.txt
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' ${bamboo_build_working_directory}/resources/client/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${bamboo_build_working_directory}/resources/server/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${bamboo_build_working_directory}/resources/client/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${bamboo_build_working_directory}/resources/api/FEAPIReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${bamboo_build_working_directory}/resources/client/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${bamboo_build_working_directory}/resources/servercomplete/FEReadme.txt
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' src/minecraft/com/ForgeEssentials/core/preloader/FEModContainer.java
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java
sed -i 's/@BETA@/'${BETA}'/g' src/minecraft/com/ForgeEssentials/core/preloader/FEModContainer.java
sed -i 's/@BETA@/'${BETA}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java

echo "Recompiling..."
./recompile.sh

echo "Reobfuscating..."
./reobfuscate_srg.sh

# create this ahead of time...
mkdir ${bamboo_build_working_directory}/output
cd reobf/minecraft

echo "Creating Client package"
cp -rf ${bamboo_build_working_directory}/resources/client/* .
jar cvf "${bamboo_build_working_directory}/output/ForgeEssentials-client-${MC}-${VERSIONBUILD}.jar" ./com/ForgeEssentials/client/* mcmod.info logo.png FEReadme.txt LICENSE.TXT
rm -rf ./com/ForgeEssentials/client
rm -rf ./*.info

echo "Copying in extra files for core"
cp -rf ${bamboo_build_working_directory}/resources/server/* .

echo "Creating server packages"
jar cvfm "${bamboo_build_working_directory}/output/ForgeEssentials-core-${MC}-${VERSIONBUILD}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* logo.png mcmod.info forgeessentials_at.cfg ./com/ForgeEssentials/api/*.class ./com/ForgeEssentials/api/permissions ./com/ForgeEssentials/api/packetInspector ./com/ForgeEssentials/api/json
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-auth-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/auth/*
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-backups-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/backup/*
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-chat-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/chat/* 
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-commands-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/commands/* ./com/ForgeEssentials/api/commands
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-economy-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/economy/* 
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-playerlogger-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/playerLogger/* 
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-protection-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/protection/* 
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-questioner-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/questioner/* ./com/ForgeEssentials/api/questioner 
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-snooper-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/snooper/*  ./com/ForgeEssentials/api/snooper
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-servervote-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/serverVote/*
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-scripting-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/scripting/*
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-tickets-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/tickets/* 
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-worldborder-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/WorldBorder/*
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-WorldControl-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/WorldControl/*
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-afterlife-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/afterlife/*
rm -rf ./*.info ./*.txt logo.png

echo "Creating ServerComplete package"
cd ${bamboo_build_working_directory}/output
cp -rf ${bamboo_build_working_directory}/resources/servercomplete/* .

mkdir mods
mkdir coremods
# coremod.. then alphebetical order please...
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-core-${MC}-${VERSIONBUILD}.jar" ./coremods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-auth-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-backups-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-chat-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-commands-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-economy-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-playerlogger-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-protection-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-questioner-${MC}-${VERSIONBUILD}.zip"  ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-snooper-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-servervote-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-scripting-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-tickets-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-worldborder-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-WorldControl-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "${bamboo_build_working_directory}/output/ForgeEssentials-afterlife-${MC}-${VERSIONBUILD}.zip" ./mods/
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-ServerComplete-${MC}-${VERSION}.zip" ./coremods/* ./mods/* FEReadme.txt HowToGetFEsupport.txt LICENSE.TXT

echo "Cleaning up"
rm -rf ./mods/*
rm -rf ./coremods/*
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-auth-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-backups-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-chat-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-core-${MC}-${VERSIONBUILD}.jar" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-commands-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-economy-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-playerlogger-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-protection-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-questioner-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-servervote-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-scripting-${MC}-${VERSIONBUILD}.zip"
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-snooper-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-tickets-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-worldborder-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-WorldControl-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/ForgeEssentials-afterlife-${MC}-${VERSIONBUILD}.zip" 
rm -rf "${bamboo_build_working_directory}/output/FEReadme.txt"
rm -rf "${bamboo_build_working_directory}/output/HowToGetFEsupport.txt"
rm -rf "${bamboo_build_working_directory}/output/LICENSE.TXT"

echo "Creating API package"
cd ${bamboo_build_working_directory}/src/FE_SRC_COMMON
cp -f ${bamboo_build_working_directory}/resources/api/* . .
zip -r9 "${bamboo_build_working_directory}/output/ForgeEssentials-API-r2-${VERSIONBUILD}.zip" ./com/ForgeEssentials/api/* FEAPIReadme.txt LICENSE.TXT

#upload
