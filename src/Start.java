import org.chocosolver.solver.Model;

public class Start {

    public static void main(String[] args) {
        Model model = new Model("nQueens");
        System.out.println(model.getName());
    }
}
