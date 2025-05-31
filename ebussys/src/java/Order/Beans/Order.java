package Order.Beans;

import Customer.Beans.Customer;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity class representing an Order.
 * Maps to the "orders" table in the database.
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    /** Primary key of the order, auto-generated */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Model name of the product ordered */
    private String productModel;

    /** Quantity of the product ordered */
    private int quantity;

    /** Unit price of the product at the time of ordering */
    private double unitPrice;

    /** Date and time when the order was placed */
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    /** 
     * The customer who placed the order.
     * Many orders can be linked to one customer.
     * Foreign key column: customer_id
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** Default constructor initializing orderDate to current date/time */
    public Order() {
        this.orderDate = new Date();
    }

    // === Getters and Setters ===

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
