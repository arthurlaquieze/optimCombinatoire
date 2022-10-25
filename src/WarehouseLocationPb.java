import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMiddle;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelectorWithTies;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.Arrays;

public class WarehouseLocationPb {

    public static void wlp() {
        // load parameters
        // number of warehouses
        int W = 5;
        // number of stores
        int S = 10;
        // maintenance cost
        int C = 30;
        // capacity of each warehouse
        int[] K = new int[] { 1, 4, 2, 1, 3 };
        // matrix of supply costs, store x warehouse
        int[][] P = new int[][] {
                { 20, 24, 11, 25, 30 },
                { 28, 27, 82, 83, 74 },
                { 74, 97, 71, 96, 70 },
                { 2, 55, 73, 69, 61 },
                { 46, 96, 59, 83, 4 },
                { 42, 22, 29, 67, 59 },
                { 1, 5, 73, 59, 56 },
                { 10, 73, 13, 43, 96 },
                { 93, 35, 63, 85, 46 },
                { 47, 65, 55, 71, 95 } };

        // A new model instance
        Model model = new Model("Warehouse");

        // VARIABLES
        BoolVar[] openedWarehouses = model.boolVarArray("openedWarehouses", W);
        IntVar[] supppliers = model.intVarArray("suppliers", S, 0, W - 1);

        // CONSTRAINTS
        checkSupplier(model, openedWarehouses, supppliers);
        // RESOLUTION
        /*
         * model.setObjective(Model.MINIMIZE, tot_cost);
         * Solver solver = model.getSolver();
         * solver.setSearch(Search.intVarSearch(
         * new VariableSelectorWithTies<>(
         * new FirstFail(model),
         * new Smallest()),
         * new IntDomainMiddle(false),
         * ArrayUtils.append(supplier, cost, open))
         * );
         * solver.showShortStatistics();
         * while (solver.solve()) {
         * prettyPrint(model, open, W, supplier, S, tot_cost);
         * }
         */
    }

    public static void checkSupplier(Model model, BoolVar[] opened, IntVar[] supplier) {
        for (int i = 0; i < supplier.length; i++) {
            model.arithm(opened[supplier[i].getValue()], "=", 1).post();

        }

    }

    private static void prettyPrint(Model model, IntVar[] open, int W, IntVar[] supplier, int S, IntVar tot_cost) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < W; i++) {
            if (open[i].getValue() > 0) {
                st.append(String.format("\tWarehouse %d supplies customers : ", (i + 1)));
                for (int j = 0; j < S; j++) {
                    if (supplier[j].getValue() == (i + 1)) {
                        st.append(String.format("%d ", (j + 1)));
                    }
                }
                st.append("\n");
            }
        }
        st.append("\tTotal C: ").append(tot_cost.getValue());
        System.out.println(st.toString());
    }

    public static void main(String[] args) {
        wlp();
    }

}
