package test;

import javax.swing.JOptionPane;

import com.jnet.Args;
import com.jnet.ClientProtocol;
import com.jnet.Com;
import com.jnet.Control;
import com.jnet.ServerProtocol;
import com.jnet.ProtocolClient;
import com.jnet.ProtocolHandler;
import com.jnet.ProtocolMaster;
import com.jnet.Query;
import com.jnet.Query.Mode;
import com.jnet.Tunnel;


public class Client
{
	public static void main(String[] args)
	{
		ProtocolMaster pm = ProtocolMaster.launch(P1.class, P2.class);
		ProtocolHandler p1 = pm.getProtocolClient(P1.class).getProtocolHandler().waitStarting();
		ProtocolHandler p2 = pm.getProtocolClient(P2.class).getProtocolHandler().waitStarting();
		
		p2.send(Query.normal("priority").pack("Hello").general_broadcast());

	}
	
	@ClientProtocol(objQuery = false, port = 2000, name = "Sqrt")
	public static class P1
	{
		@Control
		public void square(Double n)
		{
			System.out.println("Result: " + n);
		}
		
		@Control
		public void priority(String message)
		{
			System.out.println("SQRT !!! " + message + " !!!");
		}
	}
	
	@ClientProtocol(objQuery = false, port = 3000, name = "Pow")
	public static class P2
	{
		@Control
		public void pow(Double n)
		{
			System.out.println("Result: " + n);
		}
		
		@Control
		public void priority(String message)
		{
			System.out.println("POW !!! " + message + " !!!");
		}
	}
}
