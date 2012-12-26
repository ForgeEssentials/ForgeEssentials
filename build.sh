echo "Downloading Forge..."
wget http://files.minecraftforge.net/minecraftforge/minecraftforge-src-latest.zip
unzip minecraftforge-src-*.zip
cd forge

echo "Installing Forge..."
bash ./install.sh
cd mcp

echo "Copying ${JOB_NAME} into MCP... DOUBLE!!!"
cd src
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/* ./minecraft/
cp -rf ${WORKSPACE}/src/FE_SRC_CLIENT/* ./minecraft/
cd ..

echo "adding in libraries..."
cd lib
cp -rf ${WORKSPACE}/lib/* .
cd ..

echo "Recompiling..."
bash ./recompile.sh

echo "Reobfuscating..."
bash ./reobfuscate.sh

echo "Creating server package..."

cd reobf/minecraft
cp -rf ${WORKSPACE}/A1-zipStuff/* .
cp -rf ${WORKSPACE}/src/FE_SRC_COMMON/com/ForgeEssentials/util/lang/* ./com/ForgeEssentials/util/lang/
rm ./com/ForgeEssentials/util/lang/dummyForGithub

jar cvfm "${WORKSPACE}/${JOB_NAME}.jar" ./META-INF/MANIFEST.MF *
