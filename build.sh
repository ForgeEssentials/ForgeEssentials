VERSION="1.2.1"
MC="1.5.1"
BETA="false"

#in case we arnt there already
cd ${WORKSPACE}

# try getting the version from files
VERSION="`head -n 1 VERSION.TXT`"
if [ BETA = true ]
then VERSION="${VERSION}.${BUILD_NUMBER}-betas"
else VERSION="${VERSION}.${BUILD_NUMBER}"
fi
MC="`tail -n 1 VERSION.TXT`"
echo "Version of ForgeEssentials is: ${VERSION} for MC ${MC}"

echo "Downloading Forge..."
#wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-1.5.1-7.7.2.682.zip
wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip
# if it didn't downlaod, try the mirror...
#if [ ! -f "minecraftforge-src-*.zip" ]
#then
#   echo "Forge server not found, using mirror"
#    wget http://ken.wingedboot.com/forgemirror/files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip
#fi

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

echo "Copying ForgeEssentials into MCP..."
cd src
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/* ./minecraft/
cp -rf ${WORKSPACE}/src/FE_SRC_CLIENT/* ./minecraft/
cd ..

echo "Adding in libraries..."
cd lib
cp -rf ${WORKSPACE}/lib/* .
cd ..

echo "injecting version into places"
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/server/mcmod.info
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/client/mcmod.info
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/FEAPIReadme.txt
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/resources/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/server/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/client/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/FEAPIReadme.txt
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/resources/FEReadme.txt
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
cp -rf ${WORKSPACE}/resources/client/mcmod.info .
cp -rf ${WORKSPACE}/resources/FEReadme.txt .
cp -rf ${WORKSPACE}/resources/logo.png .
cp -rf ${WORKSPACE}/resources/LICENSE.TXT .
zip -r9 "${WORKSPACE}/output/ForgeEssentials-client-${MC}-${VERSION}.zip" ./com/ForgeEssentials/client/* mcmod.info logo.png FEReadme.txt LICENSE.TXT
rm -rf ./com/ForgeEssentials/client
rm -rf ./*.info ./*.txt logo.png

echo "Copying in extra files for core"
cp -rf ${WORKSPACE}/resources/server/mcmod.info .
cp -rf ${WORKSPACE}/resources/META-INF .
cp -rf ${WORKSPACE}/resources/logo.png .
cp -rf ${WORKSPACE}/resources/HowToGetFEsupport.txt .
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/forgeessentials_at.cfg .
#rm ./com/ForgeEssentials/util/lang/dummyForGithub

echo "Creating server packages"
jar cvfm "${WORKSPACE}/output/ForgeEssentials-core-${MC}-${VERSION}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/coremod/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* logo.png mcmod.info forgeessentials_at.cfg HowToGetFEsupport.txt ./com/ForgeEssentials/api/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-auth-${MC}-${VERSION}.zip" ./com/ForgeEssentials/auth/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-backups-${MC}-${VERSION}.zip" ./com/ForgeEssentials/backup/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-chat-${MC}-${VERSION}.zip" ./com/ForgeEssentials/chat/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-commands-${MC}-${VERSION}.zip" ./com/ForgeEssentials/commands/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-economy-${MC}-${VERSION}.zip" ./com/ForgeEssentials/economy/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-playerlogger-${MC}-${VERSION}.zip" ./com/ForgeEssentials/playerLogger/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-protection-${MC}-${VERSION}.zip" ./com/ForgeEssentials/protection/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-snooper-${MC}-${VERSION}.zip" ./com/ForgeEssentials/snooper/* ./com/ForgeEssentials/api/snooper/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-servervote-${MC}-${VERSION}.zip" ./com/ForgeEssentials/serverVote/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-tickets-${MC}-${VERSION}.zip" ./com/ForgeEssentials/tickets/* 
zip -r9 "${WORKSPACE}/output/ForgeEssentials-worldborder-${MC}-${VERSION}.zip" ./com/ForgeEssentials/WorldBorder/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-WorldControl-${MC}-${VERSION}.zip" ./com/ForgeEssentials/WorldControl/*
zip -r9 "${WORKSPACE}/output/ForgeEssentials-afterlife-${MC}-${VERSION}.zip" ./com/ForgeEssentials/afterlife/*
rm -rf ./*.info ./*.txt logo.png

echo "Creating ServerComplete package"
cd ${WORKSPACE}/output
cp -rf ${WORKSPACE}/resources/servercomplete/*


mkdir mods
mkdir coremods
cp -rf ${WORKSPACE}/resources/FEReadme.txt .
cp -rf ${WORKSPACE}/resources/LICENSE.TXT .
cp -rf ${WORKSPACE}/resources/HowToGetFESupport.txt .
# coremod.. then alphebetical order please...
cp -rf "${WORKSPACE}/output/ForgeEssentials-core-${MC}-${VERSION}.jar" ./coremods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-auth-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-backups-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-chat-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-commands-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-economy-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-playerlogger-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-protection-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-snooper-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/ForgeEssentials-servervote-${MC}-${VERSION}.zip" ./mods/
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
rm -rf "${WORKSPACE}/output/ForgeEssentials-servervote-${MC}-${VERSION}.zip" 
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
cp -f ${WORKSPACE}/resources/LICENSE.TXT .
cp -rf ${WORKSPACE}/resources/FEAPIReadme.txt .
zip -r9 "${WORKSPACE}/output/ForgeEssentials-API-${MC}-${VERSION}.zip" ./com/ForgeEssentials/api/* ./com/ForgeEssentials/util/* FEAPIReadme.txt LICENSE.TXT

#upload
