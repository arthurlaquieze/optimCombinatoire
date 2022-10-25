import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;

public class Pharmacy {
    public static void pharmacyProblem() {
        Processus toPowderComprime = new Processus(20, 1, "Transformation to comprimé power");
        Processus toPowderGelule = new Processus(7, 1, "Transformation to gélule powder");
        Processus toPowderSachet = new Processus(14, 1, "Transformation to sachet powder");

        Processus powderToComprime = new Processus(15, 2, "Conditionnement vers comprimé");
        Processus powderToGelule = new Processus(5, 2, "Conditionnement vers gélule");
        Processus powderToSachet = new Processus(20, 2, "Conditionnement vers sachet");

        List<Order> orders = generateOrders(100);

        Model model = new Model("Pharmacy");

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
        pharmacyProblem();
    }
}
