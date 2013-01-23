package com.ForgeEssentials.snooper.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.snooper.ModuleSnooper;
import com.ForgeEssentials.util.OutputHandler;

public class VoteResponce extends Response
{
	private File keyFolder;
	private boolean debug = false;
	
	private KeyPair keyPair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	@Override
	public String getResponceString(DatagramPacket packet)
	{
		try
		{
			String encr = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
			System.out.println("encr: " + encr);
			
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decodedBytes = cipher.doFinal(encr.getBytes());
			String decoded = new String(decodedBytes);
			System.out.println("decoded: " + decoded);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		    
		return TextFormatter.toJSON(new String[] {"Handled"});
	}

	@Override
	public String getName()
	{
		return "VoteResponce";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		debug = config.get(category, "debug", false, "This might print sensitive info in the server log!").getBoolean(false);
		loadKeys();
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		config.get(category, "debug", false).value = "" + debug;
	}

	private void loadKeys()
	{
		keyFolder = new File(ModuleSnooper.configSnooper.getFile().getParent(), "RSA");
		File publicFile = new File(keyFolder, "public.key");
		File privateFile = new File(keyFolder, "private.key");
		
		if(!keyFolder.exists() || !publicFile.exists() || !privateFile.exists())
		{
			try
			{
				OutputHandler.SOP("Generating RSA key pair...");
				
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
				
				OutputHandler.SOP("RSA key pair made!");
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
				OutputHandler.SOP("Loading RSA key pair...");
				
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
				OutputHandler.SOP("RSA key pair loaded!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
