package examples;

import java.util.List;
import java.util.function.Function;

import static examples.AddBuilder.Add;
import static examples.AppBuilder.App;
import static examples.ConBuilder.Con;
import static examples.FunBuilder.Fun;
import static examples.Helpers.cons;
import static examples.LamBuilder.Lam;
import static examples.MBuilder.M;
import static examples.NumBuilder.Num;
import static examples.PairBuilder.Pair;
import static examples.VarBuilder.Var;

public interface SimpleInterpreter {
    record M<A> (A value) {
        <B> M<B> bind(Function<A, M<B>> k) {
            return k.apply(value);
        }

        <B> M<B> map(Function<A, B> f) {
            return bind(x -> unitM(f.apply(x)));
        }

        <B> M<B> flatMap(Function<A, M<B>> f) {
            return bind(f);
        }
    }

    static <A> M<A> unitM(A a) {
        return M(a);
    }

    static String showM(M<Value> m) {
        return m.value.toString();
    }

    sealed interface Term permits Var,Con,Add,Lam,App {}
    record Var(String x) implements Term {}
    record Con(int n) implements Term {}
    record Add(Term l, Term r) implements Term {}
    record Lam(String x, Term body) implements Term {}
    record App(Term fun, Term arg) implements Term {}

    sealed interface Value permits Num,Fun,Wrong {}
    record Wrong() implements Value {
        public static final Wrong INSTANCE = new Wrong();
        public String toString() {
            return "wrong";
        }
    }
    record Num(int n) implements Value {
        public String toString() {
            return Integer.toString(n);
        }
    }
    record Fun(Function<Value, M<Value>> f) implements Value {
        public String toString() {
            return "<function>";
        }
    }

    static M<Value> lookup(String x, List<Pair<String, Value>> e) {
        return switch (e) {
            case List __ && e.isEmpty() -> unitM(Wrong.INSTANCE);
            case List __ && e.get(0).a().equals(x) -> unitM(e.get(0).b());
            default -> lookup(x, e.subList(1, e.size()));
        };
    }

    static M<Value> add(Value a, Value b) {
        return switch (Pair(a, b)) {
            case Pair p && p.a() instanceof Num m && p.b() instanceof Num n -> unitM(Num(m.n() + n.n()));
            default -> unitM(Wrong.INSTANCE);
        };
    }

    static M<Value> apply(Value a, Value b) {
        return switch (a) {
            case Fun fun -> fun.f.apply(b);
            default -> unitM(Wrong.INSTANCE);
        };
    }

    static M<Value> interp(Term t, List<Pair<String, Value>> e) {
        return switch (t) {
            case Var var -> lookup(var.x, e);
            case Con con -> unitM(Num(con.n));
            case Add add -> {
                var a = interp(add.l, e);
                var b = interp(add.r, e);
                yield add(a.value, b.value);
            }
            case Lam lam -> unitM(Fun(a -> interp(lam.body, cons(Pair(lam.x, a), e))));
            case App app -> {
                var a = interp(app.fun, e);
                var b = interp(app.arg, e);
                yield apply(a.value, b.value);
            }
        };
    }

    static String test(Term t) {
        return showM(interp(t, List.of()));
    }

    static void main(String[] args) {
        var term0 = App(Lam("x", Add(Var("x"), Var("x"))), Add(Con(10), Con(11)));
        var term1 = App(Con(1), Con(2));

        System.out.println(test(term0));
        System.out.println(test(term1));
    }
}
