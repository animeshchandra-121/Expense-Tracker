package income_and_expense_tracker;//package income_and_expense_tracker;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.Objects;

public class ExpenseAndIncomeTrackerApp {
    // for gui and ui components
    private JFrame frame;
    private JPanel dashboardpanel;
    private JPanel buttonPanel;
    private JLabel buttonlabel;
    private JButton addTransaction;
    private JButton removeTransaction;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;

    //calculating the amount
    private double totalamount = 0.0;
    //storing the datapanel values
    private final ArrayList<String> datapanelvalues = new ArrayList<>(List.of("0.0", "0.0", "0.0"));

    public ExpenseAndIncomeTrackerApp() {
        frame = new JFrame("Expense And Income Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        //creating dashboard panel
        dashboardpanel = new JPanel();
        dashboardpanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        dashboardpanel.setBackground(new Color(236, 240, 241));
        frame.add(dashboardpanel, BorderLayout.CENTER);

        addTransaction = new JButton("Add Transaction");
        addTransaction.setBackground(new Color(41, 128, 185));
        addTransaction.setForeground(Color.white);
        addTransaction.setFocusPainted(false);
        addTransaction.setFont(new Font("Arial", Font.BOLD, 14));
        addTransaction.setCursor(new Cursor(Cursor.HAND_CURSOR));

        removeTransaction = new JButton("Remove Transaction");
        removeTransaction.setBackground(new Color(231, 76, 60));
        removeTransaction.setForeground(Color.white);
        removeTransaction.setFocusPainted(false);
        removeTransaction.setFont(new Font("Arial", Font.BOLD, 14));
        removeTransaction.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout(10, 5));
        buttonPanel.add(addTransaction, BorderLayout.NORTH);
        buttonPanel.add(removeTransaction, BorderLayout.SOUTH);

        dashboardpanel.add(buttonPanel);

        //adding the expense, income and total amount
        addDataPanel("Expense",0);
        addDataPanel("Income",1);
        addDataPanel("Total",2);

        // setting up the column's name
        String[] columns_name = {"ID", "Type", "Description", "Amount"};
        transactionTableModel = new DefaultTableModel(columns_name, 25);
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setBackground(new Color(236, 240, 241));
        transactionTable.setPreferredSize(new Dimension(750, 300));
        JScrollPane scrollpane = new JScrollPane(transactionTable);
        dashboardpanel.add(scrollpane);

        // adding action listener to add event when button is clicked
        addTransaction.addActionListener(e -> {
            transactionDialogueBox();

        });
        removeTransaction.addActionListener(e -> {
            removeTransaction();
        });

        frame.setVisible(true);

    }
    // adding panel to the dashboard
    private void addDataPanel(String title,int index){
        JPanel datapanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(title.equals("Total")){
                    DrawDataPanel(g, title,String.format("$%.2f", totalamount), getWidth(), getHeight());
                }
                else{
                    DrawDataPanel(g, title, datapanelvalues.get(index), getWidth(), getHeight());
                }
            }
        };
        datapanel.setLayout(new GridLayout(2, 1));
        datapanel.setPreferredSize(new Dimension(170, 100));
        datapanel.setBackground(new Color(255, 255, 255));
        datapanel.setBorder(new LineBorder(new Color(149, 165, 166, 2)));
        dashboardpanel.add(datapanel);
    }
    private void DrawDataPanel(Graphics g, String title, String value, int width , int height){
        Graphics2D g2d = (Graphics2D) g;

        // Draw Rectangle to store the value and title
        g2d.setColor(new Color(236, 240, 241));
        g2d.fillRect(0, 0, width, 40);

        // Draw Title
        g2d.setColor(Color.black);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(title, 20, 30);

        // Draw Value
        g2d.setColor(Color.black);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(value, 20, 75);
    }
    // to enter the expense and income , this dialogue box is created seperately
    private void transactionDialogueBox(){
        JDialog dialog = new JDialog(frame, "Add Transaction", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(frame);

        // Adding a panel in which we will add the label and text field to add transaction
        JPanel dialoguePanel = new JPanel();
        dialoguePanel.setLayout(new GridLayout(4, 2, 10, 10)); // Adjust grid layout for spacing
        dialoguePanel.setBackground(Color.lightGray);
        dialoguePanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding around the entire panel

        // Font for labels and buttons
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font inputFont = new Font("Arial", Font.PLAIN, 12);

        // Adding labels and input fields
        JLabel typelabel = new JLabel("Type:");
        typelabel.setForeground(Color.black);
        typelabel.setFont(labelFont);

        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Expense", "Income"});
        typeComboBox.setFont(inputFont);
        typeComboBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel descriptionlabel = new JLabel("Description:");
        descriptionlabel.setForeground(Color.black);
        descriptionlabel.setFont(labelFont);

        JTextField descriptionTextField = new JTextField();
        descriptionTextField.setFont(inputFont);
        descriptionTextField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel amountlabel = new JLabel("Amount:");
        amountlabel.setForeground(Color.black);
        amountlabel.setFont(labelFont);

        JTextField amountTextField = new JTextField();
        amountTextField.setFont(inputFont);
        amountTextField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JButton addButton = new JButton("Add");
        addButton.setFont(labelFont);
        addButton.setBackground(new Color(70, 130, 180)); // Steel blue color
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setFocusPainted(false); // Remove focus border
        addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 170), 2),
                new EmptyBorder(10, 20, 10, 20))); // Outer border and padding

        addButton.addActionListener(e -> {
            addTransaction(typeComboBox, descriptionTextField, amountTextField);
            loaddata(transactionTableModel); // Refresh data
            dialog.dispose(); // Close dialog
            addvalues(datapanelvalues, typeComboBox, amountTextField);
        });

        // Adding components to the panel
        dialoguePanel.add(typelabel);
        dialoguePanel.add(typeComboBox);
        dialoguePanel.add(descriptionlabel);
        dialoguePanel.add(descriptionTextField);
        dialoguePanel.add(amountlabel);
        dialoguePanel.add(amountTextField);
        dialoguePanel.add(new JLabel()); // Empty cell for spacing
        dialoguePanel.add(addButton);

        // Adding the panel to the dialog
        dialog.add(dialoguePanel);


        // Making the dialog visible
        dialog.setVisible(true);
    }
    // below method is used to store data to the sql
    public void addTransaction(JComboBox<String> TypeComboBox, JTextField descriptionTextField, JTextField amountTextField){
        String Type = Objects.requireNonNull(TypeComboBox.getSelectedItem()).toString();
        String Description = (String) descriptionTextField.getText();
       double amount = Double.parseDouble(amountTextField.getText());

        try {
            Connection conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO transaction_table(type, description, amount) VALUES(?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, Type);
            st.setString(2, Description);
            st.setDouble(3, amount);
            st.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void loaddata(DefaultTableModel model) {
        // Clear existing rows
        model.setRowCount(0);

        String sql = "SELECT * FROM transaction_table";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            Object[] row = new Object[4];
            while (rs.next()) {
                for (int i = 0; i < row.length; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void addvalues(ArrayList<String> datapanelvalue, JComboBox<String> TypeComboBox, JTextField amountTextField) {
        String sql = "SELECT * FROM transaction_table";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            double totalExpense = 0.0;
            double totalIncome = 0.0;

            while (rs.next()) { // Iterate through all rows
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");

                if ("Expense".equalsIgnoreCase(type)) {
                    totalExpense += amount;
                } else if ("Income".equalsIgnoreCase(type)) {
                    totalIncome += amount;
                }
            }
            // Update datapanelvalues and totalamount
            datapanelvalue.set(0, String.valueOf(totalExpense));
            datapanelvalue.set(1, String.valueOf(totalIncome));
            totalamount = totalIncome - totalExpense;
            datapanelvalue.set(2, String.valueOf(totalamount));

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        dashboardpanel.repaint();
    }

    // to remove transactions dialogue box
    private void removeTransaction() {
        String transactionId = JOptionPane.showInputDialog(frame, "Enter the Transaction ID to remove:", "Remove Transaction", JOptionPane.QUESTION_MESSAGE);
        if (transactionId != null && !transactionId.trim().isEmpty()) {
            try {
                Connection conn = DataBaseConnection.getConnection();

                // Step 1: Retrieve the transaction details before deleting
                String fetchSql = "SELECT type, amount FROM transaction_table WHERE id = ?";
                PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
                fetchStmt.setInt(1, Integer.parseInt(transactionId.trim()));
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");

                    // Step 2: Delete the transaction
                    String deleteSql = "DELETE FROM transaction_table WHERE id = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                    deleteStmt.setInt(1, Integer.parseInt(transactionId.trim()));
                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Transaction removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Step 3: Update the datapanelvalues based on the transaction type
                        if ("Expense".equalsIgnoreCase(type)) {
                            datapanelvalues.set(2, String.valueOf(totalamount + amount));
                        } else if ("Income".equalsIgnoreCase(type)) {
                            datapanelvalues.set(2, String.valueOf(totalamount - amount));
                        }

                        // Repaint the dashboard and reload the table data
                        dashboardpanel.repaint();
                        loaddata(transactionTableModel);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Transaction ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Transaction ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid Transaction ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static void main(String[] args) {
        new ExpenseAndIncomeTrackerApp();
    }
}