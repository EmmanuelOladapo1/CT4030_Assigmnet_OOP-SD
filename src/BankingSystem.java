
import java.util.*;

public class BankingSystem {
    private final Map<String, Account> accounts;
    private Account currentAccount;

    public BankingSystem() {
        this.accounts = new HashMap<>();
    }

    public void createAccount(String accountNumber, String pin, double initialBalance) {
        accounts.put(accountNumber, new Account(accountNumber, pin, initialBalance));
    }

    public boolean login(String accountNumber, String pin) {
        Account account = accounts.get(accountNumber);
        if (account != null && account.verifyPin(pin)) {
            currentAccount = account;
            return true;
        }
        return false;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
}
