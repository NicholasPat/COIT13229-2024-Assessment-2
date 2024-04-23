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
public class ServerBook {
    
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
        /*Declarations for server port, and the number of threads made this session*/
        int i = 1 ; 
        int serverPort = 6455 ; 
        
        try {
            /*Server socket listens to the port identified. Once receiving a connection 
            will then create a connection thread then start it */
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                Connection3 c = new Connection3(clientSocket, i++) ; 
                c.start() ; //Not starting thread in constructor
            }
        } catch (IOException e){System.out.println("Listen :" + e.getMessage());}
    }
}

/** 
 * Connection3 is like the other connections in that it creates the Thread as a connection
 * comes in, then it creates the object input / output streams then reads the 
 * input stream to get the object sent by ServerCoordinator, then invokes the 
 * method executeTask() on the object which sets tax and total. Then it is sent 
 * back to the ServerCoordinator 
 * 
 * @author Nicholas Paterno 12188564
 * @see Thread 
 */
class Connection3 extends Thread { 
    ObjectInputStream in10 ; //Input of the ServerCoordinator 
    ObjectOutputStream out10 ; //Output of the ServerCoordinator 
    Socket clientSocket ; //The current socket made by the server 
    int numberofTimes ; //Thread count this session 
    
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
    public Connection3(Socket aClientSocket, int tn) { 
        numberofTimes = tn ; //Count of how many threads made 
        clientSocket = aClientSocket ; //Current socket made by listenSocket 
        try { 
            in10 = new ObjectInputStream(clientSocket.getInputStream()) ; //Data for the initial data sent to here 
            out10 = new ObjectOutputStream(clientSocket.getOutputStream()) ; //Object for sending back to the coordinator 
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
            //Create a BookOrder object and assign the sent object from Server Coordinator 
            BookOrder book = (BookOrder)in10.readObject() ; 
            System.out.println("ServerBook received Book object number: " + numberofTimes) ; 
            
            /* Invoke the method to calculate the cost and with that totalBill
            variable set it will be pushed into the output stream to be read by
            the coordinator */
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