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
 * Managed bean for Phone product operations.
 * Handles CRUD and search functionalities for Phone entities.
 * Uses JPA for persistence and manages transactions explicitly.
 * SessionScoped to maintain state during a user session.
 * 
 * Author: Ronak (original code), documented by ChatGPT
 */
@Named(value = "phoneBean")
@SessionScoped
public class PhoneBean implements Serializable {

    /** JPA EntityManager for performing DB operations */
    @PersistenceContext
    private EntityManager em;

    /** UserTransaction to manually control transaction boundaries */
    @Resource
    private UserTransaction utx;

    /** Currently selected or new Phone entity for create/edit */
    private Phone currentPhone;

    /** Search keyword to filter phones by model */
    private String searchModel;

    /** List of phones matching the last search */
    private List<Phone> searchResults;

    /** Cached list of all phones from the database */
    private List<Phone> allPhones;

    /** Message to communicate operation success to UI */
    private String successMessage;

    /**
     * Returns the current Phone instance.
     * Lazily initializes a new Phone object if null.
     * 
     * @return currentPhone the Phone entity currently selected or new.
     */
    public Phone getCurrentPhone() {
        if (currentPhone == null) {
            currentPhone = new Phone();
        }
        return currentPhone;
    }

    /**
     * Sets the current Phone instance.
     * 
     * @param currentPhone the Phone object to set as current.
     */
    public void setCurrentPhone(Phone currentPhone) {
        this.currentPhone = currentPhone;
    }

    /**
     * Gets the current search model string.
     * 
     * @return searchModel string used to filter phones by model.
     */
    public String getSearchModel() {
        return searchModel;
    }

    /**
     * Sets the search model string.
     * 
     * @param searchModel search keyword for phone model filtering.
     */
    public void setSearchModel(String searchModel) {
        this.searchModel = searchModel;
    }

    /**
     * Returns the list of phones matching the last search.
     * 
     * @return searchResults list of Phone entities matching search criteria.
     */
    public List<Phone> getSearchResults() {
        return searchResults;
    }

    /**
     * Sets the list of phones to be displayed as search results.
     * 
     * @param searchResults list of phones after search operation.
     */
    public void setSearchResults(List<Phone> searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Returns the cached list of all phones.
     * Loads from the database if cache is empty.
     * 
     * @return allPhones list of all Phone entities.
     */
    public List<Phone> getAllPhones() {
        if (allPhones == null) {
            allPhones = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList();
        }
        return allPhones;
    }

    /**
     * Sets the cached list of all phones.
     * 
     * @param allPhones list of all Phone entities to cache.
     */
    public void setAllPhones(List<Phone> allPhones) {
        this.allPhones = allPhones;
    }

    /**
     * Returns the success message for UI display.
     * 
     * @return successMessage the success message string.
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Sets the success message for UI display.
     * 
     * @param successMessage the success message string to set.
     */
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    /**
     * Creates a new Phone entity by persisting it to the database.
     * Manages transaction begin and commit.
     * Resets current phone and cached lists on success.
     * Displays FacesMessage on success or failure.
     * 
     * @return navigation string for redirection on success, null on failure.
     */
    public String createPhone() {
        try {
            utx.begin();
            em.persist(getCurrentPhone());
            utx.commit();

            successMessage = "Successfully created the phone: " + currentPhone.getModel();
            currentPhone = null;   // Reset form object
            allPhones = null;      // Invalidate cached list
            searchResults = null;  // Clear previous search results

            return "stockPhones.xhtml?faces-redirect=true&success=true&message=" + successMessage;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating phone", e.getMessage()));
            return null;  // Stay on current page
        }
    }

    /**
     * Searches phones by matching the model with the search keyword.
     * If the search keyword is empty or null, clears the search results.
     * Otherwise performs a case-insensitive LIKE search.
     */
    public void searchPhones() {
        if (searchModel == null || searchModel.trim().isEmpty()) {
            searchResults = null;
        } else {
            searchResults = em.createQuery("SELECT p FROM Phone p WHERE LOWER(p.model) LIKE :model", Phone.class)
                    .setParameter("model", "%" + searchModel.toLowerCase() + "%")
                    .getResultList();
        }
    }

    /**
     * Runs the search operation and returns navigation string
     * to redirect to the found phones page.
     * 
     * @return navigation string to foundPhones.xhtml with redirect.
     */
    public String searchAndRedirect() {
        searchPhones();  // Perform search query
        return "foundPhones.xhtml?faces-redirect=true";
    }
}
