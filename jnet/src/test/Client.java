package test;

import com.jnet.*;


public class Client {

	public static void main(String[] args)
	{
		ProtocolMaster master = ProtocolMaster.launch(MyCLIProtocol.class);
		ProtocolClient cli = master.getProtocolClient(MyCLIProtocol.class);
		ProtocolHandler handler = cli.getProtocolHandler();
		Tunnel tunnel = handler.getTunnel();
		tunnel.sendbuff(Query.normal("ping"));
		tunnel.senobj(Query.normal("hello").pack(new Double[]{1.9, 2.5, 3.0}));
	}
	
	
	@ClientProtocol(name = "PingPong")
	public static class MyCLIProtocol
	{
		
		@Control
		public void pong()
		{
			System.err.println("pong");
		}
	}
}
