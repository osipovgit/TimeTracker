package ru.eltex.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
@AllArgsConstructor
@NoArgsConstructor
public class TimeTrack {
    /**
     * Поле идентификатора времени, потраченного на задачу.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Поле идентификатора задачи.
     */
    private Long taskId;
    /**
     * Поле даты.
     * Хранится в формате: Date().getTime() / 86400000
     */
    private Long date;
    /**
     * Поле потраченного за день времени (cek).
     */
    private Long spentTime;
    /**
     * Поле времени начала отсчета.
     * null - задача приостановлена
     * Date().getTime() - время начала отсчета
     */
    private Long startTime;
}