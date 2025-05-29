/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package product.beans;

/**
 *
 * @author Ronak
 */


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

@Named(value = "phoneBean")
@SessionScoped
public class PhoneBean implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private Phone currentPhone;

    private String searchModel;
    private List<Phone> searchResults;
    private List<Phone> allPhones;

    private String successMessage;

    // Lazy initialization of currentPhone
    public Phone getCurrentPhone() {
        if (currentPhone == null) {
            currentPhone = new Phone();
        }
        return currentPhone;
    }

    public void setCurrentPhone(Phone currentPhone) {
        this.currentPhone = currentPhone;
    }

    public String getSearchModel() {
        return searchModel;
    }

    public void setSearchModel(String searchModel) {
        this.searchModel = searchModel;
    }

    public List<Phone> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Phone> searchResults) {
        this.searchResults = searchResults;
    }

    public List<Phone> getAllPhones() {
        if (allPhones == null) {
            allPhones = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList();
        }
        return allPhones;
    }

    public void setAllPhones(List<Phone> allPhones) {
        this.allPhones = allPhones;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    // Create a new phone
    public String createPhone() {
        try {
            utx.begin();
            em.persist(getCurrentPhone());
            utx.commit();

            successMessage = "Successfully created the phone: " + currentPhone.getModel();
            currentPhone = null;
            allPhones = null;
            searchResults = null;

            return "stockPhones.xhtml?faces-redirect=true&success=true&message=" + successMessage;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating phone", e.getMessage()));
            return null;
        }
    }

    // Search phones by model
    public void searchPhones() {
        if (searchModel == null || searchModel.trim().isEmpty()) {
            searchResults = em.createQuery("SELECT p FROM Phone p", Phone.class).getResultList();
        } else {
            searchResults = em.createQuery("SELECT p FROM Phone p WHERE LOWER(p.model) LIKE :model", Phone.class)
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

