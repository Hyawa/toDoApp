package br.com.lucas.testeMySQL.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.lucas.testeMySQL.model.Task;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Buscar tarefas por status de conclusão
     */
    List<Task> findByCompleted(boolean completed);
    
    /**
     * Buscar tarefas que contenham um texto no título (case-insensitive)
     */
    List<Task> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Buscar tarefas que contenham um texto na descrição (case-insensitive)
     */
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Buscar tarefas criadas após uma data específica
     */
    List<Task> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Buscar tarefas criadas antes de uma data específica
     */
    List<Task> findByCreatedAtBefore(LocalDateTime date);
    
    /**
     * Buscar tarefas criadas entre duas datas
     */
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Buscar tarefas por título exato (case-insensitive)
     */
    List<Task> findByTitleIgnoreCase(String title);
    
    /**
     * Contar tarefas por status de conclusão
     */
    long countByCompleted(boolean completed);
    
    /**
     * Buscar tarefas ordenadas por data de criação (mais recentes primeiro)
     */
    List<Task> findAllByOrderByCreatedAtDesc();
    
    /**
     * Buscar tarefas ordenadas por título
     */
    List<Task> findAllByOrderByTitleAsc();
    
    /**
     * Query customizada: buscar tarefas não concluídas mais antigas que X dias
     */
    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.createdAt < :cutoffDate")
    List<Task> findOldUncompletedTasks(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Query customizada: buscar por título ou descrição
     */
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Task> findByTitleOrDescriptionContaining(@Param("searchText") String searchText);
    
    /**
     * Query nativa: estatísticas básicas
     */
    @Query(value = "SELECT " +
                   "COUNT(*) as total, " +
                   "SUM(CASE WHEN completed = true THEN 1 ELSE 0 END) as completed, " +
                   "SUM(CASE WHEN completed = false THEN 1 ELSE 0 END) as pending " +
                   "FROM tasks", 
           nativeQuery = true)
    Object[] getTaskStatistics();
}