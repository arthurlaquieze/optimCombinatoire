import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Start {

    public static void main(String[] args) {
        int n = 10;
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
        model.allDifferent(tableau);
    }
}
