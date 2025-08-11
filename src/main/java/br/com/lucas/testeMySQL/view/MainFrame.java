package br.com.lucas.testeMySQL.view;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JButton;

import javax.swing.JTable;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import br.com.lucas.testeMySQL.controller.TaskController;
import br.com.lucas.testeMySQL.model.Task;
import java.util.List;

@Component // Tornar MainFrame um componente Spring (opcional)
public class MainFrame extends JFrame {

	private TaskController taskController;
	private JTable table;
	private DefaultTableModel tableModel;

	// Construtor que recebe o TaskController
	public MainFrame(TaskController taskController) {
		this.taskController = taskController;
		initializeComponents();
		refreshTable();
	}

	// Construtor padrão (caso seja usado como @Component)
	public MainFrame() {
		// Este será chamado se MainFrame for um @Component
		// O taskController será injetado via @Autowired
	}

	@Autowired(required = false) // required = false para evitar erro se não for componente
	public void setTaskController(TaskController taskController) {
		this.taskController = taskController;
		if (tableModel != null) {
			refreshTable();
		}
	}

	private void initializeComponents() {
		setTitle("Gerenciador de Tarefas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);

		// Configurar tabela
		String[] columnNames = { "ID", "Título", "Descrição", "Concluída" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public Class<?> getColumnClass(int column) {
				if (column == 3)
					return Boolean.class; // Coluna "Concluída"
				return String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Tornar células não editáveis
			}
		};

		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(table);

		// Painel de botões
		JPanel buttonPanel = new JPanel(new FlowLayout());

		JButton addButton = new JButton("Adicionar Tarefa");
		JButton editButton = new JButton("Editar Tarefa");
		JButton deleteButton = new JButton("Excluir Tarefa");
		JButton refreshButton = new JButton("Atualizar");

		addButton.addActionListener(e -> showAddTaskDialog());
		editButton.addActionListener(e -> editSelectedTask());
		deleteButton.addActionListener(e -> deleteSelectedTask());
		refreshButton.addActionListener(e -> refreshTable());

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(refreshButton);

		// Layout principal
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void refreshTable() {
		if (taskController == null) {
			System.err.println("TaskController está null!");
			JOptionPane.showMessageDialog(this, "Erro: Controlador não foi inicializado corretamente.",
					"Erro de Inicialização", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			tableModel.setRowCount(0); // Limpar tabela

			List<Task> tasks = taskController.getAllTasks();
			for (Task task : tasks) {
				Object[] row = { task.getId(), task.getTitle(), task.getDescription(), task.isCompleted() };
				tableModel.addRow(row);
			}
		} catch (Exception e) {
			System.err.println("Erro ao atualizar tabela: " + e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao carregar tarefas: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showAddTaskDialog() {
		TaskDialog dialog = new TaskDialog(this, "Adicionar Tarefa", null, taskController);
		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			refreshTable();
		}
	}

	private void editSelectedTask() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar.");
			return;
		}

		Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
		try {
			Task task = taskController.getTaskById(taskId);
			TaskDialog dialog = new TaskDialog(this, "Editar Tarefa", task, taskController);
			dialog.setVisible(true);

			if (dialog.isConfirmed()) {
				refreshTable();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar tarefa: " + e.getMessage());
		}
	}

	private void deleteSelectedTask() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Selecione uma tarefa para excluir.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta tarefa?",
				"Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
			try {
				taskController.deleteTask(taskId);
				refreshTable();
				JOptionPane.showMessageDialog(this, "Tarefa excluída com sucesso!");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Erro ao excluir tarefa: " + e.getMessage());
			}
		}
	}
}