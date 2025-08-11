package br.com.lucas.testeMySQL.util;

import org.update4j.Configuration;
import org.update4j.FileMetadata;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;

/**
 * Classe utilitária para gerar arquivos de configuração do update4j
 * Execute esta classe para criar um config.xml de exemplo
 */
public class ConfigurationGenerator {
    
    public static void main(String[] args) {
        try {
            generateSampleConfig();
            System.out.println("Arquivo de configuração gerado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void generateSampleConfig() throws IOException {
        // Criar o diretório update se não existir
        Path updateDir = Paths.get("update");
        if (!Files.exists(updateDir)) {
            Files.createDirectories(updateDir);
        }
        
        Configuration.Builder config = Configuration.builder();
        
        // Configurações básicas
        config.baseUri("https://seuservidor.com/updates/"); // Substitua pela sua URL base
        config.basePath("${user.dir}"); // Diretório base da aplicação
        
        // Adicionar JAR principal da aplicação
        config.file(FileMetadata.readFrom("testeMySQL.jar") // Nome do seu JAR
                .uri("testeMySQL.jar")
                .path("testeMySQL.jar")
                .classpath(true));
        
        // Adicionar dependências (exemplo)
        config.file(FileMetadata.readFrom("lib/spring-boot-starter-2.7.0.jar")
                .uri("lib/spring-boot-starter-2.7.0.jar")
                .path("lib/spring-boot-starter-2.7.0.jar")
                .classpath(true));
                
        config.file(FileMetadata.readFrom("lib/mysql-connector-java-8.0.33.jar")
                .uri("lib/mysql-connector-java-8.0.33.jar")
                .path("lib/mysql-connector-java-8.0.33.jar")
                .classpath(true));
                
        config.file(FileMetadata.readFrom("lib/update4j-1.5.8.jar")
                .uri("lib/update4j-1.5.8.jar")
                .path("lib/update4j-1.5.8.jar")
                .classpath(true));
        
        // Arquivos de recursos (se houver)
        config.file(FileMetadata.readFrom("resources/application.properties")
                .uri("resources/application.properties")
                .path("resources/application.properties"));
        
        // Configuração de launch
        config.property("default.launcher.main.class", "br.com.lucas.testeMySQL.TesteMySqlApplication");
        
        Configuration configuration = config.build();
        
        // Salvar configuração
        Path configPath = Paths.get("update/config.xml");
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            configuration.write(writer);
        }
        
        System.out.println("Configuração salva em: " + configPath.toAbsolutePath());
        
        // Criar também um arquivo de exemplo para o servidor
        createServerExampleConfig();
    }
    
    private static void createServerExampleConfig() throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Update4j Server Setup</title>\n");
        html.append("</head>\n<body>\n");
        html.append("<h1>Configuração do Servidor Update4j</h1>\n");
        html.append("<h2>Estrutura de Diretórios no Servidor:</h2>\n");
        html.append("<pre>\n");
        html.append("servidor/\n");
        html.append("├── config.xml               # Arquivo de configuração\n");
        html.append("├── testeMySQL.jar           # JAR principal\n");
        html.append("└── lib/                     # Dependências\n");
        html.append("    ├── spring-boot-starter-2.7.0.jar\n");
        html.append("    ├── mysql-connector-java-8.0.33.jar\n");
        html.append("    └── update4j-1.5.8.jar\n");
        html.append("</pre>\n");
        html.append("<h2>Passos para Configurar o Servidor:</h2>\n");
        html.append("<ol>\n");
        html.append("<li>Configure um servidor HTTP (Apache, Nginx, etc.)</li>\n");
        html.append("<li>Coloque os arquivos na estrutura mostrada acima</li>\n");
        html.append("<li>Atualize a URL no UpdateManager.java:</li>\n");
        html.append("<code>CONFIG_URL = \"https://seuservidor.com/update/config.xml\"</code>\n");
        html.append("<li>Sempre que houver uma atualização:</li>\n");
        html.append("<ul>\n");
        html.append("<li>Substitua os JARs no servidor</li>\n");
        html.append("<li>Execute o ConfigurationGenerator para gerar novo config.xml</li>\n");
        html.append("<li>Upload o novo config.xml para o servidor</li>\n");
        html.append("</ul>\n");
        html.append("</ol>\n");
        html.append("</body>\n</html>");
        
        Path serverSetupPath = Paths.get("update/server-setup.html");
        Files.write(serverSetupPath, html.toString().getBytes());
        
        System.out.println("Guia do servidor criado em: " + serverSetupPath.toAbsolutePath());
    }
}
