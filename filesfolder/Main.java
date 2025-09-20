import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize database connection
                DatabaseConnection.getConnection();
                
                // Show login screen
                ScreenManager.getInstance().showScreen(new LoginScreen());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to initialize application: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}