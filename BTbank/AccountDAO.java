
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

interface AccountDAO {
    void save(Account account) throws SQLException;
    Account findAndAuthenticate(String accNum, String password) throws SQLException;
    Account findAccountByNumber(String accNum);
    boolean accountExists(String accNum);
    void updateBalance(Account account) throws SQLException;
    void updatePassword(String accNum, String newHashedPassword) throws SQLException;
    void recordTransaction(String accountNumber, String type, double amount) throws SQLException;
    void transferFunds(Account from, Account to, double amount) throws SQLException, InsufficientFundsException;
    List<Account> findAllAsList() throws SQLException;
    List<Transaction> getTransactionHistory(String accountNumber) throws SQLException;
    void saveComplaint(String type, String details) throws SQLException;
    Map<String, List<String>> getAllComplaints() throws SQLException;
    String findAccountByGovtIdAndContact(String govtId, String contact);
}