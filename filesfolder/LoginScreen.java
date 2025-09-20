import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 35, 40)); // Dark background

        // Main dark container
        JPanel darkPanel = new JPanel(new GridBagLayout());
        darkPanel.setBackground(new Color(45, 50, 60)); // Darker slate
        darkPanel.setBorder(BorderFactory.createEmptyBorder(50, 70, 60, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login Title (Teal accent)
        JLabel loginLabel = new JLabel("LOGIN", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginLabel.setForeground(new Color(0, 180, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        darkPanel.add(loginLabel, gbc);

        // Username Label (Light gray)
        JLabel usernameLabel = new JLabel("Name:", SwingConstants.LEFT);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(180, 180, 180));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        darkPanel.add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField();
        styleTextField(usernameField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        darkPanel.add(usernameField, gbc);

        // Password Label (Light gray)
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.LEFT);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(180, 180, 180));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        darkPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        darkPanel.add(passwordField, gbc);

        // Login Button (Teal accent)
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(320, 50));
        loginButton.setBackground(new Color(0, 150, 150));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.addActionListener(e -> performLogin());
        
        // Hover effects
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(0, 170, 170));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(0, 150, 150));
            }
            public void mousePressed(MouseEvent e) {
                loginButton.setBackground(new Color(0, 130, 130));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        darkPanel.add(loginButton, gbc);

        // Center the panel
        add(darkPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("Brought to you by © MS CodeForge Ltd", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(120, 120, 120));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void styleTextField(JComponent field) {
        field.setPreferredSize(new Dimension(320, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(60, 65, 70));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 85, 90)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both fields", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM employees WHERE name = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("employee_id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                
                if ("admin".equals(role)) {
                    ScreenManager.getInstance().showScreen(new AdminDashboard(id, name));
                } else {
                    ScreenManager.getInstance().showScreen(new EmployeeDashboard(id, name));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error", 
                "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}