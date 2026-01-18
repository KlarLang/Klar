package org.klar.cli.utils;

import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Path;

public class BuildCache {
    
    public static String calculateFileHash(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(file);
        byte[] hashBytes = digest.digest(fileBytes);
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
    
    public static boolean needsRebuild(Path sourceFile, Path cacheFile) throws Exception {
        // Se o arquivo de cache não existe, precisa buildar
        if (!Files.exists(cacheFile)) {
            return true;
        }
        
        // Ler hash anterior
        String cachedHash = Files.readString(cacheFile).trim();
        
        // Calcular hash atual
        String currentHash = calculateFileHash(sourceFile);
        
        // Comparar
        return !currentHash.equals(cachedHash);
    }
    
    public static void saveHash(Path sourceFile, Path cacheFile) throws Exception {
        String hash = calculateFileHash(sourceFile);
        Files.writeString(cacheFile, hash);
    }
}