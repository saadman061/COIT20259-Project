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

    private Product currentProduct;  // lazy init

    private String searchModel;
    private List<Product> searchResults;
    private List<Product> allProducts; // stock list cache

    private String successMessage;

    // Lazy initialization of currentProduct
    public Product getCurrentProduct() {
        if (currentProduct == null) {
            currentProduct = new Product();
        }
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public String getSearchModel() {
        return searchModel;
    }

    public void setSearchModel(String searchModel) {
        this.searchModel = searchModel;
    }

    public List<Product> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Product> searchResults) {
        this.searchResults = searchResults;
    }

    public List<Product> getAllProducts() {
        if (allProducts == null) {
            allProducts = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        }
        return allProducts;
    }

    public void setAllProducts(List<Product> allProducts) {
        this.allProducts = allProducts;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    // Create a new product
    public String createProduct() {
        try {
            utx.begin();
            em.persist(getCurrentProduct());
            utx.commit();

            successMessage = "Successfully created the product: " + currentProduct.getModel();
            currentProduct = null; // reset for next input
            allProducts = null;    // refresh stock cache
            searchResults = null;  // clear search results

            // stay on same page or redirect to stock page
            return "stockLaptops.xhtml?faces-redirect=true&success=true&message=" + successMessage;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating product", e.getMessage()));
            return null;
        }
    }

    // Search products by model
    public void searchProducts() {
        if (searchModel == null || searchModel.trim().isEmpty()) {
            searchResults = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        } else {
            searchResults = em.createQuery("SELECT p FROM Product p WHERE LOWER(p.model) LIKE :model", Product.class)
                    .setParameter("model", "%" + searchModel.toLowerCase() + "%")
                    .getResultList();
        }
    }

    // Reset search
    public void resetSearch() {
        searchModel = null;
        searchResults = null;
    }

}