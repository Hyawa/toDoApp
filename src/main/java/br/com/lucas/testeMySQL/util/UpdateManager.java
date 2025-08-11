package br.com.lucas.testeMySQL.util;

import org.update4j.*;
import org.update4j.service.UpdateHandler;
import javax.swing.SwingUtilities;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

import java.security.PublicKey;
import org.update4j.inject.Injectable;

import br.com.lucas.testeMySQL.view.LoadingScreen;

public class UpdateManager {
    
    private static final String CONFIG_URL = "https://seuservidor.com/update/config.xml"; // Substitua pela sua URL
    private static final String LOCAL_CONFIG_PATH = "util/config.xml";
    private LoadingScreen loadingScreen;
    private boolean updateRequired = false;
    
    public UpdateManager() {
        SwingUtilities.invokeLater(() -> {
            loadingScreen = new LoadingScreen();
            loadingScreen.setVisible(true);
        });
    }
    
    public boolean checkAndUpdate() {
        try {
            updateProgress(10, "Verificando atualizações...");
            Thread.sleep(500); // Simula tempo de verificação
            
            // Baixar configuração de atualização do servidor
            Configuration config = downloadConfig();
            
            if (config == null) {
                updateProgress(30, "Usando configuração local...");
                config = loadLocalConfig();
            }
            
            if (config == null) {
                updateProgress(100, "Nenhuma configuração encontrada. Iniciando aplicação...");
                return true; // Continua sem atualizar
            }
            
            updateProgress(40, "Verificando arquivos locais...");                       
            
            if (updateRequired) {
                updateProgress(60, "Baixando atualizações...");
                // O download já foi feito pelo UpdateHandler
                updateProgress(90, "Aplicando atualizações...");
                Thread.sleep(1000);
            }
            
            updateProgress(100, "Atualização concluída!");
            Thread.sleep(500);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            updateProgress(100, "Erro na verificação. Continuando...");
            return true; // Continua mesmo com erro
        }
    }
    
    private Configuration downloadConfig() {
        try {
            updateProgress(20, "Baixando configuração do servidor...");
            
            // Criar diretório se não existir
            Path updateDir = Paths.get("update");
            if (!Files.exists(updateDir)) {
                Files.createDirectories(updateDir);
            }
            
            try (InputStream in = URI.create(CONFIG_URL).toURL().openStream()) {
                Files.copy(in, Paths.get(LOCAL_CONFIG_PATH), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Carregar configuração
            try (Reader reader = Files.newBufferedReader(Paths.get(LOCAL_CONFIG_PATH))) {
                return Configuration.read(reader);
            }
            
        } catch (Exception e) {
            System.out.println("Não foi possível baixar configuração do servidor: " + e.getMessage());
            return null;
        }
    }
    
    private Configuration loadLocalConfig() {
        try {
            Path configPath = Paths.get(LOCAL_CONFIG_PATH);
            if (Files.exists(configPath)) {
                try (Reader reader = Files.newBufferedReader(configPath)) {
                    return Configuration.read(reader);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar configuração local: " + e.getMessage());
        }
        return null;
    }
    
    private void updateProgress(int progress, String message) {
        SwingUtilities.invokeLater(() -> {
            if (loadingScreen != null) {
                loadingScreen.updateProgress(progress, message);
            }
        });
    }
    
    public void closeLoadingScreen() {
        SwingUtilities.invokeLater(() -> {
            if (loadingScreen != null) {
                loadingScreen.dispose();
            }
        });
    }
    
    // Handler customizado para controlar o processo de atualização
    private class CustomUpdateHandler implements UpdateHandler {
        
        @Override
        public void updateDownloadFileProgress(FileMetadata file, float frac) {
            int progress = (int) (60 + (frac * 30)); // Progress de 60% a 90%
            updateProgress(progress, "Baixando: " + file.getPath().getFileName());
        }
        
        @Override
        public void updateDownloadProgress(float frac) {
            int progress = (int) (60 + (frac * 30)); // Progress de 60% a 90%
            updateProgress(progress, "Baixando atualizações...");
        }
        
        @Override
        public void doneDownloadFile(FileMetadata file, Path path) {
            updateRequired = true;
            System.out.println("Arquivo atualizado: " + file.getPath().getFileName());
        }
        
        @Override
        public void failed(Throwable exception) {
            System.err.println("Falha na atualização");
            exception.printStackTrace();
        }
        
        @Override
        public void succeeded() {
            System.out.println("Todas as atualizações foram baixadas com sucesso!");
        }
        
        @Override
        public void startCheckUpdates() {
            updateProgress(35, "Iniciando verificação de atualizações...");
        }
        
        @Override
        public void doneCheckUpdates() {
            updateProgress(50, "Verificação concluída.");
        }
        
        @Override
        public void startDownloads() {
            updateProgress(55, "Iniciando downloads...");
        }
        
        @Override
        public void doneDownloads() {
            updateProgress(85, "Downloads concluídos.");
        }
    }
}