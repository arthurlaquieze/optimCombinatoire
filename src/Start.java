import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Start {

    public static void main(String[] args) {
        int n = 10;
        Model model = new Model("nQueens");
        System.out.println(model.getName());

        IntVar[] tableau = model.intVarArray("tableau", n, 1, n);

    }
}
