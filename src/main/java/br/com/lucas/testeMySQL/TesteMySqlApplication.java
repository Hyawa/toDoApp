package br.com.lucas.testeMySQL;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.lucas.testeMySQL.controller.TaskController;
import br.com.lucas.testeMySQL.view.MainFrame;

@SpringBootApplication
public class TesteMySqlApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Configurar para não ser headless (se necessário)
        System.setProperty("java.awt.headless", "false");
        
        context = SpringApplication.run(TesteMySqlApplication.class, args);
        System.out.println("Contexto do Spring Boot inicializado.");

        // Verificar se não está em ambiente headless antes de abrir GUI
        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Abrindo a interface gráfica...");
                try {
                    // Obter o TaskController do contexto Spring
                    TaskController taskController = context.getBean(TaskController.class);
                    
                    // Criar MainFrame passando o controller
                    MainFrame mainFrame = new MainFrame(taskController);
                    mainFrame.setVisible(true);
                    
                } catch (Exception e) {
                    System.err.println("Erro ao abrir interface gráfica: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Ambiente headless detectado. Interface gráfica não será aberta.");
            System.out.println("Acesse a aplicação via navegador em: http://localhost:8080");
        }
    }
    
    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}