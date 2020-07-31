package ru.eltex.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.eltex.entity.User;

/**
 * Интерфейс User repository.
 */
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Find by username.
     *
     * @param username String
     * @return User
     */
    User findByUsername(String username);

    /**
     * Delete by username.
     *
     * @param username the username
     */
    @Modifying
    @Transactional
    void deleteByUsername(@Param("username") String username);

    /**
     * Update username.
     *
     * @param username the username
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set username = :username where username =:username")
    void updateUsername(@Param("username") String username);

    /**
     * Update first_name by username.
     *
     * @param first_name the first_name
     * @param username   the username
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set first_name = :first_name where username =:username")
    void updateFirstName(@Param("username") String username, @Param("first_name") String first_name);

    /**
     * Update first_name by username.
     *
     * @param last_name the last_name
     * @param username  the username
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set last_name = :last_name where username =:username")
    void updateLastName(@Param("username") String username, @Param("last_name") String last_name);

    /**
     * Update first_name by username.
     *
     * @param password the password
     * @param username the username
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set password = :password where username =:username")
    void updatePassword(@Param("username") String username, @Param("password") String password);
}
