

import java.sql.SQLException;

class SavingsAccount extends Account {
    SavingsAccount(String customerName, String accountNumber, double balance, String address, String contactNumber, String govtID, int age, byte[] profilePicture, String passwordHash) {
        super(customerName, accountNumber, balance, address, contactNumber, govtID, age, "Savings", profilePicture, passwordHash);
    }

    @Override
    void withdraw(double amount) throws InsufficientFundsException, SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (balance - amount < 1000) {
            throw new InsufficientFundsException("Withdrawal failed. Minimum balance of 1000 must be maintained.");
        }
        balance -= amount;
        accountDAO.updateBalance(this);
        accountDAO.recordTransaction(this.accountNumber, "WITHDRAWAL", amount);
    }
}