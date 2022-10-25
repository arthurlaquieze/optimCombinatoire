import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Start {

    public static void main(String[] args) {
        int n = 8;
        Model model = new Model("nQueens");
        System.out.println(model.getName());

        /**
         * Le rang dans le tableau correspond à la ligne,
         * la valeur d'une variable correspond à la colonne de la reine
         * 
         * Ce choix de modèle implique déjà une seule reine par ligne
         */
        IntVar[] tableau = model.intVarArray("tableau", n, 1, n);

        // ajout condition sur les colonnes, toutes différentes.
        model.allDifferent(tableau).post();

        // ajout contraintes des diagonales
        for (int i = 0; i < n; i++) {
            addDiagonalCondition(model, tableau, i);
        }

        // find and print solution
        Solver solver = model.getSolver();
        Solution solution = solver.findSolution();

        for (int i = 0; i < n; i++) {
            System.out.println("row" + i + " = " + solution.getIntVal(tableau[i]));
        }
    }

    public static void addDiagonalCondition(Model model, IntVar[] tableau, int row) {
        int max = tableau.length;

        for (int i = 0; i < max; i++) {
            if (i == row) {
                continue;
            }
            // contrainte sur diagonale
            model.arithm(tableau[row], "!=", tableau[i].sub(i - row).intVar()).post();
            // antidiagonale (bas gauche -> haut droit)
            model.arithm(tableau[row], "!=", tableau[i].add(i - row).intVar()).post();
        }
    }
}
