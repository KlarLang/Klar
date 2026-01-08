@Use("java")
public void main() {
    double valueToAHalf = 10.0;
    double halfOfValue = halfOfValue(valueToAHalf);
    println(halfOfValue);

    return;
}

@Use("java")
public double halfOfValue(double value){
    return value / 2;
}