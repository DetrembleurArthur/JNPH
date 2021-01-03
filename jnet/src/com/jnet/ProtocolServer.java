package com.jnet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolServer extends Thread implements ProtocolEntity
{
    private final Object poolMonitor;
    private Class<?> protocol;
    private ServerSocket serverSocket;
    private boolean running;
    private ArrayList<ProtocolHandler> protocolHandlers;
    private Socket socket=null;
    
    public ProtocolServer(Class<?> protocol) throws IOException
    {
        running = false;
        this.protocol = protocol;
        if(protocol.getAnnotation(ServerProtocol.class).ip().isEmpty())
        	serverSocket = new ServerSocket(protocol.getAnnotation(ServerProtocol.class).port());
        else
        	serverSocket = new ServerSocket(protocol.getAnnotation(ServerProtocol.class).port(), 50, InetAddress.getByName(protocol.getAnnotation(ServerProtocol.class).ip()));
        protocolHandlers = new ArrayList<>();
        poolMonitor = new Object();
    }

    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }

    public synchronized boolean isRunning()
    {
        return running;
    }

    public synchronized ArrayList<ProtocolHandler> getProtocolHandlers()
    {
        return protocolHandlers;
    }

    public synchronized Class<?> getProtocol()
    {
        return protocol;
    }

    public void initPool()
    {
    	int poolSize = protocol.getAnnotation(ServerProtocol.class).maxClients();
        for(int i = 0; i < poolSize; i++)
        {
            try
            {
                ProtocolHandler protocolHandler = ProtocolHandler.create(protocol.getConstructor().newInstance(), this);
                protocolHandlers.add(protocolHandler);
                protocolHandler.start();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
            {
                Log.err(this, "unable to start pool n°" + (i+1));
                e.printStackTrace();
            }
        }
    }
    
    public String getManagedProtocolName()
    {
    	return getProtocol().getAnnotation(ServerProtocol.class).name();
    }

    @Override
    public void run()
    {
    	boolean pool = protocol.getAnnotation(ServerProtocol.class).pool();
        running = true;
        if(pool)
        	initPool();
        while(running)
        {
            try
            {
                Log.out(this,"listen on " + serverSocket.getInetAddress().getHostAddress());
                Socket cliSocket = serverSocket.accept();
                Log.out(this, "client accepted on " + cliSocket.getInetAddress().getHostName() + ":" + cliSocket.getPort());
                if(pool)
                {
                	synchronized (getPoolMonitor())
                	{
                		setSocket(cliSocket);
                        getPoolMonitor().notify();
                        Log.out(this, "notify");
					}
                }
                else
                {
                	if(protocol.getAnnotation(ServerProtocol.class).maxClients() == protocolHandlers.size())
                	{
                		cliSocket.close();
                		continue;
                	}
                    Object protocolInstance = protocol.getConstructor().newInstance();
                    ProtocolHandler protocolHandler = ProtocolHandler.create(protocol.getConstructor().newInstance(), this);
                    protocolHandler.initNet(cliSocket);
                    protocolHandlers.add(protocolHandler);
                    protocolHandler.start();
                }
            } catch (IOException e)
            {
            	e.printStackTrace();
            	Log.err(this, "connection error");
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
            {
            	e.printStackTrace();
            	Log.err(this, "protocol instantiation error");
            }
        }
    }

    public Object getPoolMonitor()
    {
        return poolMonitor;
    }

    public synchronized Socket getSocket()
    {
        return socket;
    }

    public synchronized void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public String getEntityId()
    {
        return "PS:" + getManagedProtocolName() + ":" + getServerSocket().getInetAddress().getHostAddress() + ":" + getServerSocket().getLocalPort();
    }
}
