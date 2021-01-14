package com.jnet;

public abstract class ProtocolHandlerServer extends ProtocolHandler
{
    private final ProtocolServer protocolServer;
    
    public static ProtocolHandlerServer create(Object protocol, ProtocolServer server)
    {
        Options options = server.getOptions();
    	boolean pool = (boolean) options.get("pool");
    	if(pool)
    	{
    		return new PoolProtocolHandler(protocol, server);
    	}
    	else
    	{
    		return new OnDemandProtocolHandler(protocol, server);
    	}
    }

    public ProtocolHandlerServer(Object protocol, ProtocolServer sup)
    {
        super(protocol);
        protocolServer = sup;
    }
    
	public ProtocolServer getProtocolServer() {
		return protocolServer;
	}

    @Override
    public String getEntityId()
    {
        return "PH(S):" + (tunnel != null ? tunnel.getSocket().getInetAddress().getHostAddress() + ":" + tunnel.getSocket().getPort() + ":" + getProtocolName() : "");
    }
    
	@Override
	protected void handshake()
	{
		running.set(true);
        Query query = recv();
        if(query.getMode().equals(Query.Mode.DISCOVERY))
        {
            if(query.getType().equals("..."))
            {
                query.setType(getOptions().getProperty("name"));
                send(query);
            }
            else
            {
                if(query.getType().equals(getOptions().getProperty("name")))
                {
                    send(query);
                }
                else
                {
                    query.pack("error");
                    send(query);
                    running.set(false);
                }
            }
        }
        else
        {
            running.set(false);
        }
	}

	@Override
	protected void manageQueryModes(Query query)
	{
		switch(query.getMode())
		{
			case CLASSIC_BROADCAST:
				getProtocolServer().redirect(query.mode(Query.Mode.NORMAL), this);
				break;
			case GENERAL_BROADCAST:
				getProtocolServer().getMaster().redirect(query.mode(Query.Mode.NORMAL), this);
				break;
			case PROTOCOL_BROADCAST:
				getProtocolServer().getMaster().redirect(query.mode(Query.Mode.NORMAL), this, (String) query.getArgs().get(0));
				break;
			default:
				break;
		}
	}
}
