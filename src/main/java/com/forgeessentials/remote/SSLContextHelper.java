package com.forgeessentials.remote;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLContextHelper
{

    private SSLContext sslCtx;

    public SSLContext getSSLCtx()
    {
        return sslCtx;
    }

    public SSLContextHelper()
    {
        try
        {
            sslCtx = SSLContext.getDefault();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public void loadSSLCertificate(InputStream keystore, String storepass, String keypass) throws IOException, GeneralSecurityException
    {
        if (keystore == null)
            throw new IOException("Invalid keystore");

        // Load KeyStore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(keystore, storepass.toCharArray());

        // Init KeyManager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keypass.toCharArray());

        // Init TrustManager
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        // Init SSLContext
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        sslCtx = ctx;
    }

    public void loadSSLCertificate(String filename, String storepass, String keypass) throws IOException, GeneralSecurityException
    {
        loadSSLCertificate(new FileInputStream(filename), storepass, keypass);
    }

}
