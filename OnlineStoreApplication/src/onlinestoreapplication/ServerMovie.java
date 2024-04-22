package onlinestoreapplication;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is the main initialisation of the Book Server. It starts and listens
 * to the server port that is set (6455) and it accepts the client socket connections
 * Connection class is created as a thread and then is started. It repeats this 
 * until it is stopped 
 * 
 * @author Nicholas Paterno 12188564
 */
public class ServerMovie {
    
    /** 
     * Initialises the server socket and waits for a socket connection then steps 
     * through and initialises the relevant Connection and then starts the thread 
     * 
     * @param args  Not important 
     * 
     * @throws IOException  Exception is thrown if the serverSocket fails or 
     *                      runs into some sort of error 
     */
    public static void main(String[] args) { 
        //Declarations 
        int serverPort = 6488 ; 
        int i = 0 ; 
        
        try { 
            /*Server socket listens to the port identified. Once receiving a connection 
            will then create a connection thread then start it */
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                Connection2 c = new Connection2(clientSocket, i++) ; 
                c.start() ; 
            }
        } catch (IOException e) {System.out.println("Listen :" + e.getMessage());}
    }
}

/** 
 * Connection2 is like the other connections in that it creates the Thread as a connection
 * comes in, then it creates the object input / output streams then reads the 
 * input stream to get the object sent by ServerCoordinator, then invokes the 
 * method executeTask() on the object which sets tax and total. Then it is sent 
 * back to the ServerCoordinator 
 * 
 * @author Nicholas Paterno 12188564
 * @see Thread 
 */
class Connection2 extends Thread { 
    ObjectInputStream in21 ; 
    ObjectOutputStream out21 ; 
    Socket clientSocket ; 
    int numberofTimes = 1 ; 
    
    /** 
     * This constructor generates the ObjectInputStream and ObjectOutputStream 
     * using the given client socket which is the current thread connection made 
     * from the ServerCoordinator.java 
     * 
     * @param aClientSocket The server listen socket created. It is used to 
     *                      make the input/output streams 
     * @param tn            Assigns a count to the thread which allows for output 
     *                      of the current thread count 
     * @throws IOException  This exception is basically saying that the Input / 
     *                      Output failed in some way so will output the error
     */
    public Connection2(Socket aClientSocket, int tn) { 
        numberofTimes = tn ; 
        clientSocket = aClientSocket ; 
        try {
            out21 = new ObjectOutputStream(clientSocket.getOutputStream()) ; 
            in21 = new ObjectInputStream(clientSocket.getInputStream()) ; 
        }catch(IOException e){System.out.println("Connection: "+e.getMessage());}
    }
    
    /** 
     * The run() override will create a Book object and read in the object from the 
     * initialised stream and then executeTask() then send back to ServerCoordinator 
     * 
     * @throws ClassNotFoundException   Thrown when Java cannot determine the class 
     *                                  referenced in code. Since we are calling 
     *                                  for BookOrder, should not throw an error 
     *                                  unless the class is missing from the project 
     * @throws EOFException             End Of File Exception which signals that 
     *                                  the stream is at the end of it's operation. 
     *                                  Usually unexpectedly 
     * @throws IOException              This exception is basically saying that 
     *                                  the Input / Output failed in some way 
     *                                  and will output the issue 
     */
    @Override
    public void run() { 
        try {
            System.out.println("ServerBook received Book object number: " + numberofTimes) ; 
            MovieOrder movie = (MovieOrder)in21.readObject() ; 
            movie.executeTask(); 
            System.out.println("Computed the total bill for the current Book Order. Sending back to the client\n") ; 
            out21.writeObject(movie) ;
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