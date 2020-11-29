package test;

import org.immutables.value.Value;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Value.Style(
    of = "new",
    allParameters = true
)
@Value.Include({
    SimpleInterpreter.M.class,
    SimpleInterpreter.Term.class,
    SimpleInterpreter.Con.class,
    SimpleInterpreter.Add.class,
    SimpleInterpreter.Lam.class,
    SimpleInterpreter.App.class,
    SimpleInterpreter.Num.class,
    SimpleInterpreter.Fun.class,
    SimpleInterpreter.Var.class,
    Pair.class
})
public class Helpers {
    public static <T> List<T> cons(T value, List<T> list) {
        ArrayList<T> worker = new ArrayList<T>();
        worker.add(value);
        worker.addAll(list);
        return worker;
    }

    public static <A> SimpleInterpreter.M<A> M(A a) {
        return new ImmutableM<>(a);
    }

    public static SimpleInterpreter.Num Num(int n) {
        return new ImmutableNum(n);
    }

    public static SimpleInterpreter.Fun Fun(Function<SimpleInterpreter.Value, SimpleInterpreter.M<SimpleInterpreter.Value>> f) {
        return new ImmutableFun(f);
    }

    public static <A, B> Pair<A, B> Pair(A a, B b) {
        return new ImmutablePair<>(a, b);
    }

    public static SimpleInterpreter.App App(SimpleInterpreter.Term fun, SimpleInterpreter.Term arg) {
        return new ImmutableApp(fun, arg);
    }

    public static SimpleInterpreter.Lam Lam(String x, SimpleInterpreter.Term body) {
        return new ImmutableLam(x, body);
    }

    public static SimpleInterpreter.Add Add(SimpleInterpreter.Term l, SimpleInterpreter.Term r) {
        return new ImmutableAdd(l, r);
    }

    public static SimpleInterpreter.Var Var(String x) {
        return new ImmutableVar(x);
    }

    public static SimpleInterpreter.Con Con(int n) {
        return new ImmutableCon(n);
    }
}
