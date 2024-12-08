# **Expense Tracker Application**

The Expense Tracker Application is a Java-based desktop GUI application designed to help users manage their finances by tracking income, expenses, and savings. The application connects to a MySQL database to store account details and expense records.

## **Features**

##### Account Management:

Add new accounts with income details.
View all available accounts.

##### Expense Tracking:

Add expenses for a specific account with details like date, description, amount, and transaction information.
Display a list of expenses for the selected account.

##### Savings Calculation:

Automatically calculate savings as the difference between total income and total expenses for a specific account.

##### Database Integration:

Persistent storage using MySQL for accounts and expenses data.
Tables are created automatically if they do not exist.

##### Graphical User Interface:

User-friendly interface built with Swing.
Components like text fields, combo boxes, buttons, and tables make interaction intuitive.

## Prerequisites

##### Java Development Kit (JDK):

Ensure JDK 8 or higher is installed on your machine.

##### MySQL Server:

Install and configure MySQL server locally or on a remote server.

##### Database Configuration:

Create a database named expensetrackerdb.

Update the database credentials (DB_USER and DB_PASSWORD) in the source code.

## Installation and Setup

##### Clone or Download the Repository

Download the source code and place it in your desired directory.

##### Configure the Database

Run the following SQL commands to create the database manually (optional; the application can create tables automatically):

```sql
CREATE DATABASE expensetrackerdb;

USE expensetrackerdb;

CREATE TABLE accounts (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
income DOUBLE NOT NULL
);

CREATE TABLE expenses (id INT AUTO_INCREMENT PRIMARY KEY,  
account_id INT NOT NULL,   
date DATE NOT NULL, 
description TEXT NOT NULL,   
amount DOUBLE NOT NULL,  
transactions DOUBLE NOT NULL,  
FOREIGN KEY (account_id) REFERENCES accounts(id) );
```

##### Configure Database Credentials

Update the DB_URL, DB_USER, and DB_PASSWORD constants in the code with your database details:

```java

private static final String DB_URL = "jdbc:mysql://localhost:3306/expensetrackerdb";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

##### Compile and Run the Application

Use the following commands to compile and run the program:

```bash

javac ExpenseTracker.java
java ExpenseTracker
```

## Usage

Refresh Accounts:

Click the "Refresh Accounts" button to load all available accounts into the dropdown.

##### Add New Account:

Enter the account name and income, then click "Add Account" to create a new account.

##### Add Expense:

Select an account from the dropdown.
Fill in the date, description, amount, and transaction details.
Click "Add Expense" to save the expense.

##### View Expenses:

Select an account and click "Load Expenses" to view all expenses for the selected account.

##### Calculate Savings:

Click "Show Savings" to calculate and display savings for the selected account.

## Project Structure

```plaintext
├── ExpenseTracker.java       # Main application file
├── README.md                 # Project documentation
```

## Technologies Used

**Java**: For the application logic and GUI.
**Swing**: For building the graphical user interface.
**MySQL**: For data storage and retrieval.
**JDBC**: For connecting the Java application to the MySQL database.

## Possible Enhancements

##### Data Validation:

Improve input validation to prevent incorrect or incomplete entries.

##### Multi-Account Support:

Enable support for managing multiple accounts simultaneously.

##### Improved Security:

Secure sensitive data like passwords using hashing or encryption.

##### Export/Import Data:

Add functionality to export data to CSV or Excel.

## Troubleshooting

##### Connection Errors:

Ensure the database URL, username, and password are correct.
Confirm the MySQL server is running and accessible.

##### Table Creation Issues:

Verify database permissions if tables are not created automatically.

##### Application Crashes:

Check the console for stack traces to identify and fix issues.
