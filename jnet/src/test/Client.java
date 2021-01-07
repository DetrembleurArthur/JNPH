package test;


import com.jnet.ClientProtocol;
import com.jnet.Control;
import com.jnet.ProtocolMaster;

@ClientProtocol(objQuery = false, name = "Test", port = 6000, ssl = true)
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
