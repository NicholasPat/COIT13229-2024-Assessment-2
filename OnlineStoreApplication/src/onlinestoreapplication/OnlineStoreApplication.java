package onlinestoreapplication;

import java.util.Scanner;

/**
 * @author linke
 */
public class OnlineStoreApplication {
    public static void main(String[] args) {
        String instanceForTest = "0" ; 
        boolean state = true ; 
        //0 = Nothing, 1 = TCP Client, 2 = TCP Server, 3 = UDP Client, 4 = UDP Server 
        while (state == true) { 
            Scanner input = new Scanner(System.in) ; 
            System.out.print("Please input the testing parameter desired, input = INT (1,4): " ) ; 
            instanceForTest = input.nextLine() ; 
            System.out.println() ; 
            if (instanceForTest.matches("1|2|3|4")){ 
                state = false ; 
            }
        }
        
        switch (instanceForTest) {
            case "1" -> OrderClient.main(null) ;
            case "2" -> ServerCoordinator.main(null) ;
            case "3" -> {ServerBook.main(null); }
            case "4" -> ServerMovie.main(null) ;
            default -> System.out.println("How did you break this?") ;
        }
    }
    
}
