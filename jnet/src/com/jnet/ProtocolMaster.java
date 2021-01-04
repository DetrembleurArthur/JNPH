package com.jnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class ProtocolMaster implements Runnable, ProtocolEntity
{
    private final ArrayList<Class<?>> clientProtocolClasses;
    private final ArrayList<Class<?>> serverProtocolClasses;
    private final ArrayList<ProtocolServer> protocolServers;
    private final ArrayList<ProtocolClient> protocolClients;
    private final int id;

    private static final AtomicInteger counter = new AtomicInteger(0);
    
    public static ProtocolMaster launch(Class<?> ... protocolClasses)
    {
        ProtocolMaster protocolMaster = new ProtocolMaster(protocolClasses);
        protocolMaster.run();
        return protocolMaster;
    }

    private ProtocolMaster(Class<?>[] protocolClasses)
    {
    	clientProtocolClasses = new ArrayList<>();
    	serverProtocolClasses = new ArrayList<>();
        register(protocolClasses);
        protocolServers = new ArrayList<>();
        protocolClients = new ArrayList<>();
        id = counter.incrementAndGet();
    }

    private void register(Class<?> ... protocolClasses)
    {
        for(Class<?> protocolClass : protocolClasses)
        {
            if(protocolClass.isAnnotationPresent(ServerProtocol.class))
            {
                if(!serverProtocolClasses.contains(protocolClass))
                    serverProtocolClasses.add(protocolClass);
            }
            else if(protocolClass.isAnnotationPresent(ClientProtocol.class))
            {
                if(!clientProtocolClasses.contains(protocolClass))
                    clientProtocolClasses.add(protocolClass);
            }
            else
            {
                throw new IllegalArgumentException(protocolClass.getCanonicalName() + " is not annotated as a Protocol");
            }
        }
    }

    public ProtocolMaster registerAndLaunch(Class<?> protocolClass)
    {
        register(protocolClass);
        if(protocolClass.isAnnotationPresent(ServerProtocol.class))
        {
            protocolServers.add(launchServer(protocolClass));
        }
        else if(protocolClass.isAnnotationPresent(ClientProtocol.class))
        {
            protocolClients.add(launchClient(protocolClass));
        }
        return this;
    }

    private ProtocolServer launchServer(Class<?> protocolClass)
    {
        try
        {
            ProtocolServer protocolServer = new ProtocolServer(protocolClass, this);
            protocolServer.start();
            return protocolServer;
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.err(this, "starting server failed");
        }
        return null;
    }
    
    private void launchServers()
    {
    	for(Class<?> protocol : serverProtocolClasses)
        {
    		protocolServers.add(launchServer(protocol));
        }
    }

    private ProtocolClient launchClient(Class<?> protocolClass)
    {
        ProtocolClient protocolClient = new ProtocolClient(protocolClass);
        protocolClient.run();
        return protocolClient;
    }
    
    private void launchClients()
    {
    	for(Class<?> protocol : clientProtocolClasses)
        {
    		protocolClients.add(launchClient(protocol));
        }
    }

    @Override
    public void run()
    {
        launchServers();
        launchClients();
        Log.out(this, "has launch PotocolEntity instances");
    }

	public synchronized ArrayList<Class<?>> getClientProtocolClasses() {
		return clientProtocolClasses;
	}

	public synchronized ArrayList<Class<?>> getServerProtocolClasses() {
		return serverProtocolClasses;
	}

	public synchronized ArrayList<ProtocolServer> getProtocolServers() {
		return protocolServers;
	}

	public synchronized ArrayList<ProtocolClient> getProtocolClients() {
		return protocolClients;
	}
	
	public synchronized ProtocolServer getProtocolServer(Class<?> protocol)
	{
		for(ProtocolServer protocolServer : protocolServers)
		{
			if(protocolServer.getProtocol().equals(protocol))
			{
				return protocolServer;
			}
		}
		return null;
	}
	
	public synchronized ProtocolClient getProtocolClient(Class<?> protocol)
	{
		for(ProtocolClient protocolClient : protocolClients)
		{
			if(protocolClient.getProtocol().equals(protocol))
			{
				return protocolClient;
			}
		}
		return null;
	}

    public synchronized void redirect(Query query, ProtocolHandler from)
    {
        Log.out(this, "start broadcast from " + from.getEntityId());
        for(ProtocolServer protocolServer : protocolServers)
        {
            Log.out(from, "make a broadcast : " + query);
            protocolServer.redirect(query, from);
        }
    }

    public synchronized void redirect(Query query, ProtocolHandler from, String protocolName)
    {
        Log.out(this, "start broadcast from " + from.getEntityId());
        for(ProtocolServer protocolServer : protocolServers)
        {
            if(protocolServer.getOptions().getProperty("name").equals(protocolName))
            {
                Log.out(from, "make a broadcast : " + query);
                protocolServer.redirect(query, from);
            }
        }
    }

    @Override
    public String getEntityId()
    {
        return "PM(" + id + ")";
    }
}
