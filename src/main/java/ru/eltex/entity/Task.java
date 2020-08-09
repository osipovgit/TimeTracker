package ru.eltex.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс представления задачи.
 *
 * @author Evgeny Osipov
 */
@Data
@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
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