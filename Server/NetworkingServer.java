import java.io.IOException;
import tage.networking.IGameConnection.ProtocolType;

public class NetworkingServer 
{
	private GameAIServerUDP UDPServer;
	private NPCcontroller npcCtrl;
	//private GameServerUDP thisUDPserver;

	public NetworkingServer(int serverPort) //removed protocol
	{	
		npcCtrl = new NPCcontroller();
		// start networking server
		try
		{ 
			UDPServer = new GameAIServerUDP(serverPort, npcCtrl); 
			//thisUDPServer = newGameServerUDP(serverPort);
		}
		catch (IOException e)
		{ 
			System.out.println("server didn't start"); e.printStackTrace(); 
		}
		npcCtrl.start(UDPServer);
	}
	public static void main(String[] args)
	{ 
		if(args.length == 1)
		{ NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0])); }
	}

}
