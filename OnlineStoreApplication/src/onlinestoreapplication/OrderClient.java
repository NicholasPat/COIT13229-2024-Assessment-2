package onlinestoreapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This class is the front end for the clients to use. It asks them what menu 
 * option they would like, and then goes through and creates the server connection
 * as well as send the text and receive the computed object. That object is then 
 * output in a string for the client to see 
 * 
 * @author Nicholas Paterno 12188564
 */
public class OrderClient { 
    /** 
     * All main does is initialise the mainMenu() method and get the menu wanted 
     * from the client. As it gets that it will invoke either System.exit(0) if 3
     * or the buyItems method with an identifier integer which determines whether
     * user wanted a book or a movie. Upon final output by serverConnection() 
     * method, will repeat this menu and keep going until exit is invoked
     * 
     * @param args  Not important 
     */
    public static void main(String[] args) { 
        int currentMenu = 1 ; //To initialise the loop as cannot be 0 
        
        while (currentMenu >=1 && currentMenu <=3) { 
            currentMenu = mainMenu() ; 
            switch (currentMenu) {
                case 1 -> //Purchase book 
                    buyItems(1) ;
                case 2 -> //Purchase movie 
                    buyItems(2) ;
                case 3 -> { //exit the program basically
                    System.out.println("Thank you for ordering from [STORE], "
                            + "please come again next time!") ;
                    System.exit(0) ;
                }
                default -> { //Do nothing 
                }
            }
        }
    }
    
    /** 
     * This method simply asks the user for the menu option they want, which is 
     * either 1, 2, or 3. Error checks to make sure the returned value is an 
     * Integer or those values. Keeps re-asking the user to input again if there 
     * is a NumberFormatException 
     * 
     * @return  int value of what menu the person wants (1, 2, or 3) 
     */
    private static int mainMenu() { 
        /*Assignments*/
        int menu = 0 ; 
        String intermediateValue ; //Assigned to before menu since nextLine() is String
        boolean state = true ; //Controls if while loop ends 
        
        /*Text block for the main menu. Output as menu is invoked*/
        System.out.println("""
                           PLEZSE PLACE YOUR ORDER BY SELECTING A NUMBER
                           ____________________________________________________
                           1. Purchase Book(s)
                           2. Purchase movie(s)
                           3. Exit
                           ____________________________________________________""") ; 
        
        /*While error, keep trying. When returned false will end and then exit 
        the method sending the 1, 2, or 3 value*/
        while (state) { 
            /*Query what menu is wanted then pass the entry to error handling*/
            Scanner input = new Scanner(System.in) ; 
            System.out.print("Enter your option: " ) ; 
            intermediateValue = input.nextLine() ; 
            state = checkValue(intermediateValue, 1) ; 
            
            /*If the checks passed, then assign the value safely*/
            if (!state) { 
                menu = Integer.parseInt(intermediateValue) ; 
                return menu ; 
            } 
        } 
        return menu ; 
    }
    
    /** 
     * User is queried what the quantity and cost desired is. There is error handling 
     * in place to prevent mismatched values and at the end of this method it 
     * passes everything to the serverConnection method 
     * 
     * @param identifier    Used by the program tell which object it is. Whether
     *                      it is a book or movie. The first switch statement 
     *                      is where it is used 
     */
    private static void buyItems(int identifier) { 
        String itemType = null ; //Output by println as part of continuity
        int quantity = 0 ; //Actual quantity 
        String intermediateQuantity ; //Direct user input to change to int quantity
        double price = 0 ; //Actual price
        String intermediatePrice ; //Direct user input to change to double price
        boolean state = true ; //Controls whether the while loops end. Error handling
        
        /*Should be only 1 or 2 as the program is only coding to send 1 or 2*/
        switch (identifier) {
            case 1 -> itemType = "book" ;
            case 2 -> itemType = "movie" ;
            default -> {
                System.out.println("Logic error! Invalid identifier") ;
                System.exit(0) ;
                /*Exists as a just in case. Although should never occur*/
            }
        }
        
        /*Getting quantity. Keep trying until the actual value is valid*/
        while (state) { 
            Scanner quantityInput = new Scanner(System.in) ; 
            
            /*itemType means two separate prints aren't needed*/
            System.out.print("Enter the number of " + itemType + "s: ") ; 
            intermediateQuantity = quantityInput.nextLine() ; 
            state = checkValue(intermediateQuantity, 2) ; 
            
            if (!state) { 
                /*No try - catch since should always work as test was done*/
                quantity = Integer.parseInt(intermediateQuantity) ; 
            }
        }
        
        /*Reassign true to state as will be used with checking the price, 
        otherwise would skip the next while loop*/
        state = true ; 
        
        /*Getting cost. Keep trying until actual value is valid*/
        while (state) { 
            Scanner priceInput = new Scanner(System.in) ; 
            System.out.print("Enter the " + itemType + " price: ") ; 
            intermediatePrice = priceInput.nextLine() ; 
            state = checkValue(intermediatePrice, 3) ; 
            
            if (!state) { 
                price = Double.parseDouble(intermediatePrice) ; 
            }
        }
        
        System.out.println() ; //Spacer 
        /*Send to serverConnection method which will process and pass to server*/
        serverConnection(itemType, quantity, price) ; 
    }
    
    /** 
     * This method is the bulk of this class as it is responsible for all the server 
     * aspects requited to get the text to the ServerCoordinator. Sends out the data 
     * then it receives in the tag and the object sent by ServerCoordinator. Then 
     * it outputs the object details 
     * 
     * @param identifier    Used to determine if a book or movie query 
     * @param quantity      Number of books desired from the client 
     * @param price         individual cost of the books from client 
     * 
     * @throws UnknownHostException     Host IP address could not be determined 
     * @throws EOFException             End Of File / Stream reached unexpectedly
     * @throws IOException              Input / Output failed in some way 
     * @throws ClassNotFoundException   Thrown when class cannot be determined. 
     *                                  Since class is well defined, should never 
     *                                  be thrown 
     */
    private static void serverConnection(String identifier, int quantity, double price) { 
        /*Assignments of server connection. Easier to change up here*/
        String hostName = "localhost" ; 
        int serverPort = 6433; 
        Socket s = null ; 
        String message ; 
        
        /*Assignments for final values to print*/
        String tag = null ; 
        int fQuantity = 0 ; 
        double fPrice = 0 ; 
        double fTax = 0 ; 
        double fTotalBill = 0 ; 
        
        try { 
            /*Create Socket to open connection with client*/
            s = new Socket(hostName, serverPort) ; 
            
            /*Create a DataOutputStream and then write the message to the server. 
            This will be tagged with identifier String to allow server to know 
            what object to make and where to send the object made*/
            DataOutputStream out = new DataOutputStream(s.getOutputStream()) ;
            message = quantity + "::" + price + "::" + identifier ;
            out.writeUTF(message) ;
            
            /*This in and in3 are different InputStreams. in is used to receive the 
            tag from the server to know what to do for upcoming if block. in6 is 
            used to get the object from the server*/
            DataInputStream in = new DataInputStream(s.getInputStream()) ; 
            ObjectInputStream in3 = new ObjectInputStream(s.getInputStream()) ; 
            String objectType = in.readUTF() ; 
            
            System.out.println("RRECEIVING COMPUTED OBJECT FROM THE SERVER........") ; 
            
            /*This if block is simply going through and assigning values to the
            f (final) variables to be printed below. Based on whether object is
            a book, or is a movie*/
            switch (objectType) {
                case "book" -> {
                    BookOrder book = (BookOrder)in3.readObject() ;
                    fQuantity = book.getQuantity() ;
                    fPrice = book.getUnitPrice() ;
                    fTax = book.getTax() ;
                    fTotalBill = book.getTotalBill() ;
                    tag = "Books" ;
                }
                case "movie" -> {
                    MovieOrder movie = (MovieOrder)in3.readObject() ;
                    fQuantity = movie.getQuantity() ;
                    fPrice = movie.getUnitPrice() ;
                    fTax = movie.getTax() ;
                    fTotalBill = movie.getTotalBill() ;
                    tag = "Movies" ;
                }
                default -> { 
                    /*Just an edge case in case of some error occuring*/
                    System.out.println("Logic error, returned value not 'book' "
                            + "or 'movie'. Exiting the application") ;
                    System.exit(0) ;
                }
            }
            
            /*Outputs the final object details*/
            System.out.println("Number of " + tag + ": " + fQuantity 
                    + "      Price: " + fPrice + "       Tax: " + fTax 
                    + "      Bill total for the books: " + fTotalBill) ; 
            System.out.println("____________________________________________________\n") ; 
        }catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch (IOException e){System.out.println("readline:"+e.getMessage());
        } catch (ClassNotFoundException e) {System.out.println("Class Error: "+e.getMessage());
        }finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }
    
    /** 
     * This method simply takes the String entered by the user and checks whether 
     * it can be converted to int or double (as dependant on test). The test(s) 
     * chosen is dependant on the i value which determines the tests wanted 
     * 
     * Try and convert each test and if there is a failure then catch that 
     * NumberFromatException and then return a true boolean to redo the while 
     * loop 
     * 
     * @param value  This is the String value that is entered by the user. It 
     *               checks to see if the String can be converted to an int or 
     *               double 
     * @param i      i is just the identifier for the method to know which tests
     *               to do. This avoids spreading them across multiple check methods
     *               so they are just put in the one with if statements to check 
     * 
     * @throws NumberFormatException    Upon attempting to convert the String to 
     *                                  Double or Integer, upon a failure throws 
     *                                  this error as it means it's incompatible 
     * 
     * @return       Boolean value to return whether tests are successful or not 
     *               true redoes the loop, and false exits it 
     */
    private static boolean checkValue(String value, int i) { 
        int current = 0 ; 
        
        /*If initial menu check OR quantity check then do this check */ 
        if (i == 1 || i == 2) { 
            try { 
                current = Integer.parseInt(value) ; 
            } catch (NumberFormatException e) { 
                System.out.println("Invalid entry, please input as a number") ;
                return true ; 
            }
        }
        
        /*Checking if the values are within the 1 - 3 range and if so, end the while loop for the menu check. i is a tag for what it is*/
        if (i == 1) { 
            if (current ==1 || current == 2 || current == 3) { 
                return false ; 
            } else { 
                System.out.println("Invalid entry: Please input a value of "
                        + "either 1, 2, or 3") ; 
                return true ; 
            }
        } 
        
        /*Checking if price can be doubled*/
        if (i == 3) { 
            try { 
                double doubleCurrent = Double.parseDouble(value) ; 
            } catch (NumberFormatException e) { 
                System.out.println("Invalid entry, please input as a number "
                        + "(also without '$')") ; 
                return true ; 
            }
        }
        
        /*Return false at end as assuming all other checks passed 
        False ends the while loop*/
        return false ; 
    }
}
