package onlinestoreapplication;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author linke
 */
public class ServerBook {
    public static void main(String args[]) { 
        try { 
            System.out.println("ServerBook start") ; 
            int serverPort = 6455 ; 
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            int i = 1 ; 
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                BookConnection connection = new BookConnection(clientSocket, i++) ; 
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}

class BookConnection extends Thread { 
    ObjectInputStream in10 ; 
    ObjectOutputStream out10 ; 
    Socket clientSocket ; 
    int numberofTimes ; 
    
    public BookConnection(Socket aClientSocket, int tn) { 
        try { 
            numberofTimes = tn ; 
            clientSocket = aClientSocket ; 
            in10 = new ObjectInputStream(clientSocket.getInputStream()) ; //Data for the initial data sent to here 
            out10 = new ObjectOutputStream(clientSocket.getOutputStream()) ; //Object for sending back to the coordinator 
            this.start() ; 
        }catch (IOException ex){ex.printStackTrace();}
    }
    
    public void run() { 
        try {
            BookOrder book = (BookOrder)in10.readObject() ; 
            System.out.println("ServerBook received Book object number: " + numberofTimes) ; 
            
            //Using the executeTask() method which will then be set for the object. Meaning it can be sent back to the ServerCoordinator 
            //then back to the client to be rewritten 
            book.executeTask();
            System.out.println("Computed the total bill for the current Book Order. Sending back to the client\n") ; 
            
            //Sending book object back to the ServerCoordinator 
            out10.writeObject(book) ;
        }catch(EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch(IOException e) {System.out.println("readline:"+e.getMessage());
        }catch(ClassNotFoundException ex){ex.printStackTrace(); 
        }finally{try{clientSocket.close();}catch(IOException e){/*close failed*/}}
    }
}