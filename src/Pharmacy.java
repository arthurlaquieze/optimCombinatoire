import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

public class Pharmacy {
    public static Integer pharmacyProblem(List<Order> orders, int capacityStep1, boolean parallelStep2ComprimeGelule,
            boolean verbose) {
        int nOrders = orders.size();

        // create processes
        Processus toPowderComprime = new Processus(20, 1, "Transformation to comprimé power");
        Processus toPowderGelule = new Processus(7, 1, "Transformation to gélule powder");
        Processus toPowderSachet = new Processus(14, 1, "Transformation to sachet powder");

        Processus powderToComprime = new Processus(15, 2, "Conditionnement vers comprimé");
        Processus powderToGelule = new Processus(5, 2, "Conditionnement vers gélule");
        Processus powderToSachet = new Processus(20, 2, "Conditionnement vers sachet");

        Model model = new Model("Pharmacy");

        // capacity variable, i.e. one at a time or two with question 7
        IntVar[] heightsPowder = new IntVar[3 * nOrders];
        Arrays.fill(heightsPowder, model.intVar(1));
        IntVar makingPowderCapacity = model.intVar(capacityStep1);

        IntVar[] heightsStep2 = new IntVar[nOrders];
        Arrays.fill(heightsStep2, model.intVar(1));
        IntVar[] heightsStep2Question4 = new IntVar[2 * nOrders];
        Arrays.fill(heightsStep2Question4, model.intVar(1));

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
            }
        }

        // post capacities constraints for step 2.
        model.cumulative(transformingFromPowderToSachetTasks, heightsStep2, transformingFromPowderCapacity).post();

        // differentiate from cases "Gélules et comprimés peuvent être traités en
        // parallèle" and the opposite
        if (parallelStep2ComprimeGelule == true) {
            model.cumulative(transformingFromPowderToComprimeTasks, heightsStep2, transformingFromPowderCapacity)
                    .post();
            model.cumulative(transformingFromPowderToGeluleTasks, heightsStep2, transformingFromPowderCapacity).post();
        } else {
            Task[] comprimesOrGeluleTasks = new Task[2 * nOrders];
            for (int i = 0; i < nOrders; i++) {
                comprimesOrGeluleTasks[i] = transformingFromPowderToComprimeTasks[i];
                comprimesOrGeluleTasks[i + nOrders] = transformingFromPowderToGeluleTasks[i];
            }

            model.cumulative(comprimesOrGeluleTasks, heightsStep2Question4, transformingFromPowderCapacity).post();
        }

        List<Task[]> allTasks = List.of(makingPowderTasks, transformingFromPowderToComprimeTasks,
                transformingFromPowderToGeluleTasks, transformingFromPowderToSachetTasks);

        // add objective to be optimized, i.e. the end of the latest order completed
        model.setObjective(false,
                model.intVar(getMaxEnd(allTasks)));

        // solve
        Solver solver = model.getSolver();
        Solution solution = solver.findSolution();

        // print all orders
        if (verbose) {
            System.out.println("");
            for (Order order : orders) {
                System.out.println(order);
            }
            System.out.println("");
        }

        if (solution != null) {
            int solvedMaxEnd = getMaxEnd(allTasks);

            if (verbose) {
                System.out.println("dernière commande traitée le " + solvedMaxEnd / (24 * 60) + "e jour");
                System.out.println("cela représente " + solvedMaxEnd + " minutes à partir de maintenant\n");

                // print solution
                boolean showMinutes = false; // should be set to false when there is a lot of orders
                System.out.println("making powder ; format days:hours(:minutes)\t|| transform powder (step2)");
                System.out.println(
                        "comprimé\tgélule\t\tsachet\t\t|| comprimé\tgélule\t\tsachet");
                System.out.println("start\tend\tstart\tend\tstart\tend\t|| start\tend\tstart\tend\tstart\tend");
                for (int i = 0; i < nOrders; i++) {
                    System.out.println(
                            timeConvert(makingPowderTasks[i].getStart().getValue(), showMinutes) + "\t"
                                    + timeConvert(makingPowderTasks[i].getEnd().getValue(), showMinutes)
                                    + "\t"
                                    + timeConvert(makingPowderTasks[i + 1].getStart().getValue(), showMinutes) + "\t"
                                    + timeConvert(makingPowderTasks[i + 1].getEnd().getValue(), showMinutes) + "\t"
                                    + timeConvert(makingPowderTasks[i + 2].getStart().getValue(), showMinutes) + "\t"
                                    + timeConvert(makingPowderTasks[i + 2].getEnd().getValue(), showMinutes) + "\t"
                                    + "|| "
                                    + timeConvert(transformingFromPowderToComprimeTasks[i].getStart().getValue(),
                                            showMinutes)
                                    + "\t"
                                    + timeConvert(transformingFromPowderToComprimeTasks[i].getEnd().getValue(),
                                            showMinutes)
                                    + "\t"
                                    + timeConvert(transformingFromPowderToGeluleTasks[i].getStart().getValue(),
                                            showMinutes)
                                    + "\t"
                                    + timeConvert(transformingFromPowderToGeluleTasks[i].getEnd().getValue(),
                                            showMinutes)
                                    + "\t"
                                    + timeConvert(transformingFromPowderToSachetTasks[i].getStart().getValue(),
                                            showMinutes)
                                    + "\t"
                                    + timeConvert(transformingFromPowderToSachetTasks[i].getEnd().getValue(),
                                            showMinutes)
                                    + "\t");
                }
            }

            return solvedMaxEnd;
        } else {
            if (verbose) {
                System.out.println("no solution found");
            }

            return null;
        }
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

    /**
     * @param time
     * @return time formatted in days:hours:minutes format
     */
    public static String timeConvert(int time, boolean showMinutes) {
        if (showMinutes) {
            return time / (24 * 60) + "j" + time / 60 % 24 + ':' + time % 60;
        } else {
            return time / (24 * 60) + "j" + time / 60 % 24 + 'h';
        }
    }

    public static void main(String[] args) {
        boolean useRandomOrders = true;
        int nOrders;
        List<Order> orders;

        if (useRandomOrders) {
            nOrders = 200; // number of orders to simulate
            orders = generateOrders(nOrders);
        } else {
            nOrders = 3; // number of orders to simulate
            orders = new ArrayList<Order>();
            orders.add(new Order(73, 40, 0, 2));
            orders.add(new Order(30, 42, 58, 10));
            orders.add(new Order(21, 47, 54, 3));
        }

        /**
         * either:
         * 
         * 2: first formulation,
         * 
         * 4: conditionnement en gélule et comprimé ne peut pas se faire en parallèle,
         * étape 1 capacité de 1
         * 
         * 6: conditionnement en gélule et comprimé ne peut pas se faire en parallèle,
         * étape 1 capacité de 2
         */
        int nQuestion = 6;

        int capacityStep1;
        boolean parallelStep2ComprimeGelule;

        switch (nQuestion) {
            case 2:
                capacityStep1 = 1;
                parallelStep2ComprimeGelule = true;
                break;
            case 4:
                capacityStep1 = 1;
                parallelStep2ComprimeGelule = false;
                break;
            case 6:
                capacityStep1 = 2;
                parallelStep2ComprimeGelule = false;
                break;
            default:
                capacityStep1 = 1;
                parallelStep2ComprimeGelule = true;
        }

        Integer lastOrderCompletion = pharmacyProblem(orders, capacityStep1, parallelStep2ComprimeGelule, false);

        System.out.println(lastOrderCompletion);
    }
}
