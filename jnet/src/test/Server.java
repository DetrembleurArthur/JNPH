package test;

import com.jnet.Args;
import com.jnet.Com;
import com.jnet.Control;
import com.jnet.ServerProtocol;
import com.jnet.ProtocolMaster;
import com.jnet.Query;
import com.jnet.Tunnel;

import javax.swing.*;


public class Server {

	public static void test(String ... args)
	{
		System.out.println(args.length);
	}

	public static void main(String[] args)
	{
		ProtocolMaster.launch(MyProtocol.class);
	}
	
	
	@ServerProtocol(name = "PingPong")
	public static class MyProtocol
	{
		@Com
		public Tunnel tunnel;
		
		@Control
		public void ping()
		{
			System.err.println("ping");
			tunnel.sendbuff(Query.normal("pong"));
			Query query = tunnel.recvobj();
			System.err.println(query.getType());
			for(Object o : query.getArgs())
				for(Object ss : (Double[])o)
					System.err.println(ss);
		}

		@Control
		public void test(Character c)
		{
			System.out.println(c);
		}
	}

}
