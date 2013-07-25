VERSION="SNAPSHOT"
MC="1.5.2"
VERSION="`head -n 1 VERSION.TXT`"
VERSIONBUILD="${VERSION}.${BAMBOO_BUILDNUMBER}"
MC="`head -2 VERSION.TXT | tail -1 VERSION.TXT`"
echo "Building ForgeEssentials ${VERSIONBUILD} for MC ${MC} in $1"

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
cp -rf $1/src/FE_SRC_COMMON/forgeessentials_at.cfg $1/forge/accesstransformers/

echo "Installing Forge..."
./install.sh
cd mcp

echo "Copying ForgeEssentials and related libraries into MCP..."
cd src
cp -rf $1/src/FE_SRC_COMMON/* ./minecraft/
cp -rf $1/src/FE_SRC_CLIENT/* ./minecraft/
cd ..
cd lib
cp -rf $1/lib/* .
cd ..

echo "Injecting version number into places"
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' $1/resources/server/mcmod.info
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' $1/resources/client/mcmod.info
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' $1/resources/api/FEAPIReadme.txt
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' $1/resources/servercomplete/FEReadme.txt
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' $1/resources/client/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' $1/resources/server/mcmod.info
sed -i 's/@MC@/'${MC}'/g' $1/resources/client/mcmod.info
sed -i 's/@MC@/'${MC}'/g' $1/resources/api/FEAPIReadme.txt
sed -i 's/@MC@/'${MC}'/g' $1/resources/client/FEReadme.txt
sed -i 's/@MC@/'${MC}'/g' $1/resources/servercomplete/FEReadme.txt
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' src/minecraft/com/ForgeEssentials/core/preloader/FEModContainer.java
sed -i 's/@VERSIONBUILD@/'${VERSIONBUILD}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java
sed -i 's/@BETA@/'${BETA}'/g' src/minecraft/com/ForgeEssentials/core/preloader/FEModContainer.java
sed -i 's/@BETA@/'${BETA}'/g' src/minecraft/com/ForgeEssentials/client/ForgeEssentialsClient.java

echo "Recompiling..."
./recompile.sh

echo "Reobfuscating..."
./reobfuscate_srg.sh

# create this ahead of time...
mkdir $1/output
cd reobf/minecraft

echo "Creating Client package"
cp -rf $1/resources/client/* .
jar cvf "$1/output/ForgeEssentials-client-${MC}-${VERSIONBUILD}.jar" ./com/ForgeEssentials/client/* mcmod.info logo.png FEReadme.txt LICENSE.TXT
rm -rf ./com/ForgeEssentials/client
rm -rf ./*.info

echo "Copying in extra files for core"
cp -rf $1/resources/server/* .

echo "Creating server packages"
jar cvfm "$1/output/ForgeEssentials-core-${MC}-${VERSIONBUILD}.jar" ./META-INF/MANIFEST.MF ./com/ForgeEssentials/core/* ./com/ForgeEssentials/permission/* ./com/ForgeEssentials/util/* ./com/ForgeEssentials/data/* logo.png mcmod.info forgeessentials_at.cfg ./com/ForgeEssentials/api/*.class ./com/ForgeEssentials/api/permissions ./com/ForgeEssentials/api/packetInspector ./com/ForgeEssentials/api/json
zip -r9 "$1/output/ForgeEssentials-auth-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/auth/*
zip -r9 "$1/output/ForgeEssentials-backups-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/backup/*
zip -r9 "$1/output/ForgeEssentials-chat-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/chat/* 
zip -r9 "$1/output/ForgeEssentials-commands-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/commands/* ./com/ForgeEssentials/api/commands
zip -r9 "$1/output/ForgeEssentials-economy-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/economy/* 
zip -r9 "$1/output/ForgeEssentials-playerlogger-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/playerLogger/* 
zip -r9 "$1/output/ForgeEssentials-protection-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/protection/* 
zip -r9 "$1/output/ForgeEssentials-questioner-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/questioner/* ./com/ForgeEssentials/api/questioner 
zip -r9 "$1/output/ForgeEssentials-snooper-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/snooper/*  ./com/ForgeEssentials/api/snooper
zip -r9 "$1/output/ForgeEssentials-servervote-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/serverVote/*
zip -r9 "$1/output/ForgeEssentials-scripting-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/scripting/*
zip -r9 "$1/output/ForgeEssentials-tickets-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/tickets/* 
zip -r9 "$1/output/ForgeEssentials-worldborder-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/WorldBorder/*
zip -r9 "$1/output/ForgeEssentials-WorldControl-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/WorldControl/*
zip -r9 "$1/output/ForgeEssentials-afterlife-${MC}-${VERSIONBUILD}.zip" ./com/ForgeEssentials/afterlife/*
rm -rf ./*.info ./*.txt logo.png

echo "Creating ServerComplete package"
cd $1/output
cp -rf $1/resources/servercomplete/* .

mkdir mods
mkdir coremods
# coremod.. then alphebetical order please...
cp -rf "$1/output/ForgeEssentials-core-${MC}-${VERSIONBUILD}.jar" ./coremods/
cp -rf "$1/output/ForgeEssentials-auth-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-backups-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-chat-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-commands-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-economy-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-playerlogger-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-protection-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-questioner-${MC}-${VERSIONBUILD}.zip"  ./mods/
cp -rf "$1/output/ForgeEssentials-snooper-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-servervote-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-scripting-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-tickets-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-worldborder-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-WorldControl-${MC}-${VERSIONBUILD}.zip" ./mods/
cp -rf "$1/output/ForgeEssentials-afterlife-${MC}-${VERSIONBUILD}.zip" ./mods/
zip -r9 "$1/output/ForgeEssentials-ServerComplete-${MC}-${VERSION}.zip" ./coremods/* ./mods/* FEReadme.txt HowToGetFEsupport.txt LICENSE.TXT

echo "Cleaning up"
rm -rf ./mods/*
rm -rf ./coremods/*
rm -rf "$1/output/ForgeEssentials-auth-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-backups-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-chat-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-core-${MC}-${VERSIONBUILD}.jar" 
rm -rf "$1/output/ForgeEssentials-commands-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-economy-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-playerlogger-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-protection-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-questioner-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-servervote-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-scripting-${MC}-${VERSIONBUILD}.zip"
rm -rf "$1/output/ForgeEssentials-snooper-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-tickets-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-worldborder-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-WorldControl-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/ForgeEssentials-afterlife-${MC}-${VERSIONBUILD}.zip" 
rm -rf "$1/output/FEReadme.txt"
rm -rf "$1/output/HowToGetFEsupport.txt"
rm -rf "$1/output/LICENSE.TXT"

echo "Creating API package"
cd $1/src/FE_SRC_COMMON
cp -f $1/resources/api/* . .
zip -r9 "$1/output/ForgeEssentials-API-r2-${VERSIONBUILD}.zip" ./com/ForgeEssentials/api/* FEAPIReadme.txt LICENSE.TXT

#upload
