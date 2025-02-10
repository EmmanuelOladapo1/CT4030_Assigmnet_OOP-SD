

import java.time.LocalDateTime;
import java.util.*;

public class Account {
    private final String accountNumber;
    private final String pin;
    private double balance;
    private final List<Transaction> transactions;
    private int failedAttempts;
    private LocalDateTime lockoutTime;
    private int dataEntryFailures = 0;
    private LocalDateTime dataLockoutTime = null;
    private static final Map<String, Double> exchangeRates = new HashMap<>();

    static {
        // Example exchange rates (would be updated from an API in production)
        exchangeRates.put("EUR", 1.17);
        exchangeRates.put("USD", 1.27);
        exchangeRates.put("AUD", 1.89);
        exchangeRates.put("CNY", 9.18);
        exchangeRates.put("CHF", 1.12);
    }

    public Account(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.failedAttempts = 0;
        addTransaction("INITIAL_DEPOSIT", initialBalance);
    }

    public boolean verifyPin(String enteredPin) {
        if (isLockedOut()) {
            System.out.println("Account is locked. Please try again after 1 minute.");
            return false;
        }

        if (pin.equals(enteredPin)) {
            failedAttempts = 0;
            lockoutTime = null;
            return true;
        } else {
            failedAttempts++;
            if (failedAttempts >= 3) {
                lockoutTime = LocalDateTime.now();
                System.out.println("Too many failed PIN attempts. Account locked for 1 minute.");
            }
            return false;
        }
    }

    public boolean validateDataEntry() {
        if (isDataLockout()) {
            System.out.println("Data entry locked. Please try again after 1 minute.");
            return false;
        }
        return true;
    }

    public void recordDataEntryFailure() {
        dataEntryFailures++;
        if (dataEntryFailures >= 3) {
            dataLockoutTime = LocalDateTime.now();
            System.out.println("Too many invalid data entries. Function locked for 1 minute.");
        }
    }

    public void resetDataEntryFailures() {
        dataEntryFailures = 0;
        dataLockoutTime = null;
    }

    private boolean isDataLockout() {
        if (dataLockoutTime == null) return false;
        return dataLockoutTime.plusMinutes(1).isAfter(LocalDateTime.now());
    }

    private boolean isLockedOut() {
        if (lockoutTime == null) return false;
        return lockoutTime.plusMinutes(1).isAfter(LocalDateTime.now());
    }

    public List<Transaction> getTransactionHistory(int months) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(months);
        return transactions.stream()
            .filter(t -> t.getDate().isAfter(cutoffDate))
            .toList();
    }

    public void deposit(double amount) {
        if (!validateDataEntry()) {
            return;
        }
        if (amount <= 0) {
            recordDataEntryFailure();
            return;
        }
        balance += amount;
        addTransaction("DEPOSIT", amount);
        resetDataEntryFailures();
    }

    public boolean withdraw(double amount) {
        if (!validateDataEntry()) {
            return false;
        }
        if (amount <= 0) {
            recordDataEntryFailure();
            return false;
        }
        if (balance >= amount) {
            balance -= amount;
            addTransaction("WITHDRAWAL", -amount);
            resetDataEntryFailures();
            return true;
        }
        recordDataEntryFailure();
        return false;
    }

    public boolean transfer(Account recipient, double amount) {
        if (!validateDataEntry()) {
            return false;
        }
        if (amount <= 0) {
            recordDataEntryFailure();
            return false;
        }
        if (withdraw(amount)) {
            recipient.deposit(amount);
            addTransaction("TRANSFER_OUT", -amount);
            recipient.addTransaction("TRANSFER_IN", amount);
            resetDataEntryFailures();
            return true;
        }
        recordDataEntryFailure();
        return false;
    }

    public double convertCurrency(double amount, String targetCurrency) {
        if (!validateDataEntry()) {
            return 0;
        }
        if (amount <= 0) {
            recordDataEntryFailure();
            return 0;
        }
        resetDataEntryFailures();
        return amount * exchangeRates.getOrDefault(targetCurrency, 1.0);
    }

    public void calculateInterest() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getMonthValue() == 1 || now.getMonthValue() == 7) {
            double interest = balance * 0.025;
            balance += interest;
            addTransaction("INTEREST", interest);
        }
    }

    private void addTransaction(String type, double amount) {
        transactions.add(new Transaction(type, amount, balance));
    }

    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
}