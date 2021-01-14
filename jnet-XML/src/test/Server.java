package test;


import com.jnet.Control;
import com.jnet.ProtocolMaster;
import com.jnet.ServerProtocol;

@ServerProtocol(name = "Test")
public class Server
{
    public static void main(String[] args)
    {
        ProtocolMaster.launch("resources/newXMLDocument.xml", Server.class);
    }

    @Control
    public String ping()
    {
        return "pong";
    }
}
