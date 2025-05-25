/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package ebussys;

import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;

import ebussys.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

/**
 *
 * @author Saadman
 */

@Stateless
public class UserService {
    @PersistenceContext(unitName = "EBUSSYSPU")
    private EntityManager em;

    public void registerUser(User user) {
        em.persist(user);
    }

    public User findByEmail(String email) {
        try {
            return em.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findByUsername(String username) {
        try {
            return em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    // Verify email using verification code
    public boolean verifyUser(String email, String code) {
        User user = findByEmail(email);
        if (user != null && code.equals(user.getVerificationCode())) {
            user.setVerified(true);
            user.setVerificationCode(null);
            em.merge(user);
            return true;
        }
        return false;
    }

    // Validate login credentials (hashed password)
    public boolean validateLogin(String username, String passwordHash) {
        User user = findByUsername(username);
        return user != null && user.isVerified() && user.getPasswordHash().equals(passwordHash);
    }

    // Initiate recovery - set recovery code
    public boolean initiateRecovery(String email, String code) {
        User user = findByEmail(email);
        if (user != null) {
            user.setRecoveryCode(code);
            em.merge(user);
            return true;
        }
        return false;
    }

    // Reset password using recovery code
    public boolean resetPassword(String email, String recoveryCode, String newPasswordHash) {
        User user = findByEmail(email);
        if (user != null && recoveryCode.equals(user.getRecoveryCode())) {
            user.setPasswordHash(newPasswordHash);
            user.setRecoveryCode(null);
            em.merge(user);
            return true;
        }
        return false;
    }
}
