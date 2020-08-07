package ru.eltex.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.eltex.entity.TimeTrack;

import java.util.List;


/**
 * Интерфейс Time Track repository.
 */
public interface TimeTrackRepo extends JpaRepository<TimeTrack, Long> {
    /**
     * Find task by user_id and title.
     *
     * @param taskId the task id
     * @return List of Time_track
     */
    List<TimeTrack> findAllByTaskIdOrderByDate(Long taskId);

    /**
     * Find top task by user_id and title order desc.
     *
     * @param taskId the task id
     * @return Time_track
     */
    TimeTrack findTopByTaskIdOrderByDateDesc(Long taskId);

    /**
     * Find task by taskId and date.
     *
     * @param taskId the task id
     * @param date   the date
     * @return Time track entity
     */
    TimeTrack findByTaskIdAndDate(Long taskId, Long date);

    /**
     * Delete all by taskId.
     *
     * @param taskId the task id
     */
    @Modifying
    @Transactional
    void deleteAllByTaskId(@Param("taskId") Long taskId);

    /**
     * Update current time by taskId and date.
     *
     * @param taskId      the task id
     * @param date         the date
     * @param spentTime the current time
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update time_track set spent_time = :spentTime where task_id =:taskId and date =:date")
    void updateSpentTime(@Param("taskId") Long taskId, @Param("date") Long date, @Param("spentTime") Long spentTime);

    /**
     * Update start time by taskId and date.
     *
     * @param taskId    the task id
     * @param date       the date
     * @param startTime the start time
     */
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update time_track set start_time = :startTime where task_id =:taskId and date =:date")
    void updateStartTime(@Param("taskId") Long taskId, @Param("date") Long date, @Param("startTime") Long startTime);
}
