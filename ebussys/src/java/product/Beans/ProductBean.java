package product.beans;

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
 * Managed bean for Laptop product operations.
 * Handles CRUD and search operations on Laptop entities.
 * Uses JPA for persistence and manages transactions.
 * SessionScoped to maintain state during user session.
 * 
 * @author Saadman
 */
@Named(value = "productBean")
@SessionScoped
public class ProductBean implements Serializable {

    /** JPA EntityManager for database operations */
    @PersistenceContext
    private EntityManager em;

    /** UserTransaction to manage manual transactions */
    @Resource
    private UserTransaction utx;

    /** The Laptop entity currently being created or edited */
    private Laptop currentLaptop;

    /** Search string input to filter laptops by model */
    private String searchModel;

    /** List to hold laptops returned from search queries */
    private List<Laptop> searchResults;

    /** Cached list of all laptops to minimize database calls */
    private List<Laptop> allLaptops;

    /** Message indicating success status of operations */
    private String successMessage;

    /**
     * Gets the current Laptop instance.
     * If none exists, creates a new Laptop object.
     * 
     * @return currentLaptop The current Laptop instance for create/edit.
     */
    public Laptop getCurrentLaptop() {
        if (currentLaptop == null) {
            currentLaptop = new Laptop();
        }
        return currentLaptop;
    }

    /**
     * Sets the current Laptop instance.
     * 
     * @param currentLaptop The Laptop object to set as current.
     */
    public void setCurrentLaptop(Laptop currentLaptop) {
        this.currentLaptop = currentLaptop;
    }

    /**
     * Gets the current search model string used for filtering laptops.
     * 
     * @return searchModel The current search input string.
     */
    public String getSearchModel() {
        return searchModel;
    }

    /**
     * Sets the search model string used to filter laptops.
     * 
     * @param searchModel The input string to filter laptops by model.
     */
    public void setSearchModel(String searchModel) {
        this.searchModel = searchModel;
    }

    /**
     * Gets the list of laptops returned from the last search query.
     * 
     * @return searchResults List of laptops matching the search criteria.
     */
    public List<Laptop> getSearchResults() {
        return searchResults;
    }

    /**
     * Sets the list of laptops to be displayed as search results.
     * 
     * @param searchResults The list of laptops to display.
     */
    public void setSearchResults(List<Laptop> searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Gets the cached list of all laptops from the database.
     * Queries database only if cache is empty.
     * 
     * @return allLaptops List of all laptops.
     */
    public List<Laptop> getAllLaptops() {
        if (allLaptops == null) {
            allLaptops = em.createQuery("SELECT p FROM Laptop p", Laptop.class).getResultList();
        }
        return allLaptops;
    }

    /**
     * Sets the cached list of all laptops.
     * 
     * @param allLaptops The list of all laptops to cache.
     */
    public void setAllLaptops(List<Laptop> allLaptops) {
        this.allLaptops = allLaptops;
    }

    /**
     * Gets the success message to be displayed after operations.
     * 
     * @return successMessage The current success message.
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Sets the success message to be displayed after operations.
     * 
     * @param successMessage The success message to set.
     */
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    /**
     * Persists the current Laptop entity into the database.
     * Begins a transaction, persists the entity, commits the transaction.
     * Resets the currentLaptop and cached lists on success.
     * Shows FacesMessage on success or error.
     * 
     * @return navigation string to redirect to laptop list page on success, null on error.
     */
    public String createLaptop() {
        try {
            utx.begin();
            em.persist(getCurrentLaptop());
            utx.commit();

            successMessage = "Successfully created the laptop: " + currentLaptop.getModel();
            currentLaptop = null;   // reset form
            allLaptops = null;      // invalidate cache to refresh
            searchResults = null;   // clear search results

            // Redirect to laptops listing page with success message
            return "stockLaptops.xhtml?faces-redirect=true&success=true&message=" + successMessage;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating laptop", e.getMessage()));
            return null;  // stay on current page on error
        }
    }

    /**
     * Searches for laptops by matching model name.
     * If searchModel is empty or null, clears the search results.
     * Otherwise, performs a case-insensitive LIKE query on Laptop model field.
     */
    public void searchLaptops() {
        if (searchModel == null || searchModel.trim().isEmpty()) {
            searchResults = null;
        } else {
            searchResults = em.createQuery("SELECT p FROM Laptop p WHERE LOWER(p.model) LIKE :model", Laptop.class)
                    .setParameter("model", "%" + searchModel.toLowerCase() + "%")
                    .getResultList();
        }
    }

    /**
     * Performs search and then redirects to the search results page.
     * 
     * @return navigation string to the found laptops page.
     */
    public String searchAndRedirect() {
        searchLaptops();  // run the search logic
        return "foundLaptops.xhtml?faces-redirect=true";  // redirect to results page
    }
}
