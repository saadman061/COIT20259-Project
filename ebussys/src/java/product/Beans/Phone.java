package product.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Entity representing the Phone product.
 * Subclass of Product using JOINED inheritance strategy.
 * This class stores phone-specific properties like cellular connectivity,
 * location features, and SIM card type.
 * 
 * Mapped to the "PHONE" table in the database.
 * The primary key is joined with the superclass Product via "id".
 * 
 * Author: Ronak
 */
@Entity
@Table(name = "PHONE")
@PrimaryKeyJoinColumn(name = "id")  // Joins with PRODUCT's primary key
public class Phone extends Product {

    /** Cellular connectivity information, e.g., "4G", "5G" */
    private String cellular;

    /** Location features, e.g., GPS */
    private String location;

    /** SIM card type, e.g., "Nano", "Micro" */
    private String simCard;

    /** Default constructor */
    public Phone() {}

    /**
     * Gets the cellular connectivity type.
     * @return cellular cellular connectivity description
     */
    public String getCellular() {
        return cellular;
    }

    /**
     * Sets the cellular connectivity type.
     * @param cellular cellular connectivity description
     */
    public void setCellular(String cellular) {
        this.cellular = cellular;
    }

    /**
     * Gets the location feature description.
     * @return location location features (e.g., GPS)
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location feature description.
     * @param location location features (e.g., GPS)
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the SIM card type.
     * @return simCard SIM card type description
     */
    public String getSimCard() {
        return simCard;
    }

    /**
     * Sets the SIM card type.
     * @param simCard SIM card type description
     */
    public void setSimCard(String simCard) {
        this.simCard = simCard;
    }
}
