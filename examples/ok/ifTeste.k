@Use("java")
public void main(){
    integer valueA = 12;
    integer i = 0;

    while (i < valueA){
        println(i);
        i = i + 1;
    }

    println("Valor final de i: " + i);
    return;
}
