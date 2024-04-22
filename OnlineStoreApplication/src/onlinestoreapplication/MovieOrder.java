package onlinestoreapplication;

import java.io.Serializable;

/**
 * BookOrder stores the information the user set for the book order they want 
 * Very simple class and pretty much a clone of MovieOrder. For optimisation
 * in the future, would be better to make a singular class called media and 
 * have an identifier or something. Would also mean that it's easy to scale 
 * 
 * @author Nicholas Paterno 12188564
*/
public class MovieOrder implements Task, Serializable {
    private int quantity ; 
    private double unitPrice ; 
    private double tax ; 
    private double totalBill ; 
    
    /** 
     * Constructor builds the initial object with just these two parameters 
     * as the object then gets passed around and the tax and totalBill fields 
     * are populated via executeTask() method 
     * 
     * @param quantity   Get the quantity from the user and pass it here to be 
     *                   initialised. Count of how many units the user wants 
     * @param unitPrice  Get the price from the user and pass it here to be 
     *                   initialised. Price of each of the units desired 
    */
    public MovieOrder(int quantity, double unitPrice) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /* Constructor with no arguments, used when making the empty objects which then get 
    assigned with actual object already made */
    public MovieOrder() {
    }

    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    @Override
    public String toString() {
        return "MovieOrder{" + "quantity=" + quantity + ", unitPrice=" + unitPrice + ", tax=" + tax + ", totalBill=" + totalBill + '}';
    }

    @Override
    public void executeTask() {
        tax = quantity * unitPrice * 0.30 ; 
        totalBill = (quantity * unitPrice) + tax ; 
    }

    @Override
    public double getResult() {
        return totalBill ; 
    }
    
    
}
