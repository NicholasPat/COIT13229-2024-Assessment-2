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
public class ServerMovie {
    public static void main(String[] args) { 
        int serverPort = 6488 ; 
        int i = 0 ; 
        
        try { 
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            System.out.println("ServerMovie started") ; 
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                Connection c = new Connection(clientSocket, i++) ; 
                System.out.println("Connection created") ; 
                c.start() ; 
                c.run() ; 
                System.out.println("Connection started" + c.toString()) ; 
            }
        } catch (IOException e) {System.out.println("Listen :" + e.getMessage());}
    }
}

class Connection extends Thread { 
    ObjectInputStream in21 ; 
    ObjectOutputStream out20 ; 
    Socket clientSocket ; 
    int numberofTimes ; 
    
    public Connection(Socket aClientSocket, int tn) { 
        numberofTimes = tn ; 
        try { 
            clientSocket = aClientSocket ; 
            out20 = new ObjectOutputStream(clientSocket.getOutputStream()) ; 
            in21 = new ObjectInputStream(clientSocket.getInputStream()) ; 
            System.out.println("All oonstructor variables made" + clientSocket.toString()) ; 
        }catch(IOException e){System.out.println("Connection: "+e.getMessage());}}
    
    @Override
    public void run() { 
        try {
            System.out.println("ServerBook received Book object number: " + numberofTimes) ; 
            MovieOrder movie = (MovieOrder)in21.readObject() ; 
            movie.executeTask(); 
            System.out.println("Computed the total bill for the current Book Order. Sending back to the client\n") ; 
            out20.writeObject(movie) ;
        }catch(EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch(IOException e) {System.out.println("readline:"+e.getMessage());
        }catch(ClassNotFoundException ex){System.out.println("Class not found: " + ex.getMessage()); 
        }finally{try{clientSocket.close();}catch(IOException e){/*close failed*/}}
    }
}


//System.out.println("TRACE: ") ; 

//Using the executeTask() method which will then be set for the object. Meaning it can be sent back to the ServerCoordinator 
//then back to the client to be rewritten 

//Sending book object back to the ServerCoordinator 