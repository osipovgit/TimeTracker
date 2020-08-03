package ru.eltex.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Класс представления времени выполнения задач пользователями.
 * Хранит в себе объект с задачей и потраченное на нее время за определенную дату.
 * Один день - один объект с одной задачей, время, затраченное на нее, и поле времени начала отсчета по данной задаче.
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
    private Long date;
    /**
     * Поле потраченного за день времени (cek).
     */
    private Long current_time;
    /**
     * Поле времени начала отсчета.
     */
    private Long start_time;
}