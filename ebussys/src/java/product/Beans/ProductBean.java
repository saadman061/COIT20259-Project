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

@Named(value = "productBean")
@SessionScoped
public class ProductBean implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private Laptop currentLaptop;  // changed from Product

    private String searchModel;
    private List<Laptop> searchResults;
    private List<Laptop> allLaptops;

    private String successMessage;

    public Laptop getCurrentLaptop() {
        if (currentLaptop == null) {
            currentLaptop = new Laptop();
        }
        return currentLaptop;
    }

    public void setCurrentLaptop(Laptop currentLaptop) {
        this.currentLaptop = currentLaptop;
    }

    public String getSearchModel() {
        return searchModel;
    }

    public void setSearchModel(String searchModel) {
        this.searchModel = searchModel;
    }

    public List<Laptop> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Laptop> searchResults) {
        this.searchResults = searchResults;
    }

    public List<Laptop> getAllLaptops() {
        if (allLaptops == null) {
            allLaptops = em.createQuery("SELECT p FROM Laptop p", Laptop.class).getResultList();
        }
        return allLaptops;
    }

    public void setAllLaptops(List<Laptop> allLaptops) {
        this.allLaptops = allLaptops;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String createLaptop() {
        try {
            utx.begin();
            em.persist(getCurrentLaptop());
            utx.commit();

            successMessage = "Successfully created the laptop: " + currentLaptop.getModel();
            currentLaptop = null;
            allLaptops = null;
            searchResults = null;

            return "stockLaptops.xhtml?faces-redirect=true&success=true&message=" + successMessage;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating laptop", e.getMessage()));
            return null;
        }
    }

    public void searchLaptops() {
        if (searchModel == null || searchModel.trim().isEmpty()) {
            searchResults = null; // no results shown if no input
        } else {
            searchResults = em.createQuery("SELECT p FROM Laptop p WHERE LOWER(p.model) LIKE :model", Laptop.class)
                    .setParameter("model", "%" + searchModel.toLowerCase() + "%")
                    .getResultList();
        }
    }

    public void resetSearch() {
        searchModel = null;
        searchResults = null;
    }
}
