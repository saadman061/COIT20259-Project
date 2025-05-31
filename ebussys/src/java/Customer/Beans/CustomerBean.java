package Customer.Beans;

import Order.Beans.Order;
import jakarta.annotation.PostConstruct;
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
import java.util.Date;
import java.util.List;

@Named(value = "customerBean")
@SessionScoped
public class CustomerBean implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private Customer currentCustomer;
    private String searchName;
    private List<Customer> searchResults;
    private List<Customer> allCustomers;

    private String successMessage;

    public Customer getCurrentCustomer() {
        if (currentCustomer == null) {
            currentCustomer = new Customer();
        }
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public List<Customer> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Customer> searchResults) {
        this.searchResults = searchResults;
    }

    public List<Customer> getAllCustomers() {
        if (allCustomers == null) {
            allCustomers = em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        }
        return allCustomers;
    }

    public void setAllCustomers(List<Customer> allCustomers) {
        this.allCustomers = allCustomers;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    // Create and persist a new customer
    public String createCustomer() {
        try {
            utx.begin();
            em.persist(getCurrentCustomer());
            utx.commit();

            successMessage = "Successfully created the customer: " + currentCustomer.getName();
            currentCustomer = null;
            allCustomers = null;
            searchResults = null;

            return "listCustomers.xhtml?faces-redirect=true&success=true&message=" + successMessage;

        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception rollbackEx) {
                // Log rollback error
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating customer", e.getMessage()));
            return null;
        }
    }

    // Search for customers by name
    public void searchCustomers() {
        if (searchName == null || searchName.trim().isEmpty()) {
            searchResults = null; // no results shown if no input
        } else {
            searchResults = em.createQuery(
                "SELECT c FROM Customer c WHERE LOWER(c.name) LIKE :name", Customer.class)
                .setParameter("name", "%" + searchName.toLowerCase() + "%")
                .getResultList();
        }
    }

    // Reset search
    public void resetSearch() {
        searchName = null;
        searchResults = null;
    }

    // View customer details by ID (used in View Details link/button)
    public String viewCustomerDetails(int customerId) {
        this.currentCustomer = em.find(Customer.class, customerId);
        if (currentCustomer != null) {
            return "customerDetails.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Customer not found", "No customer with the given ID"));
            return null;
        }
    }

    // Alternative: set currentCustomer directly (used if passing whole customer object)
    public String viewCustomer(Customer customer) {
        this.currentCustomer = customer;
        return "customerDetails.xhtml?faces-redirect=true";
    }

}
