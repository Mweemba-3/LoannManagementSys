import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;

public class EmployeeDashboard extends JPanel {
    private int employeeId;
    private String employeeName;
    private JPanel cardsPanel;
    
    public EmployeeDashboard(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        initUI();
        loadDashboardStats();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(34, 40, 49));
        
        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Main content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(45, 52, 64));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Cards Panel
        cardsPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        cardsPanel.setBorder(new EmptyBorder(25, 0, 25, 0));
        cardsPanel.setBackground(new Color(45, 52, 64));
        contentPanel.add(cardsPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 52, 64));
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        
        JLabel titleLabel = new JLabel("EMPLOYEE DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 173, 181));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("Welcome, " + employeeName);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 52, 64));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        addLogo(sidebar);
        addMenuItems(sidebar);
        addLogoutButton(sidebar);
        
        return sidebar;
    }
    
    private void addLogo(JPanel sidebar) {
        JLabel logoLabel = new JLabel("MS CODEFORGE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(new Color(0, 173, 181));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        sidebar.add(logoLabel);
    }
    
    private void addMenuItems(JPanel sidebar) {
        String[] menuItems = {"👥 Clients", "💰 Loans", "💳 Payments", "🔒 Change Password"};
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }
    
    private void addLogoutButton(JPanel sidebar) {
        sidebar.add(Box.createVerticalGlue());
        JButton logoutButton = createLogoutButton();
        sidebar.add(logoutButton);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(57, 62, 70));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(new Color(70, 76, 85)); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(new Color(57, 62, 70)); }
        });
        
        button.addActionListener(e -> handleMenuClick(text));
        return button;
    }
    
    private JButton createLogoutButton() {
        JButton button = new JButton("🚪 LOGOUT");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 107, 107));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(new Color(255, 77, 77)); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(new Color(255, 107, 107)); }
        });
        
        button.addActionListener(e -> ScreenManager.getInstance().showScreen(new LoginScreen()));
        return button;
    }
    
    private void handleMenuClick(String menuItem) {
        String menuText = menuItem.substring(menuItem.indexOf(" ") + 1).trim();
        
        switch (menuText) {
            case "Clients":
                ScreenManager.getInstance().showScreen(new ClientsScreen(employeeId, "employee"));
                break;
            case "Loans":
                ScreenManager.getInstance().showScreen(new LoansScreen(employeeId, "employee"));
                break;
            case "Payments":
                ScreenManager.getInstance().showScreen(new PaymentsScreen(employeeId, "employee"));
                break;
            case "Change Password":
                ScreenManager.getInstance().showScreen(new ChangePasswordScreen(employeeId, "employee"));
                break;
        }
    }
    
    private void loadDashboardStats() {
        Map<String, Integer> stats = fetchDashboardStats();
        updateDashboardCards(stats);
    }
    
    private Map<String, Integer> fetchDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            stats.put("totalClients", getCount(conn, "SELECT COUNT(*) FROM clients"));
            stats.put("dueClients", getCount(conn, 
                "SELECT COUNT(DISTINCT c.client_id) FROM clients c " +
                "JOIN loans l ON c.client_id = l.client_id " +
                "JOIN loan_payments lp ON l.loan_id = lp.loan_id " +
                "WHERE lp.status = 'Overdue'"));
            stats.put("activeClients", getCount(conn, 
                "SELECT COUNT(DISTINCT client_id) FROM loans WHERE status IN('Active','Approved') "));
            stats.put("pendingDisbursement", getCount(conn, 
                "SELECT COUNT(*) FROM loans WHERE status = 'Pending' AND disbursement_date IS NULL"));
            
        } catch (SQLException ex) {
            showDatabaseError("Error loading dashboard stats: " + ex.getMessage());
        }
        return stats;
    }
    
    private int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    private void updateDashboardCards(Map<String, Integer> stats) {
        cardsPanel.removeAll();
        
        cardsPanel.add(createDashboardCard("Total Clients", 
            formatNumber(stats.getOrDefault("totalClients", 0)), 
            new Color(0, 173, 181)));
        
        cardsPanel.add(createDashboardCard("Due Clients", 
            formatNumber(stats.getOrDefault("dueClients", 0)), 
            new Color(255, 107, 107)));
        
        cardsPanel.add(createDashboardCard("Active Clients", 
            formatNumber(stats.getOrDefault("activeClients", 0)), 
            new Color(97, 218, 121)));
        
        cardsPanel.add(createDashboardCard("Pending Disbursement", 
            formatNumber(stats.getOrDefault("pendingDisbursement", 0)), 
            new Color(255, 159, 67)));
        
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
    
    private JPanel createDashboardCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(57, 62, 70));
        card.setBorder(new CompoundBorder(
            new LineBorder(accentColor, 2),
            new EmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(200, 200, 200));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, BorderLayout.CENTER);
        
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(65, 70, 80)); }
            @Override public void mouseExited(MouseEvent e) { card.setBackground(new Color(57, 62, 70)); }
        });
        
        return card;
    }
    
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    private void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(this, message,
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}