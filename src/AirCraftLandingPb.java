import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AirCraftLandingPb {

    public static void acl() {
        // number of planes
        int N = 10;
        // Times per plane:
        // {earliest landing time, target landing time, latest landing time}
        int[][] LT = {
                {129, 155, 559},
                {195, 258, 744},
                {89, 98, 510},
                {96, 106, 521},
                {110, 123, 555},
                {120, 135, 576},
                {124, 138, 577},
                {126, 140, 573},
                {135, 150, 591},
                {160, 180, 657}};
        // penalty cost penalty cost per unit of time per plane:
        // {for landing before target, after target}
        int[][] PC = {
                {10, 10},
                {10, 10},
                {30, 30},
                {30, 30},
                {30, 30},
                {30, 30},
                {30, 30},
                {30, 30},
                {30, 30},
                {30, 30}};
        // Separation time required after i lands before j can land
        int[][] ST = {
                {99999, 3, 15, 15, 15, 15, 15, 15, 15, 15},
                {3, 99999, 15, 15, 15, 15, 15, 15, 15, 15},
                {15, 15, 99999, 8, 8, 8, 8, 8, 8, 8},
                {15, 15, 8, 99999, 8, 8, 8, 8, 8, 8},
                {15, 15, 8, 8, 99999, 8, 8, 8, 8, 8},
                {15, 15, 8, 8, 8, 99999, 8, 8, 8, 8},
                {15, 15, 8, 8, 8, 8, 99999, 8, 8, 8},
                {15, 15, 8, 8, 8, 8, 8, 999999, 8, 8},
                {15, 15, 8, 8, 8, 8, 8, 8, 99999, 8},
                {15, 15, 8, 8, 8, 8, 8, 8, 8, 99999}};

        Model model = new Model("Aircraft landing");
        // Variables declaration
        // todo

        // Constraint posting
        // todo

        // Resolution process
        /*Solver solver = model.getSolver();
        solver.plugMonitor((IMonitorSolution) () -> {
            for (int i = 0; i < N; i++) {
                System.out.printf("%s lands at %d (%d)\n",
                        planes[i].getName(),
                        planes[i].getValue(),
                        planes[i].getValue() - LT[i][1]);
            }
            System.out.printf("Deviation cost: %d\n", tot_dev.getValue());
        });
        Map<IntVar, Integer> map = IntStream
                .range(0, N)
                .boxed()
                .collect(Collectors.toMap(i -> planes[i], i -> LT[i][1]));
        solver.setSearch(Search.intVarSearch(
                variables -> Arrays.stream(variables)
                        .filter(v -> !v.isInstantiated())
                        .min((v1, v2) -> closest(v2, map) - closest(v1, map))
                        .orElse(null),
                var -> closest(var, map),
                DecisionOperatorFactory.makeIntEq(),
                planes
        ));
        solver.showShortStatistics();
        solver.findOptimalSolution(tot_dev, false);*/
    }

    private static int closest(IntVar var, Map<IntVar, Integer> map) {
        int target = map.get(var);
        if (var.contains(target)) {
            return target;
        } else {
            int p = var.previousValue(target);
            int n = var.nextValue(target);
            return Math.abs(target - p) < Math.abs(n - target) ? p : n;
        }
    }

    public static void main(String[] args) {
        acl();
    }

}
