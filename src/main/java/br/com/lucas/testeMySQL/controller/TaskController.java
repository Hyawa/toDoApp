package br.com.lucas.testeMySQL.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.lucas.testeMySQL.model.Task;
import br.com.lucas.testeMySQL.repository.TaskRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class TaskController {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Método para adicionar tarefa com apenas título (método original)
     */
    public void addTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(""); // Descrição vazia por padrão
        task.setCompleted(false); // Não concluída por padrão
        task.setCreatedAt(LocalDateTime.now());
        
        taskRepository.save(task);
        System.out.println("Tarefa adicionada: " + title);
    }

    /**
     * Buscar todas as tarefas
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Criar nova tarefa (método completo)
     */
    public Task createTask(Task newTask) {
        if (newTask.getTitle() == null || newTask.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da tarefa é obrigatório");
        }
        
        // Garantir que os campos obrigatórios estejam definidos
        if (newTask.getCreatedAt() == null) {
            newTask.setCreatedAt(LocalDateTime.now());
        }
        
        if (newTask.getDescription() == null) {
            newTask.setDescription("");
        }
        
        Task savedTask = taskRepository.save(newTask);
        System.out.println("Tarefa criada com ID: " + savedTask.getId());
        return savedTask;
    }

    /**
     * Buscar tarefa por ID
     */
    public Task getTaskById(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser null");
        }
        
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            return taskOptional.get();
        } else {
            throw new RuntimeException("Tarefa não encontrada com ID: " + taskId);
        }
    }

    /**
     * Atualizar tarefa existente
     */
    public Task updateTask(Long id, Task taskDetails) {
        if (id == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser null");
        }
        
        if (taskDetails.getTitle() == null || taskDetails.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da tarefa é obrigatório");
        }
        
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task existingTask = taskOptional.get();
            
            // Atualizar apenas os campos fornecidos
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setCompleted(taskDetails.isCompleted());
            
            // Manter o createdAt original, não atualizar
            // existingTask.setCreatedAt() - não alterar
            
            Task updatedTask = taskRepository.save(existingTask);
            System.out.println("Tarefa atualizada com ID: " + updatedTask.getId());
            return updatedTask;
        } else {
            throw new RuntimeException("Tarefa não encontrada com ID: " + id);
        }
    }

    /**
     * Deletar tarefa por ID
     */
    public void deleteTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser null");
        }
        
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            System.out.println("Tarefa deletada com ID: " + taskId);
        } else {
            throw new RuntimeException("Tarefa não encontrada com ID: " + taskId);
        }
    }
    
    /**
     * Verificar se uma tarefa existe
     */
    public boolean existsById(Long taskId) {
        return taskId != null && taskRepository.existsById(taskId);
    }
    
    /**
     * Contar total de tarefas
     */
    public long countTasks() {
        return taskRepository.count();
    }
    
    /**
     * Buscar tarefas por status (concluídas ou não)
     */
    public List<Task> getTasksByCompletionStatus(boolean completed) {
        // Para isso, você precisaria adicionar um método no TaskRepository
        // Por enquanto, vamos filtrar em memória (não é ideal para grandes volumes)
        return getAllTasks().stream()
                .filter(task -> task.isCompleted() == completed)
                .toList();
    }
    
    /**
     * Marcar tarefa como concluída
     */
    public Task markTaskAsCompleted(Long taskId) {
        Task task = getTaskById(taskId);
        task.setCompleted(true);
        return taskRepository.save(task);
    }
    
    /**
     * Marcar tarefa como não concluída
     */
    public Task markTaskAsNotCompleted(Long taskId) {
        Task task = getTaskById(taskId);
        task.setCompleted(false);
        return taskRepository.save(task);
    }
    
 // Adicione estes métodos ao seu TaskController existente:

    /**
     * Buscar tarefas por texto (título ou descrição)
     */
    public List<Task> searchTasks(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskRepository.findByTitleOrDescriptionContaining(searchText.trim());
    }

    /**
     * Buscar tarefas concluídas
     */
    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompleted(true);
    }

    /**
     * Buscar tarefas pendentes
     */
    public List<Task> getPendingTasks() {
        return taskRepository.findByCompleted(false);
    }

    /**
     * Buscar tarefas ordenadas por data (mais recentes primeiro)
     */
    public List<Task> getTasksOrderedByDate() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Buscar tarefas ordenadas por título
     */
    public List<Task> getTasksOrderedByTitle() {
        return taskRepository.findAllByOrderByTitleAsc();
    }

    /**
     * Obter estatísticas das tarefas
     */
    public TaskStatistics getTaskStatistics() {
        long totalTasks = countTasks();
        long completedTasks = taskRepository.countByCompleted(true);
        long pendingTasks = taskRepository.countByCompleted(false);
        
        return new TaskStatistics(totalTasks, completedTasks, pendingTasks);
    }

    /**
     * Classe para estatísticas (crie em um arquivo separado se preferir)
     */
    public static class TaskStatistics {
        private long total;
        private long completed;
        private long pending;
        
        public TaskStatistics(long total, long completed, long pending) {
            this.total = total;
            this.completed = completed;
            this.pending = pending;
        }
        
        // Getters
        public long getTotal() { return total; }
        public long getCompleted() { return completed; }
        public long getPending() { return pending; }
        
        public double getCompletionPercentage() {
            return total > 0 ? (double) completed / total * 100.0 : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("Total: %d, Concluídas: %d, Pendentes: %d (%.1f%% concluído)", 
                                total, completed, pending, getCompletionPercentage());
        }
    }
}