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
     * Find task by userId and title.
     *
     * @param userId the user id
     * @param title  the title of task
     * @return Task
     */
    Task findByUserIdAndTitle(Long userId, String title);

    /**
     * Find all tasks by userId.
     *
     * @param userId the user id
     * @return list tasks
     */
    List<Task> findAllByUserIdOrderById(Long userId);

    /**
     * Delete by userId and title.
     *
     * @param userId the user id
     * @param title   the title
     */
    @Modifying
    @Transactional
    void deleteByUserIdAndTitle(@Param("userId") Long userId, @Param("title") String title);

    /**
     * Delete all by userId.
     *
     * @param userId the user id
     */
    @Modifying
    @Transactional
    void deleteAllByUserId(@Param("userId") Long userId);
}
