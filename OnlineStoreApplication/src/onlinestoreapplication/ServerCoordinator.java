package onlinestoreapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is used to accept the client connection and then pass the data to 
 * the relevant server. Explained further in the Connection1 class 
 * 
 * @author Nicholas Paterno 12188564
 * 
 * @throws IOException  Input / Output stream ended suddenly and unexpectedly 
 */
public class ServerCoordinator { 
    
    /** 
     * Initialises the server socket and waits for a socket connection then steps 
     * through and initialises the relevant Connection and then starts the thread 
     * 
     * @param args  Not important 
     * 
     * @throws IOException  Input / Output stream ended suddenly and unexpectedly
     */
    public static void main(String[] args) { 
        /*Declarations for Server Port and the number of threads*/
        int i = 1 ; 
        int serverPort = 6433 ; 
        
        try { 
            /*Server socket listens to the port identified. Once receiving a connection 
            will then create a connection thread then start it */
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            
            while (true) { 
                
                Socket clientSocket = listenSocket.accept() ; 
                Connection1 c = new Connection1(clientSocket, i++) ; 
                c.start() ; //Not starting in the constructor 
            } 
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage()) ;
        }
    }
}

/** 
 * Connection1 is responsible for generating the thread of the current client 
 * connection. It will take in the data string sent by the client, make the object
 * and then it will send it to the necessary server. Then when that server has 
 * finished processing the object it will send it back. ServerCoordinator then 
 * sends that computed object back to the client with a tag so that it knows 
 * which class the object belongs to 
 * 
 * @author Nicholas Paterno 12188564
 * 
 * @see Thread 
 */
class Connection1 extends Thread { 
    DataInputStream in ; //Get in the initial string of object data 
    DataOutputStream out ; //Output tag for Client 
    ObjectOutputStream out3 ; //Output final Object 
    Socket clientSocket ; //Socket with the client connection 
    int threadCount ; //Count of threads 
    String hostName = "localhost" ; //Name of the host machine (127.0.0.1) 
    
    /** 
     * Constructor will make the current thread with the relevant Input / Output 
     * streams that are bound to the clientSocket in question 
     * 
     * @param aClientSocket Socket which is the connection to the client 
     * @param number        Number of times a connection has been made to the 
     *                      server 
     * 
     * @throws IOException  Unexpected end to the Input / Output 
     */
    public Connection1(Socket aClientSocket, int number) { 
        threadCount = number ; 
        clientSocket = aClientSocket ; 
        
        try { 
            out = new DataOutputStream(clientSocket.getOutputStream()) ; 
            out3 = new ObjectOutputStream(clientSocket.getOutputStream()) ; 
            in = new DataInputStream(clientSocket.getInputStream()) ; 
        } catch (IOException e) { 
            System.out.println("Connection: " + e.getMessage()) ; 
        }
    }
    
    /** 
     * This run class does the whole functionality of the server. It takes the 
     * information provided by the client and determines which server to send it
     * to and then sends off that information. Then it receives back the object
     * with the executeTask() function completed and then it's sent back to the 
     * client with a tag 
     * 
     * @throws EOFException             End of file or stream unexpected 
     * @throws IOException              Input / Output failed unexpectedly 
     * @throws ClassNotFoundException   (object)in.readObject() throws this if 
     *                                  class desired cannot be found 
     */
    @Override
    public void run() { 
        /*Assignments for the variables necessary to make the object*/
        String[] brokenString ; //String array of the client data broken up
        String serverIdentifier ; //Used by switch to determine what object to make
        int quantity ; //Number of books / movies wanted 
        double price ; //The cost of those books / movies 
        
        /*Assignments for the server aspects. General socket and Object streams*/
        Socket s = null ; 
        ObjectInputStream in20 ; 
        ObjectOutputStream out20 ; 
        
        try { 
            /*Take in the UTF stream which will be the information from the client
            with the "::" separators*/
            String data = in.readUTF() ; 
            System.out.println("ServerCoordinator received client object number: " + threadCount) ; 
            
            /*Break up the data using the "::" as a separator and equate each part 
            to the relevant variable which will be used in making the object, as 
            well as sending to the relevant server*/
            brokenString = data.split("::") ; 
            quantity = Integer.parseInt(brokenString[0]) ; 
            price = Double.parseDouble(brokenString[1]) ; 
            serverIdentifier = brokenString[2] ;
            
            
            switch (serverIdentifier) {
                case "movie" -> { 
                    /*Scoket as well as the in/out for the server*/
                    s = new Socket(hostName, 6488) ; 
                    out20 = new ObjectOutputStream(s.getOutputStream()) ;
                    in20 = new ObjectInputStream(s.getInputStream()) ;
                    
                    /*Using created streeams, make object and send it out to the
                    MovieServer*/
                    MovieOrder movie = new MovieOrder(quantity, price) ;
                    System.out.println("Sending to server for Movie...") ; 
                    out20.writeObject(movie) ; 
                    
                    /*Reassign the new movie object based off the in movie object
                    This object will have had tax and totalBill set by server*/
                    movie = (MovieOrder)in20.readObject() ; 
                    
                    /*Send the new object to the client as well as a tag*/
                    System.out.println("Sending order back to the original client") ;
                    out.writeUTF("movie") ; 
                    out3.writeObject(movie) ; 
                }
                /*Because "book" case is the exact same as "movie" case, don't 
                need to comment what is happened as it is doing the exact same 
                steps, only difference is doing a book*/
                case "book" -> { 
                    s = new Socket(hostName, 6455) ; 
                    out20 = new ObjectOutputStream(s.getOutputStream()) ;
                    in20 = new ObjectInputStream(s.getInputStream()) ; 
                    
                    BookOrder book = new BookOrder(quantity, price) ;
                    System.out.println("Sending to server for Book...") ;
                    out20.writeObject(book) ;
                    
                    book = (BookOrder)in20.readObject() ;
                    
                    System.out.println("Return order back to the original client") ; 
                    out.writeUTF("book") ; 
                    out3.writeObject(book) ; 
                }
                default -> {
                    /*Should never be invoked but here just in case it ever happens*/
                    System.out.println("Error in logic, exiting program") ;
                    System.exit(0) ;
                }
            } 
            
            /*Closing the socket so that it ensures there is no cross over when 
            a new one is created*/
            s.close() ; 
        }catch(EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch(IOException e){System.out.println("readline:"+e.getMessage());
        }catch(ClassNotFoundException e){System.out.println("Class error: "+e.getMessage()); 
        }finally{try{clientSocket.close();}catch(IOException e){/*close failed*/}}
    }
}