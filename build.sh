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
sed -i 's/@VERSION@/'${VERSION}'/g' src/minecraft/com/ForgeEssentials/core/ForgeEssentials.java
sed -i 's/@VERSION@/'${VERSION}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java

echo "Recompiling..."
bash ./recompile.sh

echo "Reobfuscating..."
bash ./reobfuscate.sh

echo "Copying in extra files"
cd reobf/minecraft
cp -rf ${WORKSPACE}/A1-zipStuff/* .
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/com/ForgeEssentials/util/lang/* ./com/ForgeEssentials/util/lang/
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/forgeessentials_at.cfg .
rm ./com/ForgeEssentials/util/lang/dummyForGithub

echo "Creating distribution packages"
mkdir ${WORKSPACE}/output
jar cvfm "${WORKSPACE}/output/${JOB_NAME}-core-${MC}-${VERSION}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/coremod/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* logo.png mcmod.info forgeessentials_at.cfg
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-modules-${MC}-${VERSION}.zip" ./com/ForgeEssentials/chat/* ./com/ForgeEssentials/commands/* ./com/ForgeEssentials/economy/* ./com/ForgeEssentials/playerLogger/* ./com/ForgeEssentials/protection/* ./com/ForgeEssentials/WorldBorder/* ./com/ForgeEssentials/WorldControl/* ./com/ForgeEssentials/backup/* ./com/ForgeEssentials/snooper/* 
zip -r9 "${WORKSPACE}/output/${JOB_NAME}-client-${MC}-${VERSION}.zip" ./com/ForgeEssentials/client/*
cd ${WORKSPACE}/output
#upload
