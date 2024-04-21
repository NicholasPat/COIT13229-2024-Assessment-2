package onlinestoreapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author linke
 */
public class OrderClient {
    private static String hostName = "localhost" ; 
    //Server Port will be the same for OrderClient to ServerCoordinator. ServerCoordinator to the other two servers will be different for both 
    private static int serverPort = 6433; 
    
    public static void main(String args[]) { 
        int currentMenu = 1 ; 
        
        while (currentMenu >=1 && currentMenu <=3) { 
            currentMenu = mainMenu() ; 
            switch (currentMenu) {
                case 1 -> //Purchase book 
                    buyItems(1) ;
                case 2 -> //Purchase movie 
                    buyItems(2) ;
                case 3 -> { //exit the program basically
                    System.out.println("Thank you for ordering from [STORE], please come again next time!") ;
                    //Do a socket close as well, to make sure it's fine
                    System.exit(0) ;
                }
                default -> { //Do nothing 
                }
            }
        }
    }
    
    private static int mainMenu() { 
        int menu = 0 ; 
        String intermediateValue ; 
        boolean state = true ; 
        
        System.out.println("""
                           PLEZSE PLACE YOUR ORDER BY SELECTING A NUMBER
                           ____________________________________________________
                           1. Purchase Book(s)
                           2. Purchase movie(s)
                           3. Exit
                           ____________________________________________________""") ; 
        
        while (state) { 
            //Query what menu is wanted then pass the entry to error handling 
            Scanner input = new Scanner(System.in) ; 
            System.out.print("Enter your option: " ) ; 
            intermediateValue = input.nextLine() ; 
            state = checkValue(intermediateValue, 1) ; 
            
            //If the checks passed, then assign the value safely 
            if (!state) { 
                menu = Integer.parseInt(intermediateValue) ; 
                return menu ; 
            } 
        } 
        return menu ; 
    }
    
    //This is where the user is queried what quantity and price of the books they want to have 
    private static void buyItems(int identifier) { 
        String itemType ; 
        int quantity = 0 ; 
        String intermediateQuantity ; 
        double price = 0 ; 
        String intermediatePrice ; 
        boolean state = true ; 
                
        //Should be only 1 or 2 if the error handling did its job correctly 
        if (identifier == 1) { 
            itemType = "book" ; 
        } else { 
            itemType = "movie" ; 
        } 
        
        while (state) { 
            Scanner quantityInput = new Scanner(System.in) ; 
            System.out.print("Enter the number of " + itemType + "s: ") ; 
            intermediateQuantity = quantityInput.nextLine() ; 
            state = checkValue(intermediateQuantity, 2) ; 
            
            if (!state) { 
                quantity = Integer.parseInt(intermediateQuantity) ; 
            }
        }
        
        //Reassign true to state as will be used with checking the price, otherwise would skip the next while loop 
        state = true ; 
        
        while (state) { 
            Scanner priceInput = new Scanner(System.in) ; 
            System.out.print("Enter the " + itemType + " price: ") ; 
            intermediatePrice = priceInput.nextLine() ; 
            state = checkValue(intermediatePrice, 3) ; 
            
            if (!state) { 
                price = Double.parseDouble(intermediatePrice) ; 
            }
        }
        
        //Spacer 
        System.out.println() ; 
        serverConnection(itemType, quantity, price) ; 
    }
    
    private static void serverConnection(String identifier, int quantity, double price) { 
        Socket s = null ; 
        String message ; 
        
        try { 
            s = new Socket(hostName, serverPort) ; 
            
            //Create a DataOutputStream and then write the message to the server. This will be tagged with identifier String to allow server to know where to send thing 
            DataOutputStream out = new DataOutputStream(s.getOutputStream()) ;
            message = quantity + "::" + price + "::" + identifier ;
            out.writeUTF(message) ;
            
            //The in3 and in6 Object streams have to be different as they 
            DataInputStream in = new DataInputStream(s.getInputStream()) ; 
            ObjectInputStream in3 = new ObjectInputStream(s.getInputStream()) ; 
            String objectType = in.readUTF() ; 
            
            System.out.println("Received computed object from server!") ; 
            String tag = null; 
            int fQuantity = 0 ; 
            double fPrice = 0 ; 
            double fTax = 0 ; 
            double fTotalBill = 0 ; 
            
            if(objectType.equals("book")) { 
                BookOrder book = (BookOrder)in3.readObject() ;
                fQuantity = book.getQuantity() ; 
                fPrice = book.getUnitPrice() ; 
                fTax = book.getTax() ; 
                fTotalBill = book.getTotalBill() ; 
                tag = "Books" ; 
            } else if (objectType.equals("movie")) { 
                MovieOrder movie = (MovieOrder)in3.readObject() ; 
                fQuantity = movie.getQuantity() ; 
                fPrice = movie.getUnitPrice() ; 
                fTax = movie.getTax() ; 
                fTotalBill = movie.getTotalBill() ; 
                tag = "Movies" ; 
            } else { 
                System.out.println("Logic error, exiting program") ; 
                System.exit(0) ; 
            } 
            
            //Format better 
            System.out.println("Number of " + tag + ": " + fQuantity + "      Price: " + fPrice + 
                    "       Tax: " + fTax + "      Bill total for the books: " + fTotalBill) ; 
            System.out.println("____________________________________________________\n") ; 
            
        }catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch (IOException e){System.out.println("readline:"+e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OrderClient.class.getName()).log(Level.SEVERE, null, ex);
        }finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }
    
    
    private static boolean checkValue(String value, int i) { 
        int current = 0 ; 
        
        //If initial menu check OR quantity check then do this check 
        if (i == 1 || i == 2) { 
            try { 
                current = Integer.parseInt(value) ; 
            } catch (NumberFormatException e) { 
                System.out.println("Invalid entry, please input as a number") ;
                return true ; 
            }
        }
        
        //Checking if the values are within the 1 - 3 range and if so, end the while loop for the menu check. i is a tag for what it is 
        if (i == 1) { 
            if (current ==1 || current == 2 || current == 3) { 
                return false ; 
            } else { 
                System.out.println("Invalid entry: Please input a value of either 1, 2, or 3") ; 
                return true ; 
            }
        } 
        
        //Checking if price can be doubled 
        if (i == 3) { 
            try { 
                double doubleCurrent = Double.parseDouble(value) ; 
            } catch (NumberFormatException e) { 
                System.out.println("Invalid entry, please input as a number (also without '$'") ; 
                return true ; 
            }
        }
        
        //return false at end as assuming all other checks passed (false ends the while loop) 
        return false ; 
    }
}
