@Use("java")
public integer incrementOne(integer value) {
    return value + 1;
}

@Use("java")
public void main() {
    integer result = incrementOne(incrementOne(incrementOne(1)));
    println(result);
    return;
}
