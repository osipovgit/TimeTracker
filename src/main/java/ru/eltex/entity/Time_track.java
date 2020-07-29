package ru.eltex.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Класс представления времени выполнения задач пользователями.
 *
 * @author Evgeny Osipov
 */
@Data
@Entity
@Table(name = "time_track")
public class Time_track {
    /**
     * Поле идентификатора времени, потраченного на задачу.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Поле идентификатора задачи.
     */
    private Long task_id;
    /**
     * Поле даты.
     */
    private Date date;
    /**
     * Поле заголовка.
     */
    private Long time;
}