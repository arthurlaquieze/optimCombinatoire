import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

public class Pharmacy {
    public static void pharmacyProblem(int nOrders) {
        nOrders = 1;

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

        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(1, 0, 0, 2));
        // List<Order> orders = generateOrders(nOrders);

        Model model = new Model("Pharmacy");

        // capacity variable, i.e. one at a time
        IntVar[] heightsPowder = new IntVar[3 * nOrders];
        Arrays.fill(heightsPowder, model.intVar(1));
        IntVar makingPowderCapacity = model.intVar(1);

        IntVar[] heightsStep2 = new IntVar[nOrders];
        Arrays.fill(heightsStep2, model.intVar(1));
        IntVar transformingFromPowderCapacity = model.intVar(1);

        /**
         * add matching tasks for each order
         * 
         * first step (making power) has only one array as the capacity is of only one
         * 
         * 2nd step (making capsules etc from powder) separates tasks by type as the
         * capacity is 1 per type (as of question 2)
         */
        Task[] makingPowderTasks = new Task[3 * nOrders];
        Task[] transformingFromPowderToComprimeTasks = new Task[nOrders];
        Task[] transformingFromPowderToGeluleTasks = new Task[nOrders];
        Task[] transformingFromPowderToSachetTasks = new Task[nOrders];
        for (int i = 0; i < nOrders; i++) {
            Order order = orders.get(i);
            // add tasks for step 1, transformation to powder
            {
                /**
                 * earliest time is set to 0, latest to 9999999 ('infinity') as constraints will
                 * go elsewhere
                 * 
                 * duration is the process duration times the quantity to be made in that order
                 * 
                 * latest time is order's due time
                 * 
                 * For each order we create tasks of making gélule, comprimé and sachet
                 */
                makingPowderTasks[3 * i] = new Task(model, 0, 9999999,
                        order.getComprimesQuantity() * toPowderComprime.getDuration(), 0, order.getDueTime());
                makingPowderTasks[3 * i + 1] = new Task(model, 0, 9999999,
                        order.getGelulesQuantity() * toPowderGelule.getDuration(), 0, order.getDueTime());
                makingPowderTasks[3 * i + 2] = new Task(model, 0, 9999999,
                        order.getSachetsQuantity() * toPowderSachet.getDuration(), 0, order.getDueTime());
            }
        }

        // post capacity constraint for step 1
        model.cumulative(makingPowderTasks, heightsPowder, makingPowderCapacity).post();

        for (int i = 0; i < nOrders; i++) {
            Order order = orders.get(i);
            // add tasks for step 2, transformation from powder to gélules, comprimés and
            // sachets
            {
                transformingFromPowderToComprimeTasks[i] = new Task(model,
                        0, 9999999,
                        order.getComprimesQuantity() * powderToComprime.getDuration(), 0, order.getDueTime());
                transformingFromPowderToGeluleTasks[i] = new Task(model,
                        0, 9999999,
                        order.getGelulesQuantity() * powderToGelule.getDuration(), 0, order.getDueTime());
                transformingFromPowderToSachetTasks[i] = new Task(model,
                        0, 9999999,
                        order.getSachetsQuantity() * powderToSachet.getDuration(), 0, order.getDueTime());

                // add constraint step 1 is before step 2, for each corresponding steps of one
                // order
                model.arithm(transformingFromPowderToComprimeTasks[i].getStart(), ">=",
                        makingPowderTasks[3 * i].getEnd()).post();
                model.arithm(transformingFromPowderToGeluleTasks[i].getStart(), ">=",
                        makingPowderTasks[3 * i + 1].getEnd()).post();
                model.arithm(transformingFromPowderToSachetTasks[i].getStart(), ">=",
                        makingPowderTasks[3 * i + 2].getEnd()).post();

                System.out.println("begin of making comprimés at " + makingPowderTasks[3 * i].getEnd().getValue());
            }
        }

        // post capacities constraints for step 2
        model.cumulative(transformingFromPowderToComprimeTasks, heightsStep2, transformingFromPowderCapacity).post();
        model.cumulative(transformingFromPowderToGeluleTasks, heightsStep2, transformingFromPowderCapacity).post();
        model.cumulative(transformingFromPowderToSachetTasks, heightsStep2, transformingFromPowderCapacity).post();

        List<Task[]> allTasks = List.of(makingPowderTasks, transformingFromPowderToComprimeTasks,
                transformingFromPowderToGeluleTasks, transformingFromPowderToSachetTasks);

        // add objective to be optimized, i.e. the end of the latest order completed
        model.setObjective(false,
                model.intVar(getMaxEnd(allTasks)));

        Solver solver = model.getSolver();
        Solution solution = solver.findSolution();

        System.out.println((solution));
        System.out.println("\n" + getMaxEnd(allTasks) / (24 * 60) + "e jour");

        System.out.println("\n" + getMaxEnd(allTasks) + " minutes");
    }

    /**
     * function that finds the latest end of all tasks provided. Used for the solver
     * objective
     * 
     * @tasksList list of arrays of tasks
     * @return max end time gotten with task.getEnd().getValue()
     */
    public static int getMaxEnd(List<Task[]> tasksList) {
        int maxEnd = 0;

        System.out.println("called");

        for (Task[] tasks : tasksList) {
            for (Task task : tasks) {
                if (task.getEnd().getValue() > maxEnd) {
                    maxEnd = task.getEnd().getValue();
                }
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
