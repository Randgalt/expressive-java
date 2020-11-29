package examples;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RecordBuilder.Include({
    SimpleInterpreter.M.class,
    SimpleInterpreter.Num.class,
    SimpleInterpreter.Fun.class,
    SimpleInterpreter.App.class,
    SimpleInterpreter.Lam.class,
    SimpleInterpreter.Add.class,
    SimpleInterpreter.Var.class,
    SimpleInterpreter.Con.class,
    Pair.class
})
public class Helpers {
    public static <T> List<T> cons(T value, List<T> list) {
        var worker = new ArrayList<T>();
        worker.add(value);
        worker.addAll(list);
        return Collections.unmodifiableList(worker);
    }
}
