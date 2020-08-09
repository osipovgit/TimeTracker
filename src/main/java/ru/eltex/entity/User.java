package ru.eltex.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс представления пользователя.
 *
 * @author Evgeny Osipov
 */
@Data
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
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