import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class ExpenseTracker {
    private static Connection conn;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expensetrackerdb"; // Change to your database URL
    private static final String DB_USER = "root"; // Change to your username
    private static final String DB_PASSWORD = "animeshse22uecm058"; // Change to your password

    private JFrame frame;
    private JTable table;
    private JTextField dateField;
    private JTextField descField;
    private JTextField amountField;
    private JTextField nameField;
    private int currentAccountId = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ExpenseTracker window = new ExpenseTracker();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ExpenseTracker() {
        initDB();
        initialize();
    }

    private void initDB() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = conn.createStatement();
            String createAccountsTable = """
                    CREATE TABLE IF NOT EXISTS accounts (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
                    );
                    """;
            String createExpensesTable = """
                    CREATE TABLE IF NOT EXISTS expenses (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        account_id INT NOT NULL,
                        date DATE NOT NULL,
                        description TEXT NOT NULL,
                        amount DOUBLE NOT NULL,
                        FOREIGN KEY (account_id) REFERENCES accounts(id)
                    );
                    """;
//            createExpensesTable = """
//                   ALTER TABLE expenses DROP savings;
//                   """;
            statement.executeUpdate(createAccountsTable);
            statement.executeUpdate(createExpensesTable);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void addAccount(String accountName, double income) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "INSERT INTO accounts (name, income) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountName);
            stmt.setDouble(2, income);
            stmt.executeUpdate();
            stmt.close();
            JOptionPane.showMessageDialog(frame, "Account Added Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding account: " + e.getMessage());
        }
    }

    public void loadData(DefaultTableModel model, int acId) throws SQLException {
        model.setRowCount(0); // Clear existing data
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String sql = "SELECT account_id, date, description, amount, transactions FROM expenses WHERE account_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, acId);
        ResultSet rs = ps.executeQuery();
        Object[] row = new Object[5]; // Add one more column for transactions
        while (rs.next()) {
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            model.addRow(row);
        }
        rs.close();
        ps.close();
    }

    private void addExpense(int accountId, String date, String description, double amount, double transactions, double savings) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

//           double savings = calculateSavings(accountId, amount);

            String sql = "INSERT INTO expenses (account_id, date, description, amount, transactions) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setString(2, date);
            stmt.setString(3, description);
            stmt.setDouble(4, amount);
            stmt.setDouble(5, transactions);
            stmt.executeUpdate();
            stmt.close();
            JOptionPane.showMessageDialog(frame, "Expense added successfully to Account ID: " + accountId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding expense: " + e.getMessage());
        }
    }

    public double calculateSavings(int accountId) {
        double savings = 0.0;
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        // Step 1: Fetch income from the accounts table for the given account
        String incomeQuery = "SELECT income FROM accounts WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(incomeQuery)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalIncome = rs.getDouble("income");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Step 2: Fetch total expenses from the expenses table for the given account_id
        String expensesQuery = "SELECT SUM(amount) AS total_expenses FROM expenses WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(expensesQuery)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalExpenses += rs.getDouble("total_expenses");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Step 3: Calculate savings as income minus total expenses
        savings = totalIncome - totalExpenses;

        return savings;
    }

    public void updateCombox(JComboBox<String> cbx) throws SQLException {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String sql = "SELECT * FROM accounts";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            cbx.addItem(rs.getString("id") + "|" + rs.getString("name"));
        }
        rs.close();
        ps.close();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblAccounts = new JLabel("Select Account:");
        lblAccounts.setBounds(10, 10, 120, 25);
        frame.getContentPane().add(lblAccounts);

        JComboBox<String> accountComboBox = new JComboBox<>();
        accountComboBox.setBounds(140, 10, 200, 25);
        frame.getContentPane().add(accountComboBox);

        JButton refreshButton = new JButton("Refresh Accounts");
        refreshButton.setBounds(350, 10, 150, 25);
        frame.getContentPane().add(refreshButton);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        lblDate.setBounds(10, 50, 150, 25);
        frame.getContentPane().add(lblDate);

        dateField = new JTextField();
        dateField.setBounds(170, 50, 120, 25);
        frame.getContentPane().add(dateField);

        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setBounds(10, 90, 120, 25);
        frame.getContentPane().add(lblDesc);

        descField = new JTextField();
        descField.setBounds(170, 90, 250, 25);
        frame.getContentPane().add(descField);

        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setBounds(10, 130, 120, 25);
        frame.getContentPane().add(lblAmount);

        amountField = new JTextField();
        amountField.setBounds(170, 130, 120, 25);
        frame.getContentPane().add(amountField);

        JButton addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setBounds(10, 170, 120, 25);
        frame.getContentPane().add(addExpenseButton);

        JLabel lblAddAccount = new JLabel("Add New Account:");
        lblAddAccount.setBounds(10, 210, 150, 25);
        frame.getContentPane().add(lblAddAccount);

        nameField = new JTextField();
        nameField.setBounds(170, 210, 200, 25);
        frame.getContentPane().add(nameField);

        JButton addAccountButton = new JButton("Add Account");
        addAccountButton.setBounds(10, 250, 120, 25);
        frame.getContentPane().add(addAccountButton);

        JLabel lblExpenses = new JLabel("Expenses:");
        lblExpenses.setBounds(10, 290, 120, 25);
        frame.getContentPane().add(lblExpenses);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 320, 760, 200);
        frame.getContentPane().add(scrollPane);

        table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Account id", "Date", "Description", "Amount", "Transactions"}, 0);
        table.setModel(tableModel);
        scrollPane.setViewportView(table);

        JButton loadButton = new JButton("Load Expenses");
        loadButton.setBounds(140, 290, 150, 25);
        frame.getContentPane().add(loadButton);

        // Transactions Label
        JLabel lblTransactions = new JLabel("Transaction:");
        lblTransactions.setBounds(425, 90, 120, 25);
        frame.getContentPane().add(lblTransactions);

        // Transactions Text Field
        JTextField transactionsField = new JTextField();
        transactionsField.setBounds(500, 90, 250, 25);
        frame.getContentPane().add(transactionsField);

        // Adding income amount
        JLabel lblIncomeAmount = new JLabel("Income Amount:");
        lblIncomeAmount.setBounds(375 , 210, 150, 25);
        frame.getContentPane().add(lblIncomeAmount);

        JTextField incomeField = new JTextField();
        incomeField.setBounds(475 , 210, 200, 25);
        frame.getContentPane().add(incomeField);

        // Add Savings Button
        JButton savingsButton = new JButton("Show Savings");
        savingsButton.setBounds(10, 540, 150, 25);
        frame.getContentPane().add(savingsButton);

        JLabel savingsLabel = new JLabel("Savings: ");
        savingsLabel.setBounds(180, 540, 200, 25);
        frame.getContentPane().add(savingsLabel);

        // Event Listeners
        refreshButton.addActionListener(e -> {
            try {
                updateCombox(accountComboBox);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error refreshing accounts: " + ex.getMessage());
            }
        });

        addAccountButton.addActionListener(e -> {
            String accountName = nameField.getText().trim();
            String incomes = incomeField.getText().trim();
            double incomess = Double.parseDouble(incomes);

            if (!accountName.isEmpty()) {
                addAccount(accountName, incomess);
                nameField.setText("");
                incomeField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Account name cannot be empty!");
            }
        });

        loadButton.addActionListener(e -> {
            String selected = (String) accountComboBox.getSelectedItem();
            if (selected != null) {
                currentAccountId = Integer.parseInt(selected.split("\\|")[0]);
                try {
                    loadData(tableModel, currentAccountId); // Fetch and display all columns, including transactions
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error loading expenses: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an account.");
            }
        });


        addExpenseButton.addActionListener(e -> {
            String selected = (String) accountComboBox.getSelectedItem();
            if (selected != null) {
                currentAccountId = Integer.parseInt(selected.split("\\|")[0]); // Ensure currentAccountId is updated
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an account first!");
                return; // Exit if no account is selected
            }

            String date = dateField.getText().trim();
            String description = descField.getText().trim();
            String amountText = amountField.getText().trim();
            String transactional = transactionsField.getText().trim();
            double savings = calculateSavings(currentAccountId);

            if (date.isEmpty() || description.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!");
            } else {
                try {
                    double amount = Double.parseDouble(amountText);
                    double transactions = Double.parseDouble(transactional);
                    addExpense(currentAccountId, date, description, amount, transactions, savings);
                    dateField.setText("");
                    descField.setText("");
                    amountField.setText("");
                    transactionsField.setText("");
                    loadData(tableModel, currentAccountId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Amount must be a valid number.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error adding expense: " + ex.getMessage());
                }
            }
        });

        savingsButton.addActionListener(e -> {
            String selected = (String) accountComboBox.getSelectedItem();
            if (selected != null) {
                currentAccountId = Integer.parseInt(selected.split("\\|")[0]); // Ensure currentAccountId is updated
                double savings = calculateSavings(currentAccountId);
                savingsLabel.setText("Savings: " + String.format("%.2f", savings));
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an account first!");
            }
        });

    }
}
