@Use("c")
public static void testeNumbers(){
	integer x = 10;
	double y = 12.8;
}

$

@Use("c")
public static Array<Integer> build(integer n) {
    if (n <= 0) {
        println("Using fallback size 10");
        n = 10;
    }

    Array<Integer> nums = new Array(n);
    for (integer i = 0; i < n; i++) {
        nums.append(i);
    }

    return nums;
}

@Use("java")
public static void main(Array<String> args) {
    Array<Integer> nums = build(10);
    for (integer x -> nums) {
        println(x);
    }
}
