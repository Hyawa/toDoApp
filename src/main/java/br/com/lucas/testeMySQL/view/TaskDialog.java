package br.com.lucas.testeMySQL.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.com.lucas.testeMySQL.controller.TaskController;
import br.com.lucas.testeMySQL.model.Task;

public class TaskDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckbox;
    private boolean confirmed = false;
    
    private Task task;
    private TaskController taskController;
    
    public TaskDialog(JFrame parent, String title, Task task, TaskController taskController) {
        super(parent, title, true);
        this.task = task;
        this.taskController = taskController;
        
        initializeComponents();
        
        if (task != null) {
            // Modo edição
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            completedCheckbox.setSelected(task.isCompleted());
        }
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campo título
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Título:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        mainPanel.add(titleField, gbc);
        
        // Campo descrição
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);
        
        // Checkbox concluída
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        completedCheckbox = new JCheckBox("Concluída");
        mainPanel.add(completedCheckbox, gbc);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");
        
        saveButton.addActionListener(e -> saveTask());
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void saveTask() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckbox.isSelected();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O título é obrigatório!");
            return;
        }
        
        try {
            if (task == null) {
                // Criar nova tarefa
                Task newTask = new Task();
                newTask.setTitle(title);
                newTask.setDescription(description);
                newTask.setCompleted(completed);
                taskController.createTask(newTask);
            } else {
                // Editar tarefa existente
                task.setTitle(title);
                task.setDescription(description);
                task.setCompleted(completed);
                taskController.updateTask(task.getId(), task);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar tarefa: " + e.getMessage());
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}