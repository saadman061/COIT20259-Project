/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import product.beans.Product;

@Named(value = "orderBean")
@SessionScoped
public class OrderBean implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private Order currentOrder = new Order();

    // IDs selected from dropdowns
    private Long selectedCustomerId;
    private Integer selectedProductId;
    private Integer searchOrderId; 

    // Lists for dropdown
    private List<Customer> customers;
    private List<Product> products;
    private List<Order> searchResults;
    private List<Order> allOrders;

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

    public List<Customer> getCustomers() {
        if (customers == null) {
            customers = em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        }
        return customers;
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        }
        return products;
    }

    public List<Order> getAllOrders() {
        if (allOrders == null) {
            allOrders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
            if (allOrders == null) {
                allOrders = new ArrayList<>();
            }
        }
        return allOrders;
    }

     public Integer getSearchOrderId() {
        return searchOrderId;
    }

    public void setSearchOrderId(Integer searchOrderId) {
        this.searchOrderId = searchOrderId;
    }

    public List<Order> getSearchResults() {
        return searchResults;
    }

    public String deleteOrder(Order order) {
        try {
            System.out.print(order);
            utx.begin();
            Order managedOrder = em.find(Order.class, order.getId());
            if (managedOrder != null) {
                em.remove(managedOrder);
            }
            utx.commit();

            // Reset the cached list to reload updated orders
            allOrders = null;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Order deleted successfully for " + order.getCustomer().getName()));
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Log rollback failure
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting order", e.getMessage()));
        }
        return null; // stay on the same page
    }


    public String placeOrder() {
        try {
            utx.begin();

            // Fetch entities by ID
            Customer customer = em.find(Customer.class, selectedCustomerId);
            Product product = em.find(Product.class, selectedProductId);

            if (customer == null || product == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid customer or product selection", null));
                utx.rollback();
                return null;
            }

            int orderQty = currentOrder.getQuantity();

            // Check stock availability
            if (product.getStockNumber() < orderQty) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient stock for product: " + product.getModel(), null));
                utx.rollback();
                return null;
            }

            // Deduct stock
            product.setStockNumber(product.getStockNumber() - orderQty);
            em.merge(product); // Update product stock in DB

            // Set related entities and order info
            currentOrder.setCustomer(customer);
            currentOrder.setProductModel(product.getModel());
            currentOrder.setUnitPrice(product.getPrice());

            // Persist the order
            em.persist(currentOrder);
            utx.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Order placed successfully for product: " + product.getModel()));

            // Reset form and cached lists
            currentOrder = new Order();
            selectedCustomerId = null;
            selectedProductId = null;
            allOrders = null;

            return "listOrders.xhtml?faces-redirect=true";
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // log rollback error
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error placing order", e.getMessage()));
            return null;
        }
    }


    public String searchOrders() {
        if (searchOrderId == null) {
            searchResults = null; // no results shown if no input
        } else {
            // Search by exact order ID
            Order found = em.find(Order.class, searchOrderId);
            if (found != null) {
                searchResults = List.of(found);
            } else {
                searchResults = List.of();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No order found with ID " + searchOrderId, null));
            }
        }
        return null;  // Stay on same page
    }

     public String resetSearch() {
        searchOrderId = null;
        searchResults = null;
        return null;
    }


}
