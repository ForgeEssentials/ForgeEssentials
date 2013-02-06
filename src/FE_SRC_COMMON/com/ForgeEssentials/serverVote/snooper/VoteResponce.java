package com.ForgeEssentials.serverVote.snooper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.api.snooper.VoteEvent;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.serverVote.ModuleServerVote;
import com.ForgeEssentials.util.OutputHandler;

public class VoteResponce extends Response
{	
	@Override
	public String getResponceString(DatagramPacket packet)
	{
		try
		{
			String decoded;
			
			try
			{
				String encr = new String(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, ModuleServerVote.config.privateKey);
				byte[] decodedBytes = cipher.doFinal(Arrays.copyOfRange(packet.getData(), 11, packet.getLength()));
				decoded = new String(decodedBytes);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return TextFormatter.toJSON(new String[] {"Failed", TextFormatter.toJSON(new String[] {"Encryption"})});
			}
			
			VoteEvent vote = new VoteEvent(decoded);
				
			if(!vote.isSane()) return TextFormatter.toJSON(new String[] {"Failed", TextFormatter.toJSON(new String[] {"notSane"})});
			
			OutputHandler.fine("Vote: " + vote);
			
			try
			{
				MinecraftForge.EVENT_BUS.post(vote);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return TextFormatter.toJSON(new String[] {"Failed", TextFormatter.toJSON(new String[] {e.getMessage()})});
			}
			
			if(vote.isCanceled())
			{
				return TextFormatter.toJSON(new String[] {"Failed", TextFormatter.toJSON(vote.getFeedback())});
			}
			else
			{
				return TextFormatter.toJSON(new String[] {"Success"});
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getName()
	{
		return "VoteResponce";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		
	}
}
