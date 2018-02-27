/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author hhp6148
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ServerPro server = new ServerPro(8000);
        while (true){
            server.run();
        }
    }
}

class ServerPro {
    protected ArrayList<Client> clients;
    protected ServerSocket serverSocket;
    protected Socket clientSocket;

    public ServerPro(int port){
        this.clients = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(port);
            this.clientSocket = null;
        } catch (IOException ex) {
            Logger.getLogger(ServerPro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            this.clientSocket = this.serverSocket.accept();
            Client t = new Client(clientSocket,clients);
            clients.add(t);
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerPro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class Client extends Thread {
    protected ObjectInputStream is;
    protected ObjectOutputStream os;
    protected Socket socket;
    protected ArrayList<Client> clients;
    protected String username;

    public Client(Socket socket, ArrayList<Client> clients){
        try {
            this.clients = clients;
            this.socket = socket;
            System.out.println("Client connected");
            is = new ObjectInputStream(this.socket.getInputStream());
            os = new ObjectOutputStream(this.socket.getOutputStream());
            Message joinmess = (Message) is.readObject();
            username = joinmess.getSender();
            for (Client c : clients){
                Message message = new Message("join",username,c.username,username+" joined");
                c.os.writeObject(message);
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e){
            System.err.println("Can not cast to class " + e);
        }


    }
    @Override
    public void run() {
        try {
            while(true){
                Message message = (Message) is.readObject();
                if (message.type.equals("broadcast")) {
                    for (Client c : clients) {
                        if (c != this) {
                            c.os.writeObject(message);
                        }
                    }
                } else if (message.type.equals("direct")) {
                    for (Client c:clients){
                        if (c.getThreadName().equals(message.receiver)) {
                            c.os.writeObject(message);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Client left");
        } catch (ClassNotFoundException e){
            System.err.println("Can not cast to class Mail");
        }
    }
    public String getThreadName(){
        return username;
    }
}
