package org.klar.core.semantics;

public enum ExpressionContext {
    GENERAL,
    ASSIGNMENT,     
    INITIALIZATION, 
    CONDITION,      
    ARGUMENT,       
    ARRAY_SIZE,     
    ARRAY_INIT,
    RETURN,
    INDEX           
}