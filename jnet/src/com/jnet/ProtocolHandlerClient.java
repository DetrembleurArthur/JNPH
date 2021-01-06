package com.jnet;

public class ProtocolHandlerClient extends ProtocolHandler
{   
    public static ProtocolHandlerClient create(Object protocol)
    {
    	return new ProtocolHandlerClient(protocol);
    }

    public ProtocolHandlerClient(Object protocol)
    {
        super(protocol);
    }

    @Override
    public String getEntityId()
    {
        return "PH(C):" + (tunnel != null ? tunnel.getSocket().getInetAddress().getHostAddress() + ":" + tunnel.getSocket().getPort() + ":" + getProtocolName() : "");
    }

	@Override
	protected void handshake()
	{
		running.set(false);
		send(Query.normal(getOptions().getProperty("name")).mode(Query.Mode.DISCOVERY));
        Query query = recv();
        if(query.getMode().equals(Query.Mode.DISCOVERY))
        {
            if(query.getArgs().size() == 0)
            {
            	running.set(true);
                getOptions().setProperty("name", query.getType());
                Log.out(this, "Protocol discovery success: " + getOptions().getProperty("name"));
            }
            else
            {
                Log.err(this, "Protocol discovery failed");
            }
        }
        else
        {
            Log.err(this, "Protocol discovery failed");
        }
	}

	@Override
	protected void manageQueryModes(Query query)
	{
		
	}
}
