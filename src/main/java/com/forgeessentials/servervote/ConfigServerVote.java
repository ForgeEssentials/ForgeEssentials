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
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.registry.GameData;

public class ConfigServerVote extends ConfigLoaderBase
{
    private static final String category = "ServerVote";

    public static boolean allowOfflineVotes;
    public static String msgAll = "";
    public static String msgVoter = "";
    public static ArrayList<ItemStack> freeStuff = new ArrayList<ItemStack>();

    public File keyFolder;

    public KeyPair keyPair;
    public static PrivateKey privateKey;
    public PublicKey publicKey;

    public static String hostname;
    public static Integer port;

    public boolean flatfileLog;

    @Override
    public void load(Configuration config, boolean isReload)
    {
        String subcat = category + ".Votifier";
        config.addCustomCategoryComment(subcat, "This is for votifier compatibility only.");

        hostname = config.get(subcat, "hostname", "").getString();
        port = config.get(subcat, "port", "8192").getInt();

        allowOfflineVotes = config.get(category, "allowOfflineVotes", true, "If false, votes of offline players will be canceled.").getBoolean(true);
        msgAll = config.get(category, "msgAll", "%player has voted for this server on %service.", "You can use color codes (&), %player and %service")
                .getString();
        msgVoter = config.get(category, "msgVoter", "Thanks for voting for our server!", "You can use color codes (&), %player and %service").getString();

        flatfileLog = config.get(category, "flatFileLog", true, "Log the votes in \"votes.log\"").getBoolean(true);

        String[] tempArray = config.get(category, "rewards", new String[] {}, "Format is like this: [amount]x<id>[:meta]").getStringList();

        freeStuff.clear();
        for (String temp : tempArray)
        {
            int amount = 1;
            int meta = 0;

            if (temp.contains("x"))
            {
                String[] temp2 = temp.split("x");
                amount = Integer.parseInt(temp2[0]);
                temp = temp2[1];
            }

            if (temp.contains(":"))
            {
                String[] temp2 = temp.split(":");
                meta = Integer.parseInt(temp2[2]);
                temp = temp2[0] + ":" + temp2[1];
            }

            Item item = GameData.getItemRegistry().getObject(temp);
            
            if (item == null) 
            {
                LoggingHandler.felog.warn("Found invalid item:" + temp);
                continue;
            }
            
            ItemStack stack = new ItemStack(item, amount, meta);
            
            if (stack == null) 
            {
                continue;
            }

            LoggingHandler.felog.debug("Added reward item stack: " + stack.getUnlocalizedName());
            freeStuff.add(stack);
        }

        loadKeys();
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
