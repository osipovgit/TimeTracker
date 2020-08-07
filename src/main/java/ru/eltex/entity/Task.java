package ru.eltex.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Класс представления задачи.
 *
 * @author Evgeny Osipov
 */
@Data
@Entity
@Table(name = "task")
public class Task {
    /**
     * Поле идентификатора задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Поле идентификатора пользователя.
     */
    private Long userId;
    /**
     * Поле заголовка.
     * Unique.
     */
    private String title;
}