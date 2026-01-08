/*
Remember if something is implied in K is wrong, 
that's why, integer to double will be explicit.
*/
@Use("java")
public void main(){
    integer integerValue = 5;
    integer doubleValue = integerValue;

    println(doubleValue);
    return;
}