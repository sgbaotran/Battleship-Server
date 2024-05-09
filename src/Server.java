
import control.Control;
import view.View;
import model.Model;

public class Server {
    public static void main(String[] args) throws Exception {

        View view = new View();

        Model model = new Model();

        Control control = new Control(view, model);

    }
}
