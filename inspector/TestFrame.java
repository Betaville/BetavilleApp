import javax.swing.*;

    public class TestFrame extends JFrame {
        public TestFrame() {
            JLabel label = new JLabel("Some info");
            JButton button = new JButton("Ok");
            Box b = new Box(BoxLayout.Y_AXIS);
            b.add(label);
            b.add(button);
            getContentPane().add(b);

        }
        public static void main(String[] args) {
            JFrame f = new TestFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setLocationRelativeTo(null);
            f.setVisible(true);

        }
    }