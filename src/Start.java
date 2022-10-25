import org.chocosolver.solver.Model;

public class Start {

    public static void main(String[] args) {
        int n = 10; 
        Model model = new Model("nQueens");
        System.out.println(model.getName());
        model.intVar("tableau", 1, n);
    }
}
