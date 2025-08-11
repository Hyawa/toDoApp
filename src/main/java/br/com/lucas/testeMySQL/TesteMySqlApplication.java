package br.com.lucas.testeMySQL;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.lucas.testeMySQL.controller.TaskController;
import br.com.lucas.testeMySQL.view.MainFrame;
import br.com.lucas.testeMySQL.util.UpdateManager;

@SpringBootApplication
public class TesteMySqlApplication {

    private static ConfigurableApplicationContext context;
    private static UpdateManager updateManager;

    public static void main(String[] args) {
        // Configurar para não ser headless (se necessário)
        System.setProperty("java.awt.headless", "false");
        
        // Configurar Look and Feel antes de criar qualquer componente Swing
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            System.err.println("Erro ao definir Look and Feel: " + e.getMessage());
        }
        
        // Verificar se não está em ambiente headless antes de iniciar qualquer GUI
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Ambiente headless detectado. Pulando verificação de atualizações GUI.");
            startSpringBootApplication(args);
            return;
        }
        
        // ===== VERIFICAÇÃO DE ATUALIZAÇÕES =====
        System.out.println("Iniciando verificação de atualizações...");
        
        // Executar verificação de atualizações em thread separada
        Thread updateThread = new Thread(() -> {
            try {
                updateManager = new UpdateManager();
                boolean updateSuccess = updateManager.checkAndUpdate();
                
                if (updateSuccess) {
                    // Fechar tela de loading após um breve delay
                    Thread.sleep(1000);
                    
                    SwingUtilities.invokeLater(() -> {
                        updateManager.closeLoadingScreen();
                        
                        // Iniciar Spring Boot após atualizações
                        startSpringBootApplication(args);
                    });
                } else {
                    // Em caso de falha crítica na atualização
                    System.err.println("Falha crítica na atualização. Encerrando aplicação.");
                    SwingUtilities.invokeLater(() -> {
                        updateManager.closeLoadingScreen();
                        System.exit(1);
                    });
                }
                
            } catch (Exception e) {
                System.err.println("Erro durante verificação de atualizações: " + e.getMessage());
                e.printStackTrace();
                
                // Continuar mesmo com erro de atualização
                SwingUtilities.invokeLater(() -> {
                    if (updateManager != null) {
                        updateManager.closeLoadingScreen();
                    }
                    startSpringBootApplication(args);
                });
            }
        });
        
        updateThread.setDaemon(true);
        updateThread.start();
    }
    
    private static void startSpringBootApplication(String[] args) {
        try {
            // Inicializar Spring Boot
            System.out.println("Iniciando Spring Boot...");
            context = SpringApplication.run(TesteMySqlApplication.class, args);
            System.out.println("Contexto do Spring Boot inicializado com sucesso.");

            // Abrir interface gráfica
            SwingUtilities.invokeLater(() -> {
                System.out.println("Abrindo a interface gráfica...");
                try {
                    // Obter o TaskController do contexto Spring
                    TaskController taskController = context.getBean(TaskController.class);
                    
                    // Criar MainFrame passando o controller
                    MainFrame mainFrame = new MainFrame(taskController);
                    mainFrame.setVisible(true);
                    
                    System.out.println("Interface gráfica aberta com sucesso.");
                    
                } catch (Exception e) {
                    System.err.println("Erro ao abrir interface gráfica: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Erro ao inicializar Spring Boot: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar mensagem de erro ao usuário se possível
            if (!GraphicsEnvironment.isHeadless()) {
                SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "Erro ao inicializar a aplicação:\n" + e.getMessage(),
                        "Erro de Inicialização", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                });
            }
            
            System.exit(1);
        }
    }
    
    public static ConfigurableApplicationContext getContext() {
        return context;
    }
    
    // Método para reiniciar a aplicação (útil após grandes atualizações)
    public static void restartApplication() {
        try {
            // Fechar contexto Spring
            if (context != null) {
                context.close();
            }
            
            // Reiniciar JVM (método simples)
            String javaBin = System.getProperty("java.home") + "/bin/java";
            String classpath = System.getProperty("java.class.path");
            String className = TesteMySqlApplication.class.getName();
            
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
            builder.start();
            
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("Erro ao reiniciar aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}