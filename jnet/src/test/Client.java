package test;


import com.jnet.ClientProtocol;
import com.jnet.Control;
import com.jnet.ProtocolMaster;

@ClientProtocol(objQuery = true, name = "Test", port = 50000, ssl = true, ip = "87.67.186.235")
public class Client
{
    public static void main(String[] args)
    {
        ProtocolMaster.launch(Client.class);
    }

    @Control
    public void pong()
    {
        System.out.println("pong receive");
    }
}
