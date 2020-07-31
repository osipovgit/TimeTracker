package ru.eltex.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.eltex.entity.Task;
import ru.eltex.entity.Time_track;

import java.util.Date;
import java.util.List;


/**
 * Интерфейс Time Track repository.
 */
public interface TimeTrackRepo extends JpaRepository<Time_track, Long> {
    /**
     * Find task by user_id and title.
     *
     * @param task_id the task id
     * @return List of Time_track
     */
    List<Time_track> findAllByTask_id(Long task_id);

    /**
     * Find task by task_id and date.
     *
     * @param task_id the task id
     * @param date    the date
     * @return Time track entity
     */
    Time_track findByTask_idAndDate(Long task_id, Date date);

    /**
     * Delete all by task_id.
     *
     * @param task_id the task id
     */
    @Modifying
    @Transactional
    void deleteAllByTask_id(@Param("task_id") Long task_id);

    /**
     * Update current time by task_id and date.
     *
     * @param task_id      the task id
     * @param date         the date
     * @param current_time the current time
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set 'current_time' = :'current_time' where task_id =:task_id and date =:date")
    void updateCurrentTime(@Param("task_id") String task_id, @Param("date") String date, @Param("current_time") String current_time);

    /**
     * Update start time by task_id and date.
     *
     * @param task_id    the task id
     * @param date       the date
     * @param start_time the start time
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set start_time = :start_time where task_id =:task_id and date =:date")
    void updateStartTime(@Param("task_id") String task_id, @Param("date") String date, @Param("start_time") String start_time);
}
