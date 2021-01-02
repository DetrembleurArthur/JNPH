package com.jnet;

import java.io.IOException;
import java.util.ArrayList;

public final class ProtocolMaster implements Runnable
{
    private ArrayList<Class<?>> clientProtocolClasses;
    private ArrayList<Class<?>> serverProtocolClasses;
    private ArrayList<ProtocolServer> protocolServers;
    private ArrayList<ProtocolClient> protocolClients;
    
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
        for(Class<?> protocolClass : protocolClasses)
        {
            if(protocolClass.isAnnotationPresent(ServerProtocol.class))
            {
            	serverProtocolClasses.add(protocolClass);
            }
            else if(protocolClass.isAnnotationPresent(ClientProtocol.class))
            {
            	clientProtocolClasses.add(protocolClass);
            }
            else
            {
                throw new IllegalArgumentException(protocolClass.getCanonicalName() + " is not annotated as a Protocol");
            }
        }
        protocolServers = new ArrayList<>();
        protocolClients = new ArrayList<>();
    }
    
    private void launchServers()
    {
    	for(Class<?> protocol : serverProtocolClasses)
        {
    		try
            {
                ProtocolServer protocolServer = new ProtocolServer(protocol);
                protocolServers.add(protocolServer);
                protocolServer.start();
            } catch (IOException e)
            {
            	e.printStackTrace();
            	Log.err("ProtocolMaster", "starting server failed");
            }
        }
    }
    
    private void launchClients()
    {
    	for(Class<?> protocol : clientProtocolClasses)
        {
    		ProtocolClient protocolClient = new ProtocolClient(protocol);
			protocolClient.run();
			protocolClients.add(protocolClient);
        }
    }

    @Override
    public void run()
    {
        launchServers();
        launchClients();
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
}
