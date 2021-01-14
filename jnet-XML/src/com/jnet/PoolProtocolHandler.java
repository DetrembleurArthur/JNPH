package com.jnet;

public class PoolProtocolHandler extends ProtocolHandlerServer
{
	private boolean pooling = false;
	
    public PoolProtocolHandler(Object protocol, ProtocolServer sup)
    {
		super(protocol, sup);
		options = sup.getOptions();
	}

	private void wakeup()
    {
    	try
        {
            synchronized (getProtocolServer().getPoolMonitor())
            {
                while (getProtocolServer().getSocket() == null)
                {
                	Log.out(this, " is waiting");
                    getProtocolServer().getPoolMonitor().wait();
                    Log.out(this, " is unlocked");
                }
                initNet(getProtocolServer().getSocket());
                getProtocolServer().setSocket(null);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
    	pooling = true;
    	while(pooling)
    	{
    		try
    		{
	    		wakeup();
	    		super.run();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }

    @Override
    public String getEntityId()
    {
        return "POOL:" + super.getEntityId();
    }
}
