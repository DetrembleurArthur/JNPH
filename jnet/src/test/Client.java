package test;

import com.jnet.Args;
import com.jnet.ClientProtocol;
import com.jnet.Com;
import com.jnet.Control;
import com.jnet.ServerProtocol;
import com.jnet.ProtocolClient;
import com.jnet.ProtocolHandler;
import com.jnet.ProtocolMaster;
import com.jnet.Query;
import com.jnet.Tunnel;



public class Client {

	public static void main(String[] args)
	{
		ProtocolMaster master = ProtocolMaster.launch(MyCLIProtocol.class);
		ProtocolClient cli = master.getProtocolClient(MyCLIProtocol.class);
		ProtocolHandler handler = cli.getProtocolHandler();
		Tunnel tunnel = handler.getTunnel();
		tunnel.sendbuff(Query.normal("ping"));
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
