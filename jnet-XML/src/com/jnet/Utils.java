package com.jnet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.Security;

public class Utils
{
    static
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static SSLServerSocket generateServerSSL(String keystorePath, String keystorePswd, String keyPswd, int port, String ip)
    {
        try
        {
            KeyStore ServerKs = KeyStore.getInstance("JKS");
            String FICHIER_KEYSTORE = keystorePath;
            char[] PASSWD_KEYSTORE = keystorePswd.toCharArray();
            FileInputStream ServerFK = new FileInputStream (FICHIER_KEYSTORE);
            ServerKs.load(ServerFK, PASSWD_KEYSTORE);
            SSLContext SslC = SSLContext.getInstance("SSLv3");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            char[] PASSWD_KEY = keyPswd.toCharArray();
            kmf.init(ServerKs, PASSWD_KEY);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ServerKs);
            SslC.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLServerSocketFactory SslSFac= SslC.getServerSocketFactory();
            SSLServerSocket sslSSocket;
            if(ip.isEmpty())
                sslSSocket = (SSLServerSocket) SslSFac.createServerSocket(port);
            else
                sslSSocket = (SSLServerSocket) SslSFac.createServerSocket(port, 50, InetAddress.getByName(ip));
            sslSSocket.setNeedClientAuth(true);
            return sslSSocket;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static SSLSocket generateClientSSL(String keystorePath, String keystorePswd, String keyPswd, int port, String ip)
    {
        SSLSocket sslSocket = null;
        BufferedReader dis=null;
        BufferedWriter dos=null;
        try
        {
            KeyStore ServerKs = KeyStore.getInstance("JKS");
            String FICHIER_KEYSTORE = keystorePath;
            char[] PASSWD_KEYSTORE = keystorePswd.toCharArray();
            FileInputStream ServerFK = new FileInputStream (FICHIER_KEYSTORE);
            ServerKs.load(ServerFK, PASSWD_KEYSTORE);
            SSLContext SslC = SSLContext.getInstance("SSLv3");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            char[] PASSWD_KEY = keyPswd.toCharArray();
            kmf.init(ServerKs, PASSWD_KEY);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ServerKs);
            SslC.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLSocketFactory SslSFac= SslC.getSocketFactory();
            sslSocket = (SSLSocket) SslSFac.createSocket(InetAddress.getByName(ip), port);
            return sslSocket;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
