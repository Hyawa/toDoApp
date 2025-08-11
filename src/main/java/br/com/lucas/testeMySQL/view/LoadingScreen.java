package br.com.lucas.testeMySQL.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends JFrame {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JLabel developerLabel;
    private JLabel versionLabel;
    
    public LoadingScreen() {
        initializeComponents();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Título do aplicativo
        titleLabel = new JLabel("TesteMySQL Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(45, 45, 45));
        
        // Versão da aplicação
        versionLabel = new JLabel("v1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(120, 120, 120));
        
        // Label de status
        statusLabel = new JLabel("Iniciando verificação de atualizações...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        // Barra de progresso customizada
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setPreferredSize(new Dimension(350, 25));
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setForeground(new Color(70, 130, 250));
        progressBar.setBorderPainted(false);
        
        // Assinatura do desenvolvedor
        developerLabel = new JLabel("Desenvolvido por Lucas R", SwingConstants.CENTER);
        developerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        developerLabel.setForeground(new Color(140, 140, 140));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal com padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 30, 60));
        
        // Logo/Ícone placeholder
        JLabel logoLabel = new JLabel("🔄", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Espaçamentos e componentes
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(versionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(progressBar);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Informações adicionais
        JLabel infoLabel = new JLabel("Verificando atualizações disponíveis...", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(150, 150, 150));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        
        // Espaço flexível
        mainPanel.add(Box.createVerticalGlue());
        
        developerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(developerLabel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupWindow() {
        setTitle("Atualizando TesteMySQL...");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        setAlwaysOnTop(true);
        
        // Bordas
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
    }
    
    public void updateProgress(int value, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            progressBar.setString(value + "%");
            statusLabel.setText(status);
            
            // Atualizar cor da barra baseado no progresso
            if (value >= 90) {
                progressBar.setForeground(new Color(50, 200, 50)); // Verde quando quase completo
            } else if (value >= 50) {
                progressBar.setForeground(new Color(70, 130, 250)); // Azul padrão
            }
        });
    }
    
    public void setAppVersion(String version) {
        SwingUtilities.invokeLater(() -> {
            versionLabel.setText(version);
        });
    }
    
    public void showError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setForeground(new Color(220, 50, 50)); // Vermelho para erro
            statusLabel.setText(errorMessage);
            statusLabel.setForeground(new Color(180, 50, 50));
        });
    }
}
