import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hhp6148
 */
public class Client implements Serializable{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Register username: ");
        Scanner sc = new Scanner(System.in);
        String username = sc.nextLine();
        Spy spy = new Spy(username,"localhost",8000);
        try {
            spy.connect();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

class Spy implements Serializable{
    protected String id;
    protected Socket socket;
    protected String address;
    protected int port;
    protected ObjectInputStream is;
    protected ObjectOutputStream os;

    public Spy(String id, String address, int port){
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public void connect() throws IOException{
        try {
            InetAddress ip = InetAddress.getByName(address);
            this.socket = new Socket(ip,this.port);
            System.out.println("Connected");
            os = new ObjectOutputStream(this.socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            os.writeObject(new Message("join",this.id,"",""));
            System.out.println("Done registering");
            Runnable listener = new Listener(is);
            Thread t = new Thread(listener);
            t.start();

            Scanner sc = new Scanner(System.in);


            loop:while(true){
                String cmd = sc.nextLine();
                switch (cmd){
                    case "0":
                        break loop;
                    case "1":
                        DisplayMenu();
                        break;
                    case "2":
                        Broadcast(sc,os);
                        break;
                    case "3":
                        Direct(sc,os);
                        break;
                    default:
                        DisplayMenu();
                        break;
                }
            }
            os.close();
            is.close();
            this.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Spy.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void close(){
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Spy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void DisplayMenu() {
        System.out.println("===============Menu===============");
        System.out.println("0: Exit program");
        System.out.println("1: Display Menu");
        System.out.println("2: Broadcast Message");
        System.out.println("3: Direct Message");
    }

    private void Broadcast(Scanner sc, ObjectOutputStream os) {
        try {
            System.out.print("Message to send: ");
            String data = sc.nextLine();
            Message message = new Message("broadcast",this.id, "",data);
            os.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Spy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Direct(Scanner sc, ObjectOutputStream os) {
        try {
            System.out.print("To (username): ");
            String receiver = sc.nextLine();
            System.out.print("Message to send: ");
            String data = sc.nextLine();
            Message message = new Message("direct",this.id, receiver,data);
            os.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Spy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class Listener implements Runnable{
    protected ObjectInputStream is;

    public Listener(ObjectInputStream is){
        this.is= is;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message response = (Message)is.readObject();
                if (response.type.equals("join")){
                    System.out.println(response.data);
                } else if (response.type.equals("broadcast")){
                    System.out.println("[BROADCAST] "+response.sender+": "+response.data);
                } else if (response.type.equals("direct")){
                    System.out.println("[DIRECT] "+response.sender+": "+response.data);
                }
            } catch (IOException ex) {
                Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Cannot reach the server");
                break;
            } catch (ClassNotFoundException e){
                System.err.println("Can not cast to type Message");
            }
        }
    }

}
