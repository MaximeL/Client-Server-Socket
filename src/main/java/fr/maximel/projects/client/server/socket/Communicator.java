package fr.maximel.projects.client.server.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;

public class Communicator implements Runnable {
    
    private Socket clientSocket = null;
    private ObjectOutputStream outToServer = null;
    private ObjectInputStream inFromServer = null;
    private Status lastStatus = Status.RUNNING;

    private String hostname;
    private int port;

    private Message message;
    private Function function;
    ExecutorService executor = null;

    public Communicator() {}

    public Communicator(Socket socket) {
        this.clientSocket = socket;
        this.port = socket.getLocalPort();
    }

    public void connection(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        connection();
    }

    private void connection() {

        try {
            if(clientSocket == null) {
                System.out.println("Communicator "+port+" : : Trying to connect to the server...");
                System.out.println("Communicator "+port+" : : hostname : "+hostname);
                System.out.println("Communicator "+port+" : : port : "+port);
                clientSocket = new Socket(hostname, port);
            }
        } catch (IOException e) {
            System.err.println("Communicator "+port+" : : Error while connecting to the socket !");
            e.printStackTrace();
            lastStatus = Status.CONNECTION_ERROR;
            return;
        }
        System.out.println("Communicator "+port+" : : Connection established.");

        lastStatus = Status.RUNNING;
    }

    protected void shutdown() {
        System.out.println("Communicator "+port+" : : Shutting down...");
        try {
            if(clientSocket != null && !clientSocket.isClosed())
                clientSocket.close();
            if(outToServer != null)
                outToServer.close();
            if(inFromServer != null)
                inFromServer.close();
            if(executor != null)
                executor.shutdownNow();
        } catch (IOException e) {
            System.err.println("Communicator "+port+" : : Error while shutting down ! ");
            e.printStackTrace();
        }

        clientSocket = null;
        outToServer = null;
        inFromServer = null;
        executor = null;
        System.out.println("Communicator "+port+" : : All close.");
    }

    public void send(Message message) {
        // Create input and output streams to client
        System.out.println("Communicator "+port+" : : Sending Message...");
        System.out.println("Communicator "+port+" : : Message : " + message.getMessageType().toString());

        System.out.println("Communicator "+port+" : : Creating the output stream...");
        try {
            outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Communicator "+port+" : : Error while creating the ObjectOutputStream ! ");
            e.printStackTrace();
            lastStatus = Status.COMMUNICATION_ERROR;
            shutdown();
            return;
        }
        System.out.println("Communicator "+port+" : : output created.");

        this.message = message;
        System.out.println("Communicator "+port+" : : Sending...");
        try {
            outToServer.writeObject(message);
        } catch (IOException e) {
            System.err.println("Communicator "+port+" : : Error while sending the message ! ");
            e.printStackTrace();
            lastStatus = Status.COMMUNICATION_ERROR;
            shutdown();
            return;
        }
        System.out.println("Communicator "+port+" : : Message sended.");
        lastStatus = Status.RUNNING;
    }

    public void receive() {
        System.out.println("Communicator "+port+" : : Receiving message...");
        //Try to receive drones. Fail after 10 try.
        //int count = 0;
        //while(count < 10 && count > -1) {
        //    count++;
            try {
                inFromServer = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                System.err.println("Communicator "+port+" : Error while creating ObjectInputStream ! ");
                e.printStackTrace();
                lastStatus = Status.COMMUNICATION_ERROR;
        //        continue;
            }

            try {
                message = (Message) inFromServer.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Communicator "+port+" : Error while receiving the message ! ");
                e.printStackTrace();
                lastStatus = Status.COMMUNICATION_ERROR;
          //      continue;
            }

            System.out.println("Communicator "+port+" : : Message received = " + message.toString());
            lastStatus = Status.RUNNING;
        //    return;
        //}

        //lastStatus = Status.COMMUNICATION_ERROR;
    }

    @Override
    public void run() {
        try {
            System.out.println("Communicator "+port+" : : Start processing...");

            executor = Executors.newSingleThreadExecutor();
            Future<Status> future = executor.submit(function);
            try {
                System.out.println("Communicator "+port+" : : Starting thread...");
                lastStatus = future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                System.err.println("Communicator "+port+" : : Timeout on recuperation of the drone list!");
                lastStatus = Status.TIMEOUT;
            } catch (InterruptedException e) {
                System.err.println("Communicator "+port+" : : Interrupted execution !");
                e.printStackTrace();
                lastStatus = Status.COMMUNICATION_ERROR;
            } catch (ExecutionException e) {
                System.err.println("Communicator "+port+" : : Error in the execution !");
                e.printStackTrace();
                lastStatus = Status.COMMUNICATION_ERROR;
            }

            System.out.println("Communicator "+port+" : : End of thread.");
            shutdown();

            if (lastStatus == Status.RUNNING)
                lastStatus = Status.OK;
        } catch (Exception e) {
            if(lastStatus == Status.OK || lastStatus == Status.RUNNING) {
                System.err.println("Communicator "+port+" : : An unknown error occured...");
                lastStatus = Status.UNKNOWN_ERROR;
            }
        }
        System.out.println("Communicator "+port+" : : End of process.");
        if (lastStatus == Status.RUNNING)
            lastStatus = Status.OK;
    }

    public void process() {
        System.out.println("Communicator "+port+" : : Starting process with function "+function.getClass());
        (new Thread(this)).start();
    }

    public void setFunction(Function function) {
        //Sending message
        this.function = function;
        this.function.setCommunicator(this);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Status getStatus() {
        return lastStatus;
    }

    public Message getMessage() {
        return message;
    }

    public void debugStatus() {
        String res = "=== BEBUG ===\n" +
                "Satus : "+lastStatus+"\n"+
                "Port : "+port+"\n"  +
                "function : "+function.getClass()+"\n";
        if(clientSocket != null) {
            res += "Socket is connected : " + clientSocket.isConnected() + "\n" +
                    "Socket is alive : " + clientSocket.isClosed() + "\n";
        }
        res += "=============";

        System.out.println(res);
    }
}
