import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

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

        IntVar[] heightsPowder = new IntVar[3];
        heightsPowder[0] = model.intVar(1);
        heightsPowder[1] = model.intVar(1);
        heightsPowder[2] = model.intVar(1);
        IntVar makingPowderCapacity = model.intVar(1);

        IntVar[] heightsStep2 = new IntVar[3];
        heightsStep2[0] = model.intVar(2);
        heightsStep2[1] = model.intVar(2);
        heightsStep2[2] = model.intVar(2);
        IntVar transformingFromPowderCapacity = model.intVar(1);

        // add tasks foreach order
        for (Order order : orders) {
            // add tasks for step 1, transformation to powder
            Task[] makingPowderTasks = new Task[3];
            {
                // Task makeComprimePowder ; Task makeGelulePowder ; Task makeSachetPowder
                makingPowderTasks[0] = new Task(model, 0, 9999999,
                        order.getComprimesQuantity() * toPowderComprime.getDuration(), 0, order.getDueTime());
                makingPowderTasks[1] = new Task(model, 0, 9999999,
                        order.getGelulesQuantity() * toPowderGelule.getDuration(), 0, order.getDueTime());
                makingPowderTasks[2] = new Task(model, 0, 9999999,
                        order.getSachetsQuantity() * toPowderSachet.getDuration(), 0, order.getDueTime());

                // add tasks to model so that powder transformation can only be done
                // one at a time
                model.cumulative(makingPowderTasks, heightsPowder, makingPowderCapacity).post();
            }
            // add tasks for step 2, transformation from powder to gélules, comprimés and
            // sachets
            {
                Task[] transformingFromPowderTasks = new Task[3];
                transformingFromPowderTasks[0] = new Task(model,
                        makingPowderTasks[0].getEnd().getValue(), 9999999,
                        order.getComprimesQuantity() * powderToComprime.getDuration(), 0, order.getDueTime());
                transformingFromPowderTasks[1] = new Task(model,
                        makingPowderTasks[1].getEnd().getValue(), 9999999,
                        order.getGelulesQuantity() * powderToGelule.getDuration(), 0, order.getDueTime());
                transformingFromPowderTasks[2] = new Task(model,
                        makingPowderTasks[2].getEnd().getValue(), 9999999,
                        order.getSachetsQuantity() * powderToSachet.getDuration(), 0, order.getDueTime());

                // add tasks to model so that powder transformation can be done concurrently
                model.cumulative(transformingFromPowderTasks, heightsStep2, transformingFromPowderCapacity).post();
            }
        }
        System.out.println(model.getSolver().findSolution());
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
