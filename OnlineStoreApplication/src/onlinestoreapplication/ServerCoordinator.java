package onlinestoreapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author linke
 */
public class ServerCoordinator {
    public static void main(String[] args) { 
        try { 
            System.out.println("ServerCoordinator start") ; 
            int serverPort = 6433 ; 
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            int i = 1 ; 
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                Connection1 c = new Connection1(clientSocket, i++) ; 
                c.start() ; 
            } 
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage()) ;
        }
    }
}

class Connection1 extends Thread { 
    DataInputStream in ; 
    DataOutputStream out ; 
    ObjectOutputStream out3 ; 
    Socket clientSocket ; 
    int threadCount ; 
    String hostName = "localhost" ; 
    
    public Connection1(Socket aClientSocket, int number) { 
        try { 
            threadCount = number ; 
            clientSocket = aClientSocket ; 
            out = new DataOutputStream(clientSocket.getOutputStream()) ; 
            out3 = new ObjectOutputStream(clientSocket.getOutputStream()) ; 
            in = new DataInputStream(clientSocket.getInputStream()) ; 
        } catch (IOException e) { 
            System.out.println("Connection: " + e.getMessage()) ; 
        }
    }
    
    @Override
    public void run() { 
        String [] brokenString ; 
        String serverIdentifier ; 
        int quantity ; 
        double price ; 
        
        Socket s = null ; 
        ObjectInputStream in20 ; 
        ObjectOutputStream out20 ; 
        
        try { 
            String data = in.readUTF() ; 
            System.out.println("ServerCoordinator received client object number: " + threadCount) ; 
            
            brokenString = data.split("::") ; 
            quantity = Integer.parseInt(brokenString[0]) ; 
            price = Double.parseDouble(brokenString[1]) ; 
            serverIdentifier = brokenString[2] ;
            System.out.println(data + "\nQuantity: " + quantity + "\nPrice: " + price + "\nIdentifier: " + serverIdentifier) ; 
            
            
            switch (serverIdentifier) {
                case "movie" -> {
                    MovieOrder movie = new MovieOrder(quantity, price) ;
                    //Scoket as well as the in/out for the server
                    s = new Socket(hostName, 6488) ;
                    
                    out20 = new ObjectOutputStream(s.getOutputStream()) ;
                    in20 = new ObjectInputStream(s.getInputStream()) ;
                    
                    System.out.println("Sending to server for Movie...") ; 
                    out20.writeObject(movie) ; 
                    
                    //Reassign the new movie object based off the in object 
                    movie = (MovieOrder)in20.readObject() ; 
                    
                    System.out.println("Sending order back to the original client") ;
                    out.writeUTF("movie") ; //Also write data for movie 
                    out3.writeObject(movie) ; 
                }
                case "book" -> {
                    BookOrder book = new BookOrder(quantity, price) ;
                    //Socket and in/out for the server
                    s = new Socket(hostName, 6455) ;
                    
                    out20 = new ObjectOutputStream(s.getOutputStream()) ;
                    in20 = new ObjectInputStream(s.getInputStream()) ; 
                    
                    System.out.println("Sending to server for Book...") ;
                    out20.writeObject(book) ;
                    book = (BookOrder)in20.readObject() ;
                    
                    System.out.println("Return order back to the client") ;
                    //Send to client + the tag for what it is
                    out.writeUTF("book") ; //Also write data for book
                    out3.writeObject(book) ; 
                }
                default -> {
                    System.out.println("Error in logic, exiting program") ;
                    System.exit(0) ;
                }
            } 
            
            s.close() ; 
            
        } catch(EOFException e){System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {System.out.println("readline:"+e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Connection1.class.getName()).log(Level.SEVERE, null, ex);
        }finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
    }
}


//Not sure how to condense this further since the two servers, despite being the exact same need different paramters 
//So fundamentally, it cannot match exactly as a condensed function 
//Input / Output streams MUST match variable name on both sides. Make sure both for server and this, it is "in2" "out2" 