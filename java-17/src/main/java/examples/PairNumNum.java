package examples;

import examples.SimpleInterpreter.Num;
import examples.SimpleInterpreter.Value;

public record PairNumNum(Num a, Num b) {
    public static Object PairNumNum(Value a, Value b) {
        return (a instanceof Num n1 && b instanceof  Num n2) ? new PairNumNum(n1, n2) : new Object();
    }
}
