package org.klang.core.transpilers;

public class JavaEmitter {
    private final StringBuilder out = new StringBuilder(200);
    private int indent = 0;

    public void indent(){
        out.append("    ".repeat(indent));
    }

    public void emit(String s){
        out.append(s);
    }

    public void emitLine(String s){
        indent();
        out.append(s).append("\n");
    }
    public void newLine(){
        out.append("\n");
    }

    public void openBlock(){
        emit("{");
        newLine();
        indent++;
    }

    public void closeBlock(){
        indent--;
        emitLine("}");
    }

    public String result(){
        return out.toString();
    }

}
