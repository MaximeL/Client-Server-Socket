package fr.maximel.projects.client.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servor implements Runnable {

    private ServerSocket serverSocket = null;
    //TODO for later optimisation
    //private HashMap<String, Communicator> clients = new HashMap<>();
    private Communicator communicator;
    private Socket newCommer;

    private int port = 8080;

    private Status lastStatus;
    private Function function;

    public Servor() {
    }

    public Servor(int port) {
        this.port = port;
    }

    public void init() {
        System.out.println("Servor : Trying to connect to the server...");
        System.out.println("Servor : port : "+port);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Servor : Error while connecting to the socket !");
            System.err.println(e.toString());
            lastStatus = Status.CONNECTION_ERROR;
            return;
        }
        System.out.println("Servor : Connection established.");

        lastStatus = Status.RUNNING;
    }

    public void shutdown() {
        System.out.println("Servor : Shutting down...");
        try {
            if(serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            System.err.println("Servor : Error while shutting down ! ");
            System.err.println(e.toString());
        }

        serverSocket = null;
        System.out.println("Servor : All close.");
    }

    @Override
    public void run() {
        //Sending message
        System.out.println("Servor : running server...");
        try {
            init();
            while(true) {
                System.out.println("Servor : Waiting connection...");
                newCommer = serverSocket.accept();
                System.out.println("Servor : Connection from\n");
                System.out.println("Servor : hostname : " + newCommer.getLocalAddress());
                System.out.println("Servor : Port : " + newCommer.getPort());
                communicator = new Communicator(newCommer);
                communicator.setFunction(function);
                System.out.println("Servor : Lauchning communicator process.");
                communicator.process();
                System.out.println("Servor : Process Launched.");
            }
        } catch(Exception e) {
            shutdown();
            lastStatus = Status.UNKNOWN_ERROR;
        }
        System.out.println("Communicator : End of Servor");
    }

    public void process(Function function) {
        this.function = function;
        (new Thread(this)).start();
    }
}
