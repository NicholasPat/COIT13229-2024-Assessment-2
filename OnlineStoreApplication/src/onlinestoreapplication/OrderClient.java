package onlinestoreapplication;

import java.net.Socket;
import java.util.Scanner;

/**
 * @author linke
 */
public class OrderClient {
    private Socket s = null ; 
    private String hostName = "localHost" ; 
    //Server Port will be the same for OrderClient to ServerCoordinator. ServerCoordinator to the other two servers will be different 
    private int serverPort = 6433; 
    
    private static void run() { 
        int currentMenu = 1 ; 
        while (currentMenu >=1 && currentMenu <=3) { 
            currentMenu = mainMenu() ; 
            switch (currentMenu) {
                case 1:
                    //Purchase book 
                    buyItems(1) ; 
                    break;
                case 2:
                    //Purchase movie 
                    buyItems(2) ; 
                    break;
                case 3:
                    //exit the program basically 
                    break;
                default:
                    break;
            }
        }
    }
    
    private static int mainMenu() { 
        int menu = 0 ; 
        boolean state = true ; 
        System.out.println("""
                           PLEZSE PLACE YOUR ORDER BY SELECTING A NUMBER
                           __________________________
                           1. Purchase Book(s)
                           2. Purchase movie(s)
                           3. Exit
                           __________________________""") ; 
        while (state) { 
            
            //Query what menu is wanted then pass the entry to error handling 
            Scanner input = new Scanner(System.in) ; 
            System.out.println("Enter your option: " ) ; 
            state = checkValue(input.nextLine(), 1) ; 
            
            //If the checks passed, then assign the value safely 
            if (!state) { 
                menu = Integer.parseInt(input.nextLine()) ; 
                //REMOVE THIS PRINT LATER 
                System.out.println("DEBUG: Current menu selected: " + menu) ; 
                return menu ; 
            }
        }
        return menu ; 
    }
    
    private static void buyItems(int identifier) { 
         
    }
    
    private static boolean checkValue(String value, int i) { 
        int current ; 
        try { 
            current = Integer.parseInt(value) ; 
        } catch (NumberFormatException e) { 
            System.out.println("Invalid entry, please input as a number") ;
                return true ; 
        }
        
        //Checking if the values are within the 1 - 3 range and if so, end the loop -- For the menu check. i is a tag for what it is 
        if (i == 1) { 
            if (current ==1 || current == 2 || current == 3) { 
                return false ; 
            } else { 
                System.out.println("Invalid entry: Please input a value of either 1, 2, or 3") ; 
                return true ; 
            }
        }
        
        //return false at end as assuming all other checks passed 
        return false ; 
    }
}
