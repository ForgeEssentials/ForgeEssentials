VERSION="1.2.1"
MC="1.5.1"


#in case we arnt there already
cd ${WORKSPACE}

# try getting the version from files
VERSION="`head -n 1 VERSION.TXT`"
VERSION="${VERSION}.${BUILD_NUMBER}"
MC="`head -2 VERSION.TXT | tail -1 VERSION.TXT`"
echo "Version of ForgeEssentials is: ${VERSION} for MC ${MC}"

echo "Downloading Forge..."

wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip


unzip minecraftforge-src-*.zip
rm minecraftforge-src-*.zip
rm -rf ./oldcode/*
cd forge

echo "Copying FE AccessTransformer..."
mkdir accesstransformers
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/forgeessentials_at.cfg ${WORKSPACE}/forge/accesstransformers/

echo "Installing Forge..."
bash ./install.sh
cd mcp

echo "Copying ForgeEssentials and related libraries into MCP..."
cd src
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/* ./minecraft/
cp -rf ${WORKSPACE}/src/FE_SRC_CLIENT/* ./minecraft/
cd ..
cd lib
cp -rf ${WORKSPACE}/lib/* .
cd ..

echo "Injecting version number into places"
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/server/mcmod.info
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/client/mcmod.info
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/api/FEAPIReadme.txt
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/servercomplete/FEReadme.txt
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/client/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/server/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/client/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/api/FEAPIReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/client/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/servercomplete/FEReadme.txt
sed -i 's/@VERSION@/'${VERSION}'/g' src/minecraft/com/ForgeEssentials/core/preloader/FEModContainer.java
sed -i 's/@VERSION@/'${VERSION}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java
sed -i 's/@BETA@/'${BETA}'/g' src/minecraft/com/ForgeEssentials/core/preloader/FEModContainer.java
sed -i 's/@BETA@/'${BETA}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java

echo "Recompiling..."
bash ./recompile.sh

echo "Reobfuscating..."
bash ./reobfuscate_srg.sh

# create this ahead of time...
mkdir ${WORKSPACE}/output
cd reobf/minecraft

echo "Creating Client package"
cp -rf ${WORKSPACE}/resources/client/*.
zip -r9 "${WORKSPACE}/output/ForgeEssentials-client-${MC}-${VERSION}.zip" ./com/ForgeEssentials/client/* mcmod.info logo.png FEReadme.txt LICENSE.TXT
rm -rf ./com/ForgeEssentials/client
rm -rf ./*.info

echo "Copying in extra files for core"
cp -rf ${WORKSPACE}/resources/server/* .

echo "Creating server packages"
jar cvfm "${WORKSPACE}/output/ForgeEssentials-core-${MC}-${VERSION}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* logo.png mcmod.info forgeessentials_at.cfg ./com/ForgeEssentials/api/permissions ./com/ForgeEssentials/api/packetInspector
zip -r9 "${WORKSPACE}/output/ForgeEssentials-auth-${MC}-${VERSION}.zip" ./com/ForgeEssentials/auth/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-backups-${MC}-${VERSION}.zip" ./com/ForgeEssentials/backup/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-chat-${MC}-${VERSION}.zip" ./com/ForgeEssentials/chat/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-commands-${MC}-${VERSION}.zip" ./com/ForgeEssentials/commands/* ./com/ForgeEssentials/api/commands
zip -r9 "${WORKSPACE}/output/ForgeEssentials-economy-${MC}-${VERSION}.zip" ./com/ForgeEssentials/economy/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-playerlogger-${MC}-${VERSION}.zip" ./com/ForgeEssentials/playerLogger/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-protection-${MC}-${VERSION}.zip" ./com/ForgeEssentials/protection/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-questioner-${MC}-${VERSION}.zip" ./com/ForgeEssentials/questioner/* ./com/ForgeEssentials/api/questioner 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-snooper-${MC}-${VERSION}.zip" ./com/ForgeEssentials/snooper/* ./com/ForgeEssentials/api/json ./com/ForgeEssentials/api/snooper
zip -r9 "${WORKSPACE}/output/ForgeEssentials-servervote-${MC}-${VERSION}.zip" ./com/ForgeEssentials/serverVote/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-scripting-${MC}-${VERSION}.zip" ./com/ForgeEssentials/scripting/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-tickets-${MC}-${VERSION}.zip" ./com/ForgeEssentials/tickets/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-worldborder-${MC}-${VERSION}.zip" ./com/ForgeEssentials/WorldBorder/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-WorldControl-${MC}-${VERSION}.zip" ./com/ForgeEssentials/WorldControl/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-afterlife-${MC}-${VERSION}.zip" ./com/ForgeEssentials/afterlife/*
rm -rf ./*.info ./*.txt logo.png

echo "Creating ServerComplete package"
cd ${WORKSPACE}/output
cp -rf ${WORKSPACE}/resources/servercomplete/* .

mkdir mods
mkdir coremods
# coremod.. then alphebetical order please...
cp -rf "${WORKSPACE}/output/ForgeEssentials-core-${MC}-${VERSION}.jar" ./coremods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-auth-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-backups-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-chat-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-commands-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-economy-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-playerlogger-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-protection-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-questioner-${MC}-${VERSION}.zip"  ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-snooper-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-servervote-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-scripting-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-tickets-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-worldborder-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-WorldControl-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-afterlife-${MC}-${VERSION}.zip" ./mods/
zip -r9 "${WORKSPACE}/output/ForgeEssentials-ServerComplete-${MC}-${VERSION}.zip" ./coremods/* ./mods/* FEReadme.txt HowToGetFEsupport.txt LICENSE.TXT

echo "Cleaning up"
rm -rf ./mods/*
rm -rf ./coremods/*
rm -rf "${WORKSPACE}/output/ForgeEssentials-auth-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-backups-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-chat-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-core-${MC}-${VERSION}.jar" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-commands-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-economy-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-playerlogger-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-protection-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-questioner-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-servervote-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-scripting-${MC}-${VERSION}.zip"
rm -rf "${WORKSPACE}/output/ForgeEssentials-snooper-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-tickets-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-worldborder-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-WorldControl-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/ForgeEssentials-afterlife-${MC}-${VERSION}.zip" 
rm -rf "${WORKSPACE}/output/FEReadme.txt"
rm -rf "${WORKSPACE}/output/HowToGetFEsupport.txt"
rm -rf "${WORKSPACE}/output/LICENSE.TXT"

echo "Creating API package"
cd ${WORKSPACE}/src/FE_SRC_COMMON
cp -f ${WORKSPACE}/resources/api/* . .
zip -r9 "${WORKSPACE}/output/ForgeEssentials-API-r2-${VERSION}.zip" ./com/ForgeEssentials/api/* FEAPIReadme.txt LICENSE.TXT

#upload
