import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class Pharmacy {
    public static void pharmacyProblem(int nOrders) {
        // create processes
        Processus toPowderComprime = new Processus(20, 1, "Transformation to comprimé power");
        Processus toPowderGelule = new Processus(7, 1, "Transformation to gélule powder");
        Processus toPowderSachet = new Processus(14, 1, "Transformation to sachet powder");

        Processus powderToComprime = new Processus(15, 2, "Conditionnement vers comprimé");
        Processus powderToGelule = new Processus(5, 2, "Conditionnement vers gélule");
        Processus powderToSachet = new Processus(20, 2, "Conditionnement vers sachet");

        List<Processus> processes = new ArrayList<Processus>();
        processes.add(toPowderComprime);
        processes.add(toPowderGelule);
        processes.add(toPowderSachet);
        processes.add(powderToComprime);
        processes.add(powderToGelule);
        processes.add(powderToSachet);
        List<Order> orders = generateOrders(nOrders);

        Model model = new Model("Pharmacy");

        // ordre de traitement des commandes
        IntVar[] processingOrder = model.intVarArray("Processing order", nOrders, 1, nOrders);
        // list process occupé ou non (boolean)
        BoolVar[] occupied = model.boolVarArray("Occupied processes", processes.size());
    }

    public static List<Order> generateOrders(int n) {
        /**
         * Generate n random orders
         */
        List<Order> orders = new ArrayList<Order>();

        for (int i = 0; i < n; i++) {
            orders.add(new Order());
        }

        return orders;
    }

    public static void main(String[] args) {
        int nOrders = 10; // number of orders to simulate
        pharmacyProblem(nOrders);
    }
}
