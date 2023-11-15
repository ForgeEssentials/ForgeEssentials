package com.forgeessentials.servervote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class ConfigServerVote
{
    private static final String category = "ServerVote";
    private static final String subcat = category + "_Votifier";

    public static boolean allowOfflineVotes;
    public static String msgAll = "";
    public static String msgVoter = "";

    public static File keyFolder;

    public static KeyPair keyPair;
    public static PrivateKey privateKey;
    public static PublicKey publicKey;

    public static String hostname;
    public static Integer port;
    public static String token;
    public static boolean allowClassic;

    static ForgeConfigSpec.ConfigValue<String> FEhostname;
    static ForgeConfigSpec.IntValue FEport;
    static ForgeConfigSpec.BooleanValue FEallowOfflineVotes;
    static ForgeConfigSpec.ConfigValue<String> FEmsgAll;
    static ForgeConfigSpec.ConfigValue<String> FEmsgVoter;
    static ForgeConfigSpec.ConfigValue<String> FEtoken;
    static ForgeConfigSpec.BooleanValue FEallowClassic;

    public static void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push(category);
        FEallowOfflineVotes = BUILDER.comment("If false, votes of offline players will be canceled.")
                .define("allowOfflineVotes", true);
        FEmsgAll = BUILDER.comment("You can use color codes (&), %player and %service").define("msgAll",
                "%player has voted for this server on %service.");
        FEmsgVoter = BUILDER.comment("You can use color codes (&), %player and %service").define("msgVoter",
                "Thanks for voting for our server!");
        BUILDER.pop();

        BUILDER.comment("This is for votifier compatibility only.").push(subcat);
        FEhostname = BUILDER.comment("Set this to the hostname (or IP address) of your server.").define("hostname", "");
        FEport = BUILDER.comment("The port which the vote receiver should listen on.").defineInRange("port", 8192, 0,
                65535);

        FEtoken = BUILDER.comment("Token for Votifier V2 Protocol").define("token", "");

        FEallowClassic = BUILDER.comment("Disable Classic / V1 and only use V2").define("allowClassic", true);
        BUILDER.pop();
    }

    public static void bakeConfig(boolean reload)
    {
        hostname = FEhostname.get();
        port = FEport.get();

        allowOfflineVotes = FEallowOfflineVotes.get();
        msgAll = FEmsgAll.get();
        msgVoter = FEmsgVoter.get();

        token = FEtoken.get();
        allowClassic = FEallowClassic.get();

        loadKeys();

        if (reload)
        {
            try
            {
                ModuleServerVote.votifier.shutdown();
                ModuleServerVote.votifier = new VoteReceiver(ConfigServerVote.hostname, ConfigServerVote.port, ConfigServerVote.token);
                ModuleServerVote.votifier.start();
            }
            catch (Exception e1)
            {
                LoggingHandler.felog.error("Error closing Votifier compat thread.");
                e1.printStackTrace();
            }
        }
    }

    private static void loadKeys()
    {
        ConfigServerVote.keyFolder = new File(ModuleServerVote.moduleDir, "RSA");
        File publicFile = new File(keyFolder, "public.key");
        File privateFile = new File(keyFolder, "private.key");

        if (!keyFolder.exists() || !publicFile.exists() || !privateFile.exists())
        {
            try
            {
                LoggingHandler.felog.info("Generating RSA key pair...");

                keyFolder.mkdirs();
                KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
                RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
                keygen.initialize(spec);
                keyPair = keygen.generateKeyPair();
                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();

                X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());
                FileOutputStream out = new FileOutputStream(publicFile);
                out.write(Base64.getEncoder().encode(publicSpec.getEncoded()));
                out.close();

                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
                out = new FileOutputStream(privateFile);
                out.write(Base64.getEncoder().encode(privateSpec.getEncoded()));
                out.close();

                LoggingHandler.felog.info("RSA key pair made!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                LoggingHandler.felog.info("Loading RSA key pair...");

                FileInputStream in = new FileInputStream(publicFile);
                byte[] encodedPublicKey = new byte[(int) publicFile.length()];
                in.read(encodedPublicKey);
                encodedPublicKey = Base64.getDecoder().decode(new String(encodedPublicKey));
                in.close();

                in = new FileInputStream(privateFile);
                byte[] encodedPrivateKey = new byte[(int) privateFile.length()];
                in.read(encodedPrivateKey);
                encodedPrivateKey = Base64.getDecoder().decode(new String(encodedPrivateKey));
                in.close();

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
                publicKey = keyFactory.generatePublic(publicKeySpec);
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                privateKey = keyFactory.generatePrivate(privateKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                LoggingHandler.felog.info("RSA key pair loaded!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
