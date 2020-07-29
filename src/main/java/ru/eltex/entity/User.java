package ru.eltex.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Класс представления пользователя.
 *
 * @author Evgeny Osipov
 */
@Data
@Entity
@Table(name = "user")
public class User {
    /**
     * Поле идентификатора пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Поле имени пользователя.
     */
    private String username;
    /**
     * Поле пароля.
     */
    private String password;
    /**
     * Поле имени.
     */
    private String first_name;
    /**
     * Поле фамилии.
     */
    private String last_name;
}