package examples;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static examples.Helpers.*;

public interface SimpleInterpreter {
    interface M<A> {
        A value();

        default <B> M<B> bind(Function<A, M<B>> k) {
            return k.apply(value());
        }

        default <B> M<B> map(Function<A, B> f) {
            return bind(x -> unitM(f.apply(x)));
        }

        default <B> M<B> flatMap(Function<A, M<B>> f) {
            return bind(f);
        }
    }

    static <A> M<A> unitM(A s) {
        return M(s);
    }

    static String showM(M<Value> m) {
        return m.value().toStr();
    }

    interface Term { }
    interface Var extends Term {
        String x();
    }
    interface Con extends Term {
        int n();
    }
    interface Add extends Term {
        Term l();

        Term r();
    }
    interface Lam extends Term {
        String x();

        Term body();
    }
    interface App extends Term {
        Term fun();

        Term arg();
    }

    interface Value {
        String toStr(); // can't override/implement toString() in an interface
    }
    Value Wrong = () -> "wrong";
    interface Num extends Value {
        int n();

        @Override
        default String toStr() {
            return Integer.toString(n());
        }
    }
    interface Fun extends Value {
        Function<Value, M<Value>> f();

        @Override
        default String toStr() {
            return "<function>";
        }
    }

    static M<Value> lookup(String x, List<Pair<String, Value>> e) {
        if (e.isEmpty()) {
            return unitM(Wrong);
        }
        Pair<String, Value> first = e.get(0);
        return first.a().equals(x) ? unitM(first.b()) : lookup(x, e.subList(1, e.size()));
    }

    static M<Value> add(Value a, Value b) {
        if ((a instanceof Num) && (b instanceof Num)) {
            return unitM(Num(((Num) a).n() + ((Num) b).n()));
        }
        return unitM(Wrong);
    }

    static M<Value> apply(Value a, Value b) {
        if (a instanceof Fun) {
            return ((Fun) a).f().apply(b);
        }
        return unitM(Wrong);
    }

    static M<Value> interp(Term t, List<Pair<String, Value>> e) {
        if (t instanceof Var) {
            return lookup(((Var) t).x(), e);
        }
        if (t instanceof Con) {
            return unitM(Num(((Con) t).n()));
        }
        if (t instanceof Add) {
            M<Value> a = interp(((Add) t).l(), e);
            M<Value> b = interp(((Add) t).r(), e);
            return add(a.value(), b.value());
        }
        if (t instanceof Lam) {
            return unitM(Fun(a -> interp(((Lam) t).body(), cons(Pair(((Lam) t).x(), a), e))));
        }
        if (t instanceof App) {
            M<Value> a = interp(((App) t).fun(), e);
            M<Value> b = interp(((App) t).arg(), e);
            return apply(a.value(), b.value());
        }
        return unitM(Wrong);
    }

    static String test(Term t) {
        return showM(interp(t, Collections.emptyList()));
    }

    static void main(String[] args) {
        App term0 = App(Lam("x", Add(Var("x"), Var("x"))), Add(Con(10), Con(11)));
        App term1 = App(Con(1), Con(2));

        System.out.println(test(term0));
        System.out.println(test(term1));
    }
}
