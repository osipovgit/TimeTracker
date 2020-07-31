package ru.eltex.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.eltex.entity.Task;

import java.util.List;


/**
 * Интерфейс Task repository.
 */
public interface TaskRepo extends JpaRepository<Task, Long> {
    /**
     * Find task by user_id and title.
     *
     * @param user_id the user id
     * @param title   the title of task
     * @return Task
     */
    Task findByUser_idAndTitle(Long user_id, String title);

    /**
     * Find all tasks by user_id.
     *
     * @param user_id the user id
     * @return list tasks
     */
    List<Task> findAllByUser_id(Long user_id);

    /**
     * Delete by user_id and title.
     *
     * @param user_id the user id
     * @param title   the title
     */
    @Modifying
    @Transactional
    void deleteByUser_idAndTitle(@Param("user_id") Long user_id, @Param("title") String title);

    /**
     * Delete all by user_id.
     *
     * @param user_id the user id
     */
    @Modifying
    @Transactional
    void deleteAllByUser_id(@Param("user_id") Long user_id);
}
