package test;

import com.jnet.*;

import java.io.Serializable;
import java.util.Scanner;


public class Client {


	public static class User implements Serializable
	{
		private String name;
		private int age;

		public User(String name, int age)
		{
			this.name = name;
			this.age = age;
		}

		public String getName()
		{
			return name;
		}

		public int getAge()
		{
			return age;
		}
	}

	public static void main(String[] args)
	{
		ProtocolMaster master = ProtocolMaster.launch(MyCLIProtocol.class);
		ProtocolClient cli = master.getProtocolClient(MyCLIProtocol.class);
		ProtocolHandler handler = cli.getProtocolHandler();

		Tunnel tunnel = handler.getTunnel();


		Scanner scanner = new Scanner(System.in);
		System.err.println("wait....");
		scanner.next();

		System.err.println("...");

		tunnel.sendobj(Query.normal("ping").mode(Query.Mode.BROADCAST));
	}
	
	
	@ClientProtocol(name = "PingPong", objQuery = true)
	public static class MyCLIProtocol
	{
		@Com
		public Tunnel tunnel;

		@Control
		public void pong()
		{
			System.err.println("pong");
		}

		@Control
		public void ping()
		{
			System.err.println("ping");
			tunnel.sendobj(Query.normal("pong").mode(Query.Mode.BROADCAST));
		}
	}
}
