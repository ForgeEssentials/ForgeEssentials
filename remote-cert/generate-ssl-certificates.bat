@echo off
set PATH=$PATH;C:\Program Files\Java\jdk1.8.0_25\bin

echo Generate certificate
keytool ^
	-genkeypair ^
	-keystore FeRemote.jks ^
	-alias FeRemote ^
	-keyalg RSA ^
	-keysize 4096 ^
	-validity 360 ^
	-dname "CN=ForgeEssentials Remote, OU=ForgeEssentials, O=ForgeEssentials Team, L=International, ST=International, C=INT" ^
	-storepass feremote ^
	-keypass feremote

echo.
echo Create signing request
keytool ^
	-certreq ^
	-keystore FeRemote.jks ^
	-alias FeRemote ^
	-file FeRemote.csr ^
	-storepass feremote

echo.
echo Export certificate
keytool ^
	-export ^
	-keystore FeRemote.jks ^
	-alias FeRemote ^
	-file FeRemote.cer ^
	-storepass feremote

echo.
echo Print certificate information
keytool ^
	-printcert ^
	-file FeRemote.cer

echo.
echo Importing public certificate into public keystore
keytool ^
	-import ^
	-trustcacerts ^
	-keystore FeRemotePub.jks ^
	-alias FeRemote ^
	-file FeRemote.cer ^
	-storepass feremote

echo.
pause
