package test;

import com.jnet.ClientProtocol;
import com.jnet.ProtocolMaster;
import com.jnet.Query;


@ClientProtocol(objQuery = true, name = "Test")
public class ClientNotifier
{
	public static void main(String[] args)
	{
		ProtocolMaster master = ProtocolMaster.launch(ClientNotifier.class);
		master.getProtocolClients().get(0).getProtocolHandler().send(Query.normal("alert").pack("Hello world!").classic_broadcast());
	}
}
