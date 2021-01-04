package test;

import com.jnet.*;

@ServerProtocol(name = "Test", objQuery = true)
public class Server {

	public static void main(String[] args)
	{
		ProtocolMaster.launch(Server.class);
	}

	@Com
	public ProtocolHandler ph;

	@Control
	public void alert(String message)
	{
		System.err.println("'" + message + "' will be broadcasted");
	}
}
