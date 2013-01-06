VERSION="0.1.0.${BUILD_NUMBER}"
MC="1.4.6"


VERSION = head -n 1 "VERSION.TXT"
MC = tail -n 1 "VERSION.TXT"
echo "Version of ${JOB_NAME} is: ${VERSION} for MC ${MC}"

echo "Downloading Forge..."
#wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip 
wget http://ken.wingedboot.com/forgemirror/files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip
unzip minecraftforge-src-*.zip
rm minecraftforge-src-*.zip
rm "For later.zip"
cd forge

echo "Copying FE AccessTransformer..."
mkdir accesstransformers
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/forgeessentials_at.cfg ${WORKSPACE}/forge/accesstransformers/

echo "Installing Forge..."
bash ./install.sh
cd mcp

echo "Copying ${JOB_NAME} into MCP..."
cd src
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/* ./minecraft/
cp -rf ${WORKSPACE}/src/FE_SRC_CLIENT/* ./minecraft/
cd ..

echo "Adding in libraries..."
cd lib
cp -rf ${WORKSPACE}/lib/* .
cd ..

echo "injecting version into places"
sed -i 's/@VERSION@/'${VERSION}'/g' ${WORKSPACE}/A1-zipStuff/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/A1-zipStuff/mcmod.info
sed -i 's/@MC@/'${MC}'/g' ${WORKSPACE}/A1-zipStuff/mcmodCLIENT.info
sed -i 's/@VERSION@/'${VERSION}'/g' src/minecraft/com/ForgeEssentials/core/ForgeEssentials.java
sed -i 's/@VERSION@/'${VERSION}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java

echo "Recompiling..."
bash ./recompile.sh

echo "Reobfuscating..."
bash ./reobfuscate.sh

# create this ahead of time...
mkdir ${WORKSPACE}/output
cd reobf/minecraft

echo "Copying in extra Client files"
cp -rf ${WORKSPACE}/A1-zipStuff/* .
rm ./com/ForgeEssentials/util/lang/dummyForGithub
rm ./mcmod.info
mv mcmodCLIENT.info mcmod.info

echo "Creating Client package"
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-client-${MC}-${VERSION}.zip" ./com/ForgeEssentials/client/*
rm -rf ./com/ForgeEssentials/client;
rm ./mcmod.info
# ^ get it out of the way...

echo "Copying in extra files"
cp -rf ${WORKSPACE}/A1-zipStuff/mcmod.info .
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/com/ForgeEssentials/util/lang/* ./com/ForgeEssentials/util/lang/
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/forgeessentials_at.cfg .
rm ./com/ForgeEssentials/util/lang/dummyForGithub

echo "Creating server packages"
jar cvfm "${WORKSPACE}/output/${JOB_NAME}-core-${MC}-${VERSION}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/coremod/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* logo.png mcmod.info forgeessentials_at.cfg
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-modules-${MC}-${VERSION}.zip" ./com/ForgeEssentials/chat/* ./com/ForgeEssentials/economy/* ./com/ForgeEssentials/protection/* ./com/ForgeEssentials/snooper/* ./com/ForgeEssentials/WorldBorder/* ./com/ForgeEssentials/playerLogger/*
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-commands-${MC}-${VERSION}.zip" ./com/ForgeEssentials/commands/* 
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-WorldControl-${MC}-${VERSION}.zip" ./com/ForgeEssentials/WorldControl/* 
echo "Creating API package"
cd ${WORKSPACE}/output
mkdir api
cd api
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/com/ForgeEssentials/api/*  ./com/ForgeEssentials/api/
cd ..
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-API-src-${MC}-${VERSION}.zip" ./api/*  

echo "Creating ServerComplete package"
mkdir mods
mkdir coremods
cp -rf "${WORKSPACE}/output/${JOB_NAME}-core-${MC}-${VERSION}.jar" ./coremods/
cp -rf "${WORKSPACE}/output/${JOB_NAME}-modules-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/${JOB_NAME}-commands-${MC}-${VERSION}.zip" ./mods/
cp -rf "${WORKSPACE}/output/${JOB_NAME}-WorldControl-${MC}-${VERSION}.zip" ./mods/
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-ServerComplete-${MC}-${VERSION}.zip" ./coremods/* ./mods/*
#upload
