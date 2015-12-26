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

import javax.xml.bind.DatatypeConverter;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.servervote.Votifier.VoteReceiver;
import com.forgeessentials.util.output.LoggingHandler;

public class ConfigServerVote extends ConfigLoaderBase
{
    private static final String category = "ServerVote";

    public static boolean allowOfflineVotes;
    public static String msgAll = "";
    public static String msgVoter = "";

    public File keyFolder;

    public KeyPair keyPair;
    public static PrivateKey privateKey;
    public PublicKey publicKey;

    public static String hostname;
    public static Integer port;

    @Override
    public void load(Configuration config, boolean isReload)
    {
        String subcat = category + ".Votifier";
        config.addCustomCategoryComment(subcat, "This is for votifier compatibility only.");

        hostname = config.get(subcat, "hostname", "", "Set this to the hostname (or IP address) of your server.").getString();
        port = config.get(subcat, "port", "8192", "The port which the vote receiver should listen on.").getInt();

        allowOfflineVotes = config.get(category, "allowOfflineVotes", true, "If false, votes of offline players will be canceled.").getBoolean(true);
        msgAll = config.get(category, "msgAll", "%player has voted for this server on %service.", "You can use color codes (&), %player and %service")
                .getString();
        msgVoter = config.get(category, "msgVoter", "Thanks for voting for our server!", "You can use color codes (&), %player and %service").getString();

        loadKeys();

        if (isReload)
        {
            try
            {
                ModuleServerVote.votifier.shutdown();
                ModuleServerVote.votifier = new VoteReceiver(ConfigServerVote.hostname, ConfigServerVote.port);
                ModuleServerVote.votifier.start();
            }
            catch (Exception e1)
            {
                LoggingHandler.felog.error("Error closing Votifier compat thread.");
                e1.printStackTrace();
            }
        }
    }

    private void loadKeys()
    {
        keyFolder = new File(ModuleServerVote.moduleDir, "RSA");
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
                out.write(DatatypeConverter.printBase64Binary(publicSpec.getEncoded()).getBytes());
                out.close();

                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
                out = new FileOutputStream(privateFile);
                out.write(DatatypeConverter.printBase64Binary(privateSpec.getEncoded()).getBytes());
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
                encodedPublicKey = DatatypeConverter.parseBase64Binary(new String(encodedPublicKey));
                in.close();

                in = new FileInputStream(privateFile);
                byte[] encodedPrivateKey = new byte[(int) privateFile.length()];
                in.read(encodedPrivateKey);
                encodedPrivateKey = DatatypeConverter.parseBase64Binary(new String(encodedPrivateKey));
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
