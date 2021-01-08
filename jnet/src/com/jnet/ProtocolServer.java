package com.jnet;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolServer extends Thread implements ProtocolEntity
{
    private final Object poolMonitor;
    private final Class<?> protocol;
    private final ServerSocket serverSocket;
    private boolean running;
    private final ArrayList<ProtocolHandler> protocolHandlers;
    private Socket socket=null;
    private final Options options;
    private final ProtocolMaster master;
    
    public ProtocolServer(Class<?> protocol, ProtocolMaster master) throws IOException
    {
        options = ProtocolEntity.getOptions(protocol);
        this.master = master;
        running = false;
        this.protocol = protocol;
        assert options != null;
        if((Boolean)options.get("ssl"))
        {
            serverSocket = Utils
                    .generateServerSSL(
                            "resources/makecert/server_keystore",
                            "server_keystore",
                            "server_keystore",
                            (Integer) options.get("port"), options.getProperty("ip"));
            Log.out(this, "SSL activated");
        }
        else
        {
            if(options.getProperty("ip").isEmpty())
            {
                serverSocket = new ServerSocket((Integer) options.get("port"));
            }
            else
            {
                serverSocket = new ServerSocket((Integer) options.get("port"), 50, InetAddress.getByName(options.getProperty("ip")));
            }
        }

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
    	int poolSize = (int) options.get("maxClients");
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
    	return options.getProperty("name");
    }

    @Override
    public void run()
    {
    	boolean pool = (boolean) options.get("pool");
        running = true;
        if(pool)
        	initPool();
        while(running)
        {
            try
            {
                Log.out(this,"listen on " + serverSocket.getInetAddress().getHostAddress());
                Socket cliSocket = serverSocket.accept();
                if((boolean)options.get("ssl"))
                {
                    Log.out(this, "start SSL handshake");
                    ((SSLSocket)cliSocket).startHandshake();
                }
                Log.out(this, "client accepted on " + cliSocket.getInetAddress().getHostAddress() + ":" + cliSocket.getPort());
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
                	if((int)options.get("maxClients") == protocolHandlers.size())
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

    public synchronized void redirect(Query query, ProtocolHandler from)
    {
        Log.out(this, "start broadcast from " + from.getEntityId());
        for(ProtocolHandler protocolHandler : protocolHandlers)
        {
            if(protocolHandler != from)
            {
                Log.out(from, "make a broadcast : " + query);
                protocolHandler.send(query);
            }
        }
    }

    public synchronized ProtocolMaster getMaster()
    {
        return master;
    }

    @Override
    public String getEntityId()
    {
        return "PS:" + getManagedProtocolName() + ":" + getServerSocket().getInetAddress().getHostAddress() + ":" + getServerSocket().getLocalPort();
    }

    @Override
    public Options getOptions()
    {
        return options;
    }
}
