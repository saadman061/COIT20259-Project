/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Order.Beans;

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

@Named(value = "orderBean")
@SessionScoped
public class OrderBean implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private Order currentOrder = new Order();
    private String searchEmail;
    private List<Order> searchResults;
    private List<Order> allOrders;

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public String getSearchEmail() {
        return searchEmail;
    }

    public void setSearchEmail(String searchEmail) {
        this.searchEmail = searchEmail;
    }

    public List<Order> getSearchResults() {
        return searchResults;
    }

    public List<Order> getAllOrders() {
        if (allOrders == null) {
            allOrders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        }
        return allOrders;
    }

    public String placeOrder() {
        try {
            utx.begin();
            em.persist(currentOrder);
            utx.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Order placed successfully for " + currentOrder.getProductModel()));

            currentOrder = new Order();
            allOrders = null;

            return "stockOrders.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error placing order", e.getMessage()));
            return null;
        }
    }

    public void searchOrders() {
        if (searchEmail == null || searchEmail.trim().isEmpty()) {
            searchResults = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        } else {
            searchResults = em.createQuery("SELECT o FROM Order o WHERE LOWER(o.email) LIKE :email", Order.class)
                    .setParameter("email", "%" + searchEmail.toLowerCase() + "%")
                    .getResultList();
        }
    }

    public void resetSearch() {
        searchEmail = null;
        searchResults = null;
    }
}