package Order.Beans;

import Customer.Beans.Customer;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import product.beans.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed bean to handle order-related operations including
 * creating, deleting, searching orders and managing order data.
 * Uses manual transaction control for persistence operations.
 * SessionScoped to maintain state during user session.
 */
@Named(value = "orderBean")
@SessionScoped
public class OrderBean implements Serializable {

    /** EntityManager for JPA operations */
    @PersistenceContext
    private EntityManager em;

    /** UserTransaction to manually manage transactions */
    @Resource
    private UserTransaction utx;

    /** The current order object being created or edited */
    private Order currentOrder = new Order();

    /** Selected customer ID from UI dropdown */
    private Long selectedCustomerId;

    /** Selected product ID from UI dropdown */
    private Integer selectedProductId;

    /** Search criteria: order ID */
    private Integer searchOrderId;

    /** Cached list of all customers for selection */
    private List<Customer> customers;

    /** Cached list of all products for selection */
    private List<Product> products;

    /** List to hold results from order search */
    private List<Order> searchResults;

    /** Cached list of all orders */
    private List<Order> allOrders;

    // --- Getters and Setters ---

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Long getSelectedCustomerId() {
        return selectedCustomerId;
    }

    public void setSelectedCustomerId(Long selectedCustomerId) {
        this.selectedCustomerId = selectedCustomerId;
    }

    public Integer getSelectedProductId() {
        return selectedProductId;
    }

    public void setSelectedProductId(Integer selectedProductId) {
        this.selectedProductId = selectedProductId;
    }

    public Integer getSearchOrderId() {
        return searchOrderId;
    }

    public void setSearchOrderId(Integer searchOrderId) {
        this.searchOrderId = searchOrderId;
    }

    /**
     * Returns the cached list of all customers.
     * Loads from DB if not loaded yet.
     */
    public List<Customer> getCustomers() {
        if (customers == null) {
            customers = em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        }
        return customers;
    }

    /**
     * Returns the cached list of all products.
     * Loads from DB if not loaded yet.
     */
    public List<Product> getProducts() {
        if (products == null) {
            products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        }
        return products;
    }

    /**
     * Returns the cached list of all orders.
     * Loads from DB if not loaded yet.
     * Ensures an empty list if no orders found.
     */
    public List<Order> getAllOrders() {
        if (allOrders == null) {
            allOrders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
            if (allOrders == null) {
                allOrders = new ArrayList<>();
            }
        }
        return allOrders;
    }

    /**
     * Returns the last search result list.
     */
    public List<Order> getSearchResults() {
        return searchResults;
    }

    // --- Business Logic ---

    /**
     * Deletes the specified order from the database.
     * Uses manual transaction management.
     * Refreshes cached order list after deletion.
     * Shows success or error message on UI.
     * 
     * @param order the Order entity to delete
     * @return null to stay on the same page
     */
    public String deleteOrder(Order order) {
        try {
            utx.begin();
            Order managedOrder = em.find(Order.class, order.getId());
            if (managedOrder != null) {
                em.remove(managedOrder);
            }
            utx.commit();

            allOrders = null; // reset cache to reload updated orders

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Order deleted successfully for " + order.getCustomer().getName()));
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Log rollback failure if needed
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting order", e.getMessage()));
        }
        return null; // stay on current page
    }

    /**
     * Places a new order with the selected customer and product.
     * Checks stock availability before committing.
     * Deducts the ordered quantity from product stock.
     * Manages transaction boundaries manually.
     * Resets form and caches on success.
     * Shows success or error message.
     * 
     * @return navigation string for redirection or null on failure
     */
    public String placeOrder() {
        try {
            utx.begin();

            Customer customer = em.find(Customer.class, selectedCustomerId);
            Product product = em.find(Product.class, selectedProductId);

            if (customer == null || product == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid customer or product selection", null));
                utx.rollback();
                return null;
            }

            int orderQty = currentOrder.getQuantity();
            if (product.getStockNumber() < orderQty) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient stock for product: " + product.getModel(), null));
                utx.rollback();
                return null;
            }

            // Deduct stock and update product entity
            product.setStockNumber(product.getStockNumber() - orderQty);
            em.merge(product);

            // Set order details
            currentOrder.setCustomer(customer);
            currentOrder.setProductModel(product.getModel());
            currentOrder.setUnitPrice(product.getPrice());

            em.persist(currentOrder);
            utx.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Order placed successfully for product: " + product.getModel()));

            // Reset form and cached data
            currentOrder = new Order();
            selectedCustomerId = null;
            selectedProductId = null;
            allOrders = null;

            return "listOrders.xhtml?faces-redirect=true";
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Log rollback error if needed
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error placing order", e.getMessage()));
            return null;
        }
    }

    /**
     * Searches orders by their ID.
     * If no ID is provided, clears the search results.
     * Displays appropriate info message if no order found.
     * 
     * @return null to stay on current page
     */
    public String searchOrders() {
        if (searchOrderId == null) {
            searchResults = null;
        } else {
            Order found = em.find(Order.class, searchOrderId);
            if (found != null) {
                searchResults = List.of(found);
            } else {
                searchResults = List.of();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No order found with ID " + searchOrderId, null));
            }
        }
        return null; // stay on current page
    }

    /**
     * Executes the searchOrders method and redirects to the
     * search results page.
     * 
     * @return navigation string with redirect to foundOrders.xhtml
     */
    public String searchAndRedirect() {
        searchOrders();
        return "foundOrders.xhtml?faces-redirect=true";
    }
}
