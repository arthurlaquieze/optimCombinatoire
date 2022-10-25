import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
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
        // IntVar[] processingOrder = model.intVarArray("Processing order", nOrders, 1,
        // nOrders);
        // list process occupé ou non (boolean)
        // BoolVar[] occupied = model.boolVarArray("Occupied processes",
        // processes.size());

        IntVar[] heightsPowder = new IntVar[3 * nOrders];
        Arrays.fill(heightsPowder, model.intVar(1));
        IntVar makingPowderCapacity = model.intVar(1);

        IntVar[] heightsStep2 = new IntVar[nOrders];
        Arrays.fill(heightsStep2, model.intVar(1));
        IntVar transformingFromPowderCapacity = model.intVar(1);

        // add tasks foreach order
        Task[] makingPowderTasks = new Task[3 * nOrders];
        Task[] transformingFromPowderToComprimeTasks = new Task[nOrders];
        Task[] transformingFromPowderToGeluleTasks = new Task[nOrders];
        Task[] transformingFromPowderToSachetTasks = new Task[nOrders];
        // for (Order order : orders) {
        for (int i = 0; i < nOrders; i++) {
            Order order = orders.get(i);
            // add tasks for step 1, transformation to powder
            {
                // Task makeComprimePowder ; Task makeGelulePowder ; Task makeSachetPowder
                makingPowderTasks[3 * i] = new Task(model, 0, 9999999,
                        order.getComprimesQuantity() * toPowderComprime.getDuration(), 0, order.getDueTime());
                makingPowderTasks[3 * i + 1] = new Task(model, 0, 9999999,
                        order.getGelulesQuantity() * toPowderGelule.getDuration(), 0, order.getDueTime());
                makingPowderTasks[3 * i + 2] = new Task(model, 0, 9999999,
                        order.getSachetsQuantity() * toPowderSachet.getDuration(), 0, order.getDueTime());

            }
            // add tasks for step 2, transformation from powder to gélules, comprimés and
            // sachets
            {
                transformingFromPowderToComprimeTasks[i] = new Task(model,
                        makingPowderTasks[3 * i].getEnd().getValue(), 9999999,
                        order.getComprimesQuantity() * powderToComprime.getDuration(), 0, order.getDueTime());
                transformingFromPowderToGeluleTasks[i] = new Task(model,
                        makingPowderTasks[3 * i + 1].getEnd().getValue(), 9999999,
                        order.getGelulesQuantity() * powderToGelule.getDuration(), 0, order.getDueTime());
                transformingFromPowderToSachetTasks[i] = new Task(model,
                        makingPowderTasks[3 * i + 2].getEnd().getValue(), 9999999,
                        order.getSachetsQuantity() * powderToSachet.getDuration(), 0, order.getDueTime());
            }
        }

        // add tasks to model
        model.cumulative(makingPowderTasks, heightsPowder, makingPowderCapacity).post();
        model.cumulative(transformingFromPowderToComprimeTasks, heightsStep2, transformingFromPowderCapacity).post();
        model.cumulative(transformingFromPowderToGeluleTasks, heightsStep2, transformingFromPowderCapacity).post();
        model.cumulative(transformingFromPowderToSachetTasks, heightsStep2, transformingFromPowderCapacity).post();
        // System.out.println(model.getSolver().findSolution());

        IntVar maxEnd = model.intVar("maxEnd", getMaxEnd(makingPowderTasks, transformingFromPowderToComprimeTasks,
                transformingFromPowderToGeluleTasks, transformingFromPowderToSachetTasks));

        model.setObjective(false, maxEnd);
        Solver solver = model.getSolver();
        // Solution solution = solver.findSolution();
        Solution solution = solver.findOptimalSolution(maxEnd, false);

        System.out.println((solution));
        System.out.println(maxEnd);

    }

    public static int getMaxEnd(Task[] makingPowderTasks, Task[] transformingFromPowderToComprimeTasks,
            Task[] transformingFromPowderToGeluleTasks, Task[] transformingFromPowderToSachetTasks) {
        int maxEnd = 0;

        System.out.println("called");

        maxEnd = maxEndOneTaskList(makingPowderTasks, maxEnd);
        maxEnd = maxEndOneTaskList(transformingFromPowderToComprimeTasks, maxEnd);
        maxEnd = maxEndOneTaskList(transformingFromPowderToGeluleTasks, maxEnd);
        maxEnd = maxEndOneTaskList(transformingFromPowderToSachetTasks, maxEnd);

        return maxEnd;
    }

    private static int maxEndOneTaskList(Task[] tasks, int maxEnd) {
        for (Task task : tasks) {
            if (task.getEnd().getValue() > maxEnd) {
                maxEnd = task.getEnd().getValue();
            }
        }
        return maxEnd;
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
