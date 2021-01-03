package test;

import com.jnet.*;


public class Server {

	public static void main(String[] args)
	{
		ProtocolMaster.launch(MyProtocol.class);
	}
	
	@ServerProtocol(name = "PingPong", objQuery = true)
	public static class MyProtocol
	{
		@Com
		public Tunnel tunnel;

		@Control
		public void hello(String[] elems)
		{
			for(String e : elems)
				System.out.println("### " + e);
		}
	}
}
