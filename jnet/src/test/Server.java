package test;

import com.jnet.Args;
import com.jnet.Com;
import com.jnet.Control;
import com.jnet.ProtocolHandler;
import com.jnet.ServerProtocol;
import com.jnet.ProtocolMaster;
import com.jnet.Query;
import com.jnet.Tunnel;

public class Server
{
	public static void main(String[] args)
	{
		ProtocolMaster.launch(P1.class, P2.class);
	}
	
	
	@ServerProtocol(name="Sqrt", objQuery = false, port = 2000)
	public static class P1
	{
		@Com
		public ProtocolHandler handler;
		
		@Control
		public Object square(Double n)
		{
			return Math.sqrt(n);
		}
	}
	
	@ServerProtocol(name="Pow", objQuery = false, port = 3000)
	public static class P2
	{
		@Com
		public ProtocolHandler handler;
		
		@Control
		public Object pow(Double n)
		{
			return n * n;
		}
	}
}
