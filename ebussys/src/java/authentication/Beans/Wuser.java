package Authentication.Beans;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 *
 * @author saad_
 */
@Entity
@Table(name = "WUSER")
@NamedQueries( {@NamedQuery(name = "Wuser.findById", query = "SELECT w FROM Wuser w WHERE w.id = :id"), 
    @NamedQuery(name = "Wuser.findByFirstname", query = "SELECT w FROM Wuser w WHERE w.firstname = :firstname"), 
    @NamedQuery(name = "Wuser.findByLastname", query = "SELECT w FROM Wuser w WHERE w.lastname = :lastname"), 
    @NamedQuery(name = "Wuser.findByUsername", query = "SELECT w FROM Wuser w WHERE w.username = :username"), 
    @NamedQuery(name = "Wuser.findByPassword", query = "SELECT w FROM Wuser w WHERE w.password = :password"), 
    @NamedQuery(name = "Wuser.findBySince", query = "SELECT w FROM Wuser w WHERE w.since = :since"),
    @NamedQuery(name = "Wuser.findByEmail", query = "SELECT w FROM Wuser w WHERE w.email = :email")})
public class Wuser implements Serializable {

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Column(name = "FIRSTNAME", nullable = false)
    private String firstname;
    @Column(name = "LASTNAME", nullable = false)
    private String lastname;
    @Column(name = "USERNAME", nullable = false)
    private String username;
    @Column(name = "PASSWORD", nullable = false, length=128)
    private String password;
    @Column(name = "SINCE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date since;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    /* Creates a new instance of Wuser */

    /**
     *
     */

    public Wuser() {
    }

    /**
     *
     * @param id
     */
    public Wuser(Integer id) {
        this.id = id;
    }

    /**
     *
     * @param id
     * @param firstname
     * @param lastname
     * @param username
     * @param password
     * @param email
     */
    public Wuser(Integer id, String firstname, String lastname, String username, String password, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email=email;
    }

    /**
     *
     * @return
     */
    public Integer getId() {
        return this.id;
    }

    /**
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     *
     * @param firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     *
     * @return
     */
    public String getLastname() {
        return this.lastname;
    }

    /**
     *
     * @param lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return this.password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {

        this.password = password;
    }

    /**
     *
     * @return
     */
    public Date getSince() {
        return this.since;
    }

    /**
     *
     * @param since
     */
    public void setSince(Date since) {
        this.since = since;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @param object
     * @return
     */
    public boolean equals(Object object) {
        if (object == null || !this.getClass().equals(object.getClass())) {
            return false;
        }
        Wuser other = (Wuser)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    /**
     *
     * @return
     */
    public String toString() {
        return "" + this.id;
    }

}
