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

        List<Order> orders = generateOrders();

        Model model = new Model("Pharmacy");

    }

    public static List<Order> generateOrders() {
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(4, 2, 5, 10));
        orders.add(new Order(8, 8, 3, 8));
        orders.add(new Order(2, 12, 5, 15));
        orders.add(new Order(5, 1, 15, 18));
        orders.add(new Order(4, 9, 5, 11));

        return orders;
    }

    public static void main(String[] args) {
        pharmacyProblem();
    }
}
