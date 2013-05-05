package com.ForgeEssentials.serverVote;

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

import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigServerVote extends ModuleConfigBase
{
	private static final String	category	= "ServerVote";

	private Configuration		config;

	public boolean				allowOfflineVotes;
	public String				msgAll		= "";
	public String				msgVoter	= "";
	public ArrayList<ItemStack>	freeStuff	= new ArrayList<ItemStack>();

	public File					keyFolder;

	public KeyPair				keyPair;
	public PrivateKey			privateKey;
	public PublicKey			publicKey;

	public String				hostname;
	public Integer				port;

    public boolean              flatfileLog;

	public ConfigServerVote(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);

		String subcat = category + ".Votifier";
		config.addCustomCategoryComment(subcat, "This is for votifier compatibility only.");

		hostname = config.get(subcat, "hostname", "").getString();
		port = config.get(subcat, "port", "8192").getInt();

		allowOfflineVotes = config.get(category, "allowOfflineVotes", true, "If false, votes of offline players will be canceled.").getBoolean(true);
		msgAll = config.get(category, "msgAll", "%player has voted for this server on %service.", "You can use color codes (&), %player and %service").getString();
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
				meta = Integer.parseInt(temp2[1]);
				temp = temp2[0];
			}

			int id = Integer.parseInt(temp);
			ItemStack stack = new ItemStack(id, amount, meta);

			OutputHandler.finer(stack);

			freeStuff.add(stack);
		}

		config.save();
		loadKeys();
	}

	@Override
	public void forceSave()
	{

	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		allowOfflineVotes = config.get(category, "allowOfflineVotes", true, "If false, votes of offline players will be canceled.").getBoolean(true);
		msgAll = config.get(category, "msgAll", "%player has voted for this server on %service.", "You can use color codes (&), %player and %service").getString();
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
                meta = Integer.parseInt(temp2[1]);
                temp = temp2[0];
            }

            int id = Integer.parseInt(temp);
            ItemStack stack = new ItemStack(id, amount, meta);

            OutputHandler.finer(stack);

            freeStuff.add(stack);
        }
		
		config.save();
		loadKeys();
	}

	private void loadKeys()
	{
		keyFolder = new File(ModuleServerVote.config.getFile().getParent(), "RSA");
		File publicFile = new File(keyFolder, "public.key");
		File privateFile = new File(keyFolder, "private.key");

		if (!keyFolder.exists() || !publicFile.exists() || !privateFile.exists())
		{
			try
			{
				OutputHandler.info("Generating RSA key pair...");

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

				OutputHandler.info("RSA key pair made!");
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
				OutputHandler.info("Loading RSA key pair...");

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
				OutputHandler.info("RSA key pair loaded!");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
