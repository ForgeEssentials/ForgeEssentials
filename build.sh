VERSION="0.1.0.${BUILD_NUMBER}"
MC="1.4.6"

echo "Downloading Forge..."
wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip 
unzip minecraftforge-src-*.zip
rm minecraftforge-src-*.zip
cd forge

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

echo "Recompiling..."
bash ./recompile.sh

echo "Reobfuscating..."
bash ./reobfuscate.sh

echo "Copying in extra files"

cd reobf/minecraft
cp -rf ${WORKSPACE}/A1-zipStuff/* .
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/com/ForgeEssentials/util/lang/* ./com/ForgeEssentials/util/lang/
rm ./com/ForgeEssentials/util/lang/dummyForGithub

echo "injecting version into mcmod.info"
sed -i 's/@build@/'${BUILD_NUMBER}'/g' mcmod.info

echo "Creating distribution packages"
jar cvfm "${WORKSPACE}/${JOB_NAME}-core-${MC}-${VERSION}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/coremod/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* ./com/ForgeEssentials/client/core/* logo.png mcmod.info
zip -r9 "${WORKSPACE}/${JOB_NAME}-modules-${MC}-${VERSION}.zip" ./com/ForgeEssentials/client/CUI/* ./com/ForgeEssentials/chat/* ./com/ForgeEssentials/commands/* ./com/ForgeEssentials/economy/* ./com/ForgeEssentials/playerLogger/* ./com/ForgeEssentials/protection/* ./com/ForgeEssentials/WorldBorder/* ./com/ForgeEssentials/WorldControl/* ./com/ForgeEssentials/backup/* 

#upload
