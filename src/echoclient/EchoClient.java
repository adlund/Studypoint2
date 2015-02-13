package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient extends Thread {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    List<EchoListener> listeners = new ArrayList();

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        start();//Set to true, to get auto flush behaviour
    }

    public void send(String msg) {
        output.println(msg);
    }

    public void stopServer() throws IOException {
        output.println(ProtocolStrings.STOP);
    }

    public void run() {
        String msg = input.nextLine();
        while (!msg.equals(ProtocolStrings.STOP)) {
            notifyListeners(msg);
            msg = input.nextLine();
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registerEchoListener(EchoListener l) {
        listeners.add(l);
    }

    public void unRegisterEchoListener(EchoListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String msg) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).messageArrived(msg);
        }
    }

    public static void main (String[] args)  {
        int port = 9090;
        String ip = "localhost";
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        try {
            EchoClient listener = new EchoClient();
            listener.connect(ip, port);
            System.out.println("Sending 'Hello world'");
            listener.send("Hello World");
            System.out.println("Waiting for a reply");
            //System.out.println("Received: " + tester.receive()); //Important Blocking call         
            listener.stop();
            //System.in.read();      
        } catch (UnknownHostException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
