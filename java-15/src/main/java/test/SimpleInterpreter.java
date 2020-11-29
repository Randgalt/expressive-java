package test;

import java.util.List;
import java.util.function.Function;

import static test.Helpers.cons;
import static test.MBuilder.M;
import static test.NumBuilder.Num;
import static test.PairBuilder.Pair;
import static test.VarBuilder.Var;
import static test.AddBuilder.Add;
import static test.LamBuilder.Lam;
import static test.ConBuilder.Con;
import static test.AppBuilder.App;
import static test.FunBuilder.Fun;

public interface SimpleInterpreter {
    record M<A>(A value) {
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

    interface Term {}
    record Var(String x) implements Term {}
    record Con(int n) implements Term {}
    record Add(Term l, Term r) implements Term {}
    record Lam(String x, Term body) implements Term {}
    record App(Term fun, Term arg) implements Term {}

    interface Value {}
    Value Wrong = new Value() {
        public String toString() {
            return "wrong";
        }
    };
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
        if ( e.isEmpty() ) {
            return unitM(Wrong);
        }
        var first = e.get(0);
        return first.a().equals(x) ? unitM(first.b()) : lookup(x, e.subList(1, e.size()));
    }

    static M<Value> add(Value a, Value b) {
        if ( (a instanceof Num m) && (b instanceof Num n) ) {
            return unitM(Num(m.n + n.n));
        }
        return unitM(Wrong);
    }

    static M<Value> apply(Value a, Value b) {
        if ( a instanceof Fun f ) {
            return f.f.apply(b);
        }
        return unitM(Wrong);
    }

    static M<Value> interp(Term t, List<Pair<String, Value>> e) {
        if ( t instanceof Var v ) {
            return lookup(v.x, e);
        }
        if ( t instanceof Con con ) {
            return unitM(Num(con.n));
        }
        if ( t instanceof Add add ) {
            var a = interp(add.l, e);
            var b = interp(add.r, e);
            return add(a.value, b.value);
        }
        if ( t instanceof Lam lam ) {
            return unitM(Fun(a -> interp(lam.body, cons(Pair(lam.x, a), e))));
        }
        if ( t instanceof App app ) {
            var a = interp(app.fun, e);
            var b = interp(app.arg, e);
            return apply(a.value, b.value);
        }
        return unitM(Wrong);
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
