import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ScreenManager {
    private static ScreenManager instance;
    private JFrame mainFrame;
    private Map<String, JPanel> screens;
    private JPanel currentScreen;

    private ScreenManager() {
        mainFrame = new JFrame("MS CODEFORGE");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);
        
        screens = new HashMap<>();
    }

    public static synchronized ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void showScreen(JPanel screen) {
        if (currentScreen != null) {
            mainFrame.remove(currentScreen);
        }
        
        currentScreen = screen;
        mainFrame.add(currentScreen);
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    public void addScreen(String name, JPanel screen) {
        screens.put(name, screen);
    }

    public JPanel getScreen(String name) {
        return screens.get(name);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
}