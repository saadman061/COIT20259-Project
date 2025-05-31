package Customer.Beans;

import Order.Beans.Order;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Entity class representing a Customer.
 * Maps to the "customers" table in the database.
 */
@Entity
@Table(name = "customers")
public class Customer implements Serializable {

    /** Primary key, auto-generated */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Customer's full name */
    private String name;

    /** Customer's physical address */
    private String address;

    /** Customer's phone number */
    private String phoneNumber;

    /** Customer's email address */
    private String email;

    /**
     * One-to-many relationship with Order entity.
     * A customer can have multiple orders.
     * CascadeType.ALL propagates persistence operations to orders.
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;

    /** Default constructor */
    public Customer() {}

    // --- Getters and Setters ---

    /** 
     * Returns the customer's ID
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the customer's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's name
     * @param name Customer's full name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customer's address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the customer's address
     * @param address Customer's address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the customer's phone number
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the customer's phone number
     * @param phoneNumber Customer's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the customer's email address
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address
     * @param email Customer's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the list of orders associated with the customer
     * @return List of Order objects
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Sets the list of orders associated with the customer
     * @param orders List of orders
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
