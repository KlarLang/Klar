package org.klang.core.error;

public final class SourceManager {


    private final String source;
    private String[] lines; // lazy

    public SourceManager(String source) {
        this.source = source;
    }

    /** 
    * @return Returns context lines with the error line ALWAYS as the last line. 
    * @param errorLine error line (1-indexed) 
    * @param linesBefore how many lines to show BEFORE the error 
    */
    public String[] getContextLines(int errorLine, int linesBefore) {
        String[] allLines = lines();
        
        int errorIndex = errorLine - 1;
        int startIndex = Math.max(0, errorIndex - linesBefore);
        int endIndex = errorIndex;
        
        String[] context = new String[endIndex - startIndex + 1];
        
        for (int i = startIndex; i <= endIndex; i++) {
            context[i - startIndex] = allLines[i];
        }
    
        return context;
    }


    private String[] lines(){
        if (lines == null){
            lines = source.split("\n", -1);
        }

        return lines;
    }
}