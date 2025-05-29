/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package product.beans;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
/**
 *
 * @author Ronak
 */
@Entity
@Table(name = "PHONE")
@PrimaryKeyJoinColumn(name = "id")
public class Phone extends Product {

    private String simType;
    private String cellularConnectivity;
    private String locationFeatures;

    public Phone() {}

    // Getters and Setters
    public String getSimType() {
        return simType;
    }

    public void setSimType(String simType) {
        this.simType = simType;
    }

    public String getCellularConnectivity() {
        return cellularConnectivity;
    }

    public void setCellularConnectivity(String cellularConnectivity) {
        this.cellularConnectivity = cellularConnectivity;
    }

    public String getLocationFeatures() {
        return locationFeatures;
    }

    public void setLocationFeatures(String locationFeatures) {
        this.locationFeatures = locationFeatures;
    }
}
