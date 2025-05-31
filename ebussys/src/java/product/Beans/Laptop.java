package product.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Entity representing the Laptop product.
 * Subclass of Product using JOINED inheritance strategy.
 * Stores laptop-specific properties such as network interfaces,
 * hard drive details, and available ports.
 * 
 * Mapped to the "LAPTOP" table in the database.
 * The primary key is joined with the superclass Product via "id".
 * 
 * Author: Ronak
 */
@Entity
@Table(name = "LAPTOP")
@PrimaryKeyJoinColumn(name = "id")  // Joins with PRODUCT's primary key
public class Laptop extends Product {

    /** Network interface details, e.g., "1000G Ethernet LAN" */
    private String networkInterface;

    /** Hard drive specifications */
    private String hardDrive;

    /** Available ports on the laptop */
    private String ports;

    /** Default constructor */
    public Laptop() {}

    /**
     * Gets the network interface specification.
     * @return networkInterface network interface details
     */
    public String getNetworkInterface() {
        return networkInterface;
    }

    /**
     * Sets the network interface specification.
     * @param networkInterface network interface details
     */
    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    /**
     * Gets the hard drive specification.
     * @return hardDrive hard drive details
     */
    public String getHardDrive() {
        return hardDrive;
    }

    /**
     * Sets the hard drive specification.
     * @param hardDrive hard drive details
     */
    public void setHardDrive(String hardDrive) {
        this.hardDrive = hardDrive;
    }

    /**
     * Gets the ports specification.
     * @return ports details of available ports
     */
    public String getPorts() {
        return ports;
    }

    /**
     * Sets the ports specification.
     * @param ports details of available ports
     */
    public void setPorts(String ports) {
        this.ports = ports;
    }
}
