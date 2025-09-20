import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ChangePasswordScreen extends JPanel {
    private int employeeId;
    private String userRole;
    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, cancelButton;
    
    public ChangePasswordScreen(int employeeId, String userRole) {
        this.employeeId = employeeId;
        this.userRole = userRole;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(240, 242, 245));
        
        JButton backButton = new JButton("← Back to Dashboard");
        styleButton(backButton, new Color(100, 100, 100), new Color(80, 80, 80));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.addActionListener(e -> goBackToDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("CHANGE PASSWORD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Current Password:"), gbc);
        
        gbc.gridx = 1;
        currentPasswordField = new JPasswordField(20);
        currentPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(currentPasswordField, gbc);
        
        // New Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(newPasswordField, gbc);
        
        // Confirm New Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm New Password:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(confirmPasswordField, gbc);
        
        // Button Panel
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        saveButton = new JButton("Save Changes");
        styleButton(saveButton, new Color(46, 125, 50), new Color(39, 105, 42));
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.addActionListener(e -> changePassword());
        buttonPanel.add(saveButton);
        
        cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(120, 120, 120), new Color(100, 100, 100));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> goBackToDashboard());
        buttonPanel.add(cancelButton);
        
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void goBackToDashboard() {
        if ("admin".equals(userRole)) {
            ScreenManager.getInstance().showScreen(new AdminDashboard(employeeId, getEmployeeName(employeeId)));
        } else {
            ScreenManager.getInstance().showScreen(new EmployeeDashboard(employeeId, getEmployeeName(employeeId)));
        }
    }
    
    private String getEmployeeName(int employeeId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name FROM employees WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
            System.err.println("Error getting employee name: " + ex.getMessage());
        }
        return "User";
    }
    
    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all password fields");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match");
            return;
        }
        
        if (newPassword.length() < 4) {
            showError("New password must be at least 4 characters long");
            return;
        }
        
        if (newPassword.equals(currentPassword)) {
            showError("New password must be different from current password");
            return;
        }
        
        // Verify current password
        if (!verifyCurrentPassword(currentPassword)) {
            showError("Current password is incorrect");
            return;
        }
        
        // Update password
        if (updatePassword(newPassword)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            goBackToDashboard();
        } else {
            showError("Error changing password");
        }
    }
    
    private boolean verifyCurrentPassword(String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT password FROM employees WHERE employee_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword);
            }
        } catch (SQLException ex) {
            showError("Error verifying current password: " + ex.getMessage());
        }
        return false;
    }
    
    private boolean updatePassword(String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE employees SET password = ? WHERE employee_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setInt(2, employeeId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            showError("Error updating password: " + ex.getMessage());
            return false;
        }
    }
    
    private void clearForm() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}