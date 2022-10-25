import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Equation {

    public static void equation() {

        // on cree une instance pour notre probleme avec un joli nom
        Model model = new Model("SEND+MORE=MONEY");

        // on cree les varaibles entieres relatives au probleme
        IntVar S = model.intVar("S", 1, 9, false);
        IntVar E = model.intVar("E", 0, 9, false);
        IntVar N = model.intVar("N", 0, 9, false);
        IntVar D = model.intVar("D", 0, 9, false);
        IntVar M = model.intVar("M", 1, 9, false);
        IntVar O = model.intVar("0", 0, 9, false);
        IntVar R = model.intVar("R", 0, 9, false);
        IntVar Y = model.intVar("Y", 0, 9, false);

        // chaque variable prend une valeur differente
        model.allDifferent(new IntVar[]{S, E, N, D, M, O, R, Y}).post();

        // formaliser l'equation avec les coefficients adaptes
        IntVar[] ALL = new IntVar[]{
                S, E, N, D,
                M, O, R, E,
                M, O, N, E, Y};
        int[] COEFFS = new int[]{
                1000, 100, 10, 1,
                1000, 100, 10, 1,
                -10000, -1000, -100, -10, -1};
        model.scalar(ALL, COEFFS, "=", 0).post();

        // on cree l'instance du solver pour resoudre
        Solver solver = model.getSolver();

        // on configure des options : ici les statistiques
        solver.showStatistics();

        // on configure des options : ici afficher les solutions en console
        solver.showSolutions();

        // on configure ce qu'on veut resoudre : ici on cherche une solution
        solver.findSolution();
    }


    public static void main(String[] args) {
        equation();
    }



}
