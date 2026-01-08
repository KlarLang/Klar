package org.klang.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.klang.cli.error.KcInvalidFileType;
import org.klang.cli.error.diagnostic.KcDiagnosticCode;
import org.klang.cli.utils.BuildCache;
import org.klang.core.errors.KException;
import org.klang.core.errors.SourceManager;
import org.klang.core.lexer.Lexer;
import org.klang.core.lexer.Token;
import org.klang.core.parser.Parser;
import org.klang.core.parser.ast.ProgramNode;
import org.klang.core.semantics.TypeChecker;
import org.klang.core.transpilers.JavaTranspiler;

@Command(
    name = "run",
    description = "Transpile, compile and run a Klang program"
)
public class RunCommand implements Runnable {

    @Parameters(paramLabel = "FILE")
    private File file;

    @Override
    public void run() {
        Path path = file.toPath();
    
        if (!path.getFileName().toString().endsWith(".k")) {
            throw new KcInvalidFileType(
                KcDiagnosticCode.KC002,
                "run",
                null,
                path.getFileName().toString()
            );
        }
    
        String _fileName = path.getFileName().toString(); 
        String fileName = _fileName.substring(0, _fileName.length() - 2);
        
        try {
            Path outDir = Path.of("out");
            Path cacheDir = outDir.resolve(".cache");
            Files.createDirectories(cacheDir);
        
            Path cacheFile = cacheDir.resolve(fileName + ".hash");
            Path javaFile = outDir.resolve((fileName + ".java"));
            Path classFile = outDir.resolve(fileName + ".class");
        
            // Verificar se precisa recompilar (CORRIGIDO AQUI)
            boolean needsRebuild = BuildCache.needsRebuild(path, cacheFile) || !Files.exists(classFile);
            
            if (needsRebuild) {
                System.out.println("Building " + fileName + ".k...");
            
                // 1. Read
                String source = Files.readString(path);
                SourceManager sm = new SourceManager(source);
            
                // 2. Lexer 
                Lexer lexer = new Lexer(source, file.getPath(), sm);
                List<Token> tokens = lexer.tokenizeSourceCode();
            
                // 3. Parse
                Parser parser = new Parser(tokens, path, sm);
                ProgramNode program = parser.parseProgram();
            
                // 4. Type Checker
                TypeChecker checker = new TypeChecker(sm, path);
                checker.check(program);
            
                // 5. Transpile
                JavaTranspiler transpiler = new JavaTranspiler(fileName, sm, path);
                String javaCode = transpiler.transpile(program);
            
                // 6. Write Java file
                Files.writeString(javaFile, javaCode);
            
                // 7. Compile
                System.out.println("Compiling Java code...");
                Process javac = new ProcessBuilder(
                    "javac", 
                    "-d", outDir.toString(),
                    javaFile.toString()
                ).inheritIO().start();
            
                if (javac.waitFor() != 0) {
                    throw new RuntimeException("Java compilation failed");
                }
            
                // Verificar se o .class foi gerado
                if (!Files.exists(classFile)) {
                    throw new RuntimeException(".class file was not generated at: " + classFile);
                }
            
                // 8. Save hash
                BuildCache.saveHash(path, cacheFile);
                
                System.out.println("✓ Build successful");
            } else {
                System.out.println("✓ " + fileName + " is up to date (skipping build)");
            }
        
            // 9. Execute (sempre executa, mesmo se não recompilou)
            Process java = new ProcessBuilder(
                "java", "-cp", outDir.toAbsolutePath().toString(), fileName
            ).inheritIO().start();
        
            int exitCode = java.waitFor();
            System.err.println("Program exited with code: " + exitCode);
        
        } catch (KException e) {
            System.out.println(e.format());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Process interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Build error: " + e.getMessage());
        }
    }
}