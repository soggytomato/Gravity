import javax.swing.*;


public class App {


    public static void main(String[] args) {

        JFrame window = new JFrame("Gravity");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AppPanel gp = new AppPanel();
        window.setContentPane(gp);

        window.pack(); //Set size to contents of window
        window.setVisible(true);

    }
}
