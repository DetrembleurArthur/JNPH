package com.jnet;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;

public class Tunnel
{
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Tunnel(Socket socket)
    {
        try
        {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e)
        {
            Log.err("Tunnel", "unable to init object streams");
        }
        try
        {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e)
        {
        	Log.err("Tunnel", "unable to init buffered streams");
        }
        this.socket = socket;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void sendbuff(Query query)
    {
        try
        {
            bufferedWriter.write(query.toString() + "\n");
            bufferedWriter.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.err("Tunnel", "unable to send [" + query + "]");
        }
    }

    public Query recvbuff()
    {
        try
        {
            String content = bufferedReader.readLine();
            return new Query(content, true);
        } catch (Exception e)
        {
        	e.printStackTrace();
        	Log.err("Tunnel", "unable to recieve buffer");
        }
        return null;
    }

    public void sendobj(Serializable serializable)
    {
        try
        {
            objectOutputStream.writeObject(serializable);
            objectOutputStream.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.err("Tunnel", "unable to send [" + serializable + "]");
        }
    }

    public <T extends Serializable> T recvobj()
    {
        try
        {
            return (T) objectInputStream.readObject();
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.err("Tunnel", "unable to recieve an object");
        }
        return null;
    }

    public BufferedReader getBufferedReader()
    {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter()
    {
        return bufferedWriter;
    }

    public ObjectOutputStream getObjectOutputStream()
    {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream()
    {
        return objectInputStream;
    }
}
