package Customer.Beans;

import Order.Beans.Order;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.util.List;

/**
 * Managed Bean for Customer entity operations.
 * Handles CRUD, search, and navigation related to customers.
 */
@Named(value = "customerBean")
@SessionScoped
public class CustomerBean implements Serializable {

    /** EntityManager for JPA persistence */
    @PersistenceContext
    private EntityManager em;

    /** UserTransaction for manual transaction control */
    @Resource
    private UserTransaction utx;

    /** The currently selected or edited customer */
    private Customer currentCustomer;

    /** The search input string for customer name */
    private String searchName;

    /** List to hold search results */
    private List<Customer> searchResults;

    /** Cache of all customers */
    private List<Customer> allCustomers;

    /** Success message to show after operations */
    private String successMessage;

    /** 
     * Returns currentCustomer instance, lazy initialized if null 
     * @return currentCustomer
     */
    public Customer getCurrentCustomer() {
        if (currentCustomer == null) {
            currentCustomer = new Customer();
        }
        return currentCustomer;
    }

    /** Sets the current customer object */
    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    /** Gets the search term for customer name */
    public String getSearchName() {
        return searchName;
    }

    /** Sets the search term */
    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    /** Gets the current list of search results */
    public List<Customer> getSearchResults() {
        return searchResults;
    }

    /** Sets the search results list */
    public void setSearchResults(List<Customer> searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Returns a cached list of all customers.
     * Loads from DB if not cached.
     */
    public List<Customer> getAllCustomers() {
        if (allCustomers == null) {
            allCustomers = em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        }
        return allCustomers;
    }

    /** Sets the cached list of all customers */
    public void setAllCustomers(List<Customer> allCustomers) {
        this.allCustomers = allCustomers;
    }

    /** Gets the current success message */
    public String getSuccessMessage() {
        return successMessage;
    }

    /** Sets a new success message */
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    /**
     * Persists the currentCustomer to the database.
     * Resets caches and messages on success.
     * Returns navigation outcome for customer list page with success message.
     */
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

    /**
     * Searches customers by name using LIKE query.
     * If searchName is empty, resets searchResults to null.
     */
    public void searchCustomers() {
        if (searchName == null || searchName.trim().isEmpty()) {
            searchResults = null;
        } else {
            searchResults = em.createQuery(
                "SELECT c FROM Customer c WHERE LOWER(c.name) LIKE :name", Customer.class)
                .setParameter("name", "%" + searchName.toLowerCase() + "%")
                .getResultList();
        }
    }

    /**
     * Runs the search logic and redirects to the page displaying results.
     * @return navigation string for foundCustomers.xhtml with redirect
     */
    public String searchAndRedirect() {
        searchCustomers();  // run the search logic
        return "foundCustomers.xhtml?faces-redirect=true";  // redirect to the found customer page
    }

    /**
     * Finds a customer by ID and navigates to their detail page.
     * Adds error message if customer not found.
     * @param customerId id of the customer to view
     * @return navigation outcome or null if error
     */
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

    /**
     * Sets the currentCustomer directly and navigates to details page.
     * Useful when the whole customer object is passed.
     * @param customer the customer to view
     * @return navigation outcome
     */
    public String viewCustomer(Customer customer) {
        this.currentCustomer = customer;
        return "customerDetails.xhtml?faces-redirect=true";
    }
}
