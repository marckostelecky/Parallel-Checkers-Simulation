import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



public class TwoPlayerOnline implements Serializable{

	private String hostname;
	private int port;
	private String clientHost;
	private int clientPort = 9992;
	Socket socketClient;
	ServerSocket serverSocket;
	
	public TwoPlayerOnline(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
	}
	
	public void connectPeer(String clientHost) throws UnknownHostException, IOException{
		this.clientHost = clientHost;
		System.out.println("Attepting to connect to " + hostname + ":" + port);
		socketClient = new Socket(clientHost,clientPort);
		System.out.println("Connection Established");
		
	}
	
	public void openPeerConnection(){
		boolean waiting = true;
		// Opens new socket
		System.out.println("Starting the socket server at port:" + 9992);
		try {
			serverSocket = new ServerSocket(9992);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Socket client = null;
		// listens on socket for incoming connections and
		// starts a new thread of SocketPeerHandler for each connection
		while (waiting) {
			System.out.println("Waiting for clients...");
			try {
				client = serverSocket.accept();
				waiting = false;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("The following client has connected:" + client.getInetAddress().getCanonicalHostName());
		}
	
}
	public void connect() throws UnknownHostException, IOException{
		System.out.println("Attepting to connect to " + hostname + ":" + port);
		socketClient = new Socket(hostname,port);
		System.out.println("Connection Established");
	}
	
	public String[] readResponse() throws IOException, ClassNotFoundException{
		
		ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
		String data[] = (String[]) in.readObject();
		return data;
	}
	
	public void sendMessage(String[] message) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(socketClient.getOutputStream());
		out.writeObject(message);
	}
	
	public String[] newPlayer() throws UnknownHostException, IOException, ClassNotFoundException{
		connect();
		String message[] = {"CONNECT"};
		sendMessage(message);
		String data[] = readResponse();
		return data;
	}
	
	public String[] getPlayerList() throws UnknownHostException, IOException, ClassNotFoundException{
		connect();
		String message[] = {"PLAYERS"};
		sendMessage(message);
		String data[] = readResponse();
		return data;
	}
	
	
	public void newGame(String player2) throws ClassNotFoundException, IOException{
		connect();
		String message[] = {"NEW",player2};
		sendMessage(message);
	}
}
