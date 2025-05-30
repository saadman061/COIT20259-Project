package product.beans;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;

/**
 * Abstract superclass for all product types.
 * Uses JOINED inheritance strategy for mapping subclasses to separate tables.
 * Includes common properties shared by all products.
 * 
 * The discriminator column "DTYPE" indicates the concrete subclass type.
 * 
 * Implements Serializable for entity transfer and storage.
 * 
 * Example subclasses: Laptop, Phone, etc.
 * 
 */
@Entity
@Table(name = "PRODUCT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Product implements Serializable {

    /** Primary key, auto-generated ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Brand name of the product */
    private String brand;

    /** Model name or number */
    private String model;

    /** Display size in inches */
    private double displaySize;

    /** Weight in grams */
    private int weight;

    /** Operating system of the product */
    private String operatingSystem;

    /** Camera specifications */
    private String camera;

    /** Wi-Fi capability details */
    private String wifi;

    /** Price of the product */
    private double price;

    /** Description or notes */
    private String description;

    /** Number of items currently in stock */
    private int stockNumber;

    /** Default constructor */
    public Product() {}

    // === Getters and Setters === //

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getDisplaySize() {
        return displaySize;
    }

    public void setDisplaySize(double displaySize) {
        this.displaySize = displaySize;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(int stockNumber) {
        this.stockNumber = stockNumber;
    }
}
