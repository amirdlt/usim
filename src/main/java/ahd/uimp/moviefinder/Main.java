package ahd.uimp.moviefinder;

import ahd.ulib.swingutils.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(new MainFrame() {{
            add(new MoviePanel());
        }});
    }
}
