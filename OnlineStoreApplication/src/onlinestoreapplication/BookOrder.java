package onlinestoreapplication;

/**
 * @author linke
 */
public class BookOrder {
    private int quantity ; 
    private double unitPrice ; 
    private double tax ; 
    private double totalBill ; 

    public BookOrder(int quantity, double unitPrice, double totalBill) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        //Tax is always fixed to 10%, or in decimal, 0.10 
        this.tax = 0.10 ; 
        this.totalBill = totalBill;
    }

    public BookOrder() {
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
        return "BookOrder{" + "quantity=" + quantity + ", unitPrice=" + unitPrice + ", tax=" + tax + ", totalBill=" + totalBill + '}';
    }
    
    
}
