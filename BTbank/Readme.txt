BT Bank System - Project Documentation
Version: 5.0

Author: Shashi-Shekhar-Rai

1. Introduction
1.1. Project Purpose
The BT Bank System is a comprehensive desktop application developed in Java Swing that simulates a real-world banking environment. It provides a secure, user-friendly graphical interface for both customers and bank accountants to perform core banking operations. The project is designed to showcase robust software engineering principles, including a multi-tiered architecture, secure data handling with password hashing, transactional integrity for financial operations, and a modern, enhanced user experience.

1.2. Scope & Features
The application is built around two primary user roles, with a strong emphasis on robust error handling, data validation, and seamless component integration.

Customer Features
Secure Authentication and Management:

Core Feature: Customers log in using their unique account number and password.
Code Quality: Passwords are never stored directly; they are salted and hashed using the industry-standard jBCrypt algorithm for maximum security.
Data Validation: The system validates that both fields are non-empty before attempting to log in.
Comprehensive Account Creation:

Core Feature: A user-friendly, multi-step process allows users to open a new Savings or Current account.
Data Validation:
Ensures all required fields (name, address, etc.) are filled.
The initial deposit must be a valid positive number and meet the ₹1500 minimum balance requirement.
The user's age is validated to be a positive integer.
Passwords must be at least 6 characters long and confirmed correctly.
Innovative Feature: Users can upload an optional profile picture, which is stored securely in the database as a BLOB.
Personalized User Dashboard:

Core Feature: After login, a central dashboard displays the user's name, profile picture, account number, and real-time balance.
Event Handling: The dashboard data automatically refreshes every time it is shown, ensuring the balance is always up-to-date.
Robust Financial Transactions:

Core Feature: Users can deposit, withdraw, and transfer funds.
Data Validation: All transaction amount inputs are validated to ensure they are positive numerical values.
Error Handling & Business Rules:
Withdrawals are governed by strict rules: Savings accounts must maintain a minimum balance of ₹1000, while Current accounts have a generous overdraft limit of ₹5000. The system provides clear error messages via an InsufficientFundsException if these rules are violated.
Fund Transfers are protected by transactional integrity. The system uses commit and rollback logic to ensure that money is never lost—if the transfer fails at any step, the entire operation is cancelled, and the funds are safely returned to the sender's account.
Advanced Security & Recovery:

Core Feature: Users can change their password and recover their account details.
Data Validation: Password changes require correct entry of the old password and confirmation of the new one.
Innovative Feature: A secure Account & Password Recovery mechanism allows users to retrieve their account number or reset their password by validating their Government ID and contact number against the database records.
Reporting and Support:

Innovative Feature: Users can generate and export a detailed PDF Statement of their complete transaction history. The professional-grade statement includes the bank's logo, customer details, profile picture, and a clearly formatted table of transactions.
Core Feature: A complaint registration system allows users to submit feedback categorized as "Scam" or "Other."
Accountant Features
Secure Administrative Access:

Core Feature: Provides a separate, password-protected login for the accountant role to perform administrative tasks.
Comprehensive Customer Oversight:

Core Feature: The accountant can view a dashboard of all customer accounts in a single, sortable table.
Innovative Feature: This view includes a live search filter, allowing the accountant to instantly find any customer by typing in their account number.
Event Handling: A double-click event on any row in the table opens a detailed pop-up view of that specific account.
Complaint Management System:

Core Feature: The accountant can review all user-submitted complaints, which are neatly separated by category for efficient processing. The view can be refreshed to see new complaints in real-time.
System, Architectural & Quality Features
Multi-Tiered Architecture & Integration:

The project is built on a classic 3-tiered model, ensuring excellent integration of components:
Presentation Tier (GUI): The BankingSystemGUI and Swing components.
Business Logic Tier: The Account models and Validator class.
Data Access Tier: The AccountDAO interface and its AccountDAOImpl implementation.
This separation makes the code clean, scalable, and easy to maintain.
Responsive UI with Concurrent Event Handling:

The application uses SwingWorker to perform all long-running tasks (like database queries, fund transfers, and PDF generation) on a background thread.
This ensures the User Interface (UI) never freezes, providing a smooth and responsive user experience, which is a hallmark of high-quality GUI applications.
Robust Error Handling:

The application is designed to handle errors gracefully. Database exceptions, file I/O errors, and invalid user inputs are all caught, and clear, user-friendly error dialogs are shown to the user.
A comprehensive banking_system.log file records all major operations and errors for easy debugging and auditing.
Innovative UI/UX Features:

Real-time Theme Switching: Users can select from multiple "Look and Feel" themes (Metal, Nimbus, System Default) from a menu bar, and the UI updates instantly.
Interactive Design: Buttons and other components have hover effects and custom styling, creating a modern and engaging user experience.
A custom BackgroundPanel provides a consistent and visually appealing brand identity throughout the application.
2. Prerequisites
To compile and run this project, you need the following software and libraries installed and configured:

Java Development Kit (JDK): Version 8 or higher.
MySQL Server: An active and running instance of MySQL.
External Java Libraries (.jar files):
MySQL Connector/J (JDBC Driver)
Apache PDFBox
Apache FontBox (comes with PDFBox)
Apache Commons Logging (a dependency for PDFBox)
jBCrypt (for password hashing)
3. Project and Library Setup
The project follows a flat directory structure. You must download the required .jar files and place them in the same root folder as all your .java source files (e.g., in the BTbank folder).

MySQL Connector/J: Download from the MySQL Community Downloads page.
Apache PDFBox, FontBox, and Commons Logging:
Go to the Apache PDFBox Downloads page.
Download the main application JAR (e.g., pdfbox-app-X.X.X.jar), which usually contains all dependencies. If not, download the source distribution and find the required JARs in its lib/ folder.
jBCrypt:
Go to the Maven Central Repository for jBCrypt.
Download jbcrypt-0.4.jar.
Your project directory should contain all .java files (e.g., BankingSystemGUI.java, Account.java, etc.) and these library .jar files together.

4. Database Setup
Run the following SQL script in your MySQL client (e.g., MySQL Workbench) to create the database and all necessary tables with the correct structure.

SQL

-- Step 1: Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS bank_db;

-- Step 2: Select the database to use
USE bank_db;

-- Step 3: Create the 'accounts' table with all necessary columns
-- This table stores all customer account information, including hashed passwords and photos.
CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(255) PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    balance DOUBLE NOT NULL,
    address TEXT,
    contact_number VARCHAR(20),
    govt_id VARCHAR(50),
    age INT,
    account_type VARCHAR(20) NOT NULL,
    profile_picture MEDIUMBLOB DEFAULT NULL
);

-- Step 4: Create the 'transactions' table to log all financial activities
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(255),
    transaction_type VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- Step 5: Create the 'complaints' table to store user complaints
CREATE TABLE IF NOT EXISTS complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    details TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
5. Application Configuration and Execution
5.1. Update Database Credentials
IMPORTANT: Before compiling, open the DatabaseManager.java file. You must change the DB_URL, DB_USER, and DB_PASSWORD constants to match your local MySQL server setup.

Java

// In DatabaseManager.java
class DatabaseManager {
    //
    // UPDATE THESE VALUES
    //
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "YOUR_MYSQL_USERNAME";
    private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD";

    public static Connection getConnection() throws SQLException {
        // ...
    }
}
5.2. Compile and Run
Open a terminal or command prompt in your project directory (e.g., the BTbank folder). The project now consists of multiple source files, so you must compile all of them together using the *.java wildcard.

Important: Replace the JAR filenames in the commands below with the exact versions you downloaded. The versions below are based on the last command you provided.

To Compile:

On Windows (using ; as a separator):

Shell

javac -cp ".;mysql-connector-j-9.3.0.jar;pdfbox-2.0.34.jar;fontbox-2.0.34.jar;commons-logging-1.3.5.jar;jbcrypt-0.4.jar" *.java
On macOS/Linux (using : as a separator):

Shell

javac -cp ".:mysql-connector-j-9.3.0.jar:pdfbox-2.0.34.jar:fontbox-2.0.34.jar:commons-logging-1.3.5.jar:jbcrypt-0.4.jar" *.java
To Run: (After successful compilation)

On Windows:

Shell

java -cp ".;mysql-connector-j-9.3.0.jar;pdfbox-2.0.34.jar;fontbox-2.0.34.jar;commons-logging-1.3.5.jar;jbcrypt-0.4.jar" BankingSystemGUI
On macOS/Linux:

Shell

java -cp ".:mysql-connector-j-9.3.0.jar:pdfbox-2.0.34.jar:fontbox-2.0.34.jar:commons-lo