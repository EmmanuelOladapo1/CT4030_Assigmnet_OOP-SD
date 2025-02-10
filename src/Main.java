

import java.util.List;
import java.util.Scanner;

public class Main {
    private static BankingSystem bank;
    private static Scanner scanner;

    public static void main(String[] args) {
        bank = new BankingSystem();
        scanner = new Scanner(System.in);

        bank.createAccount("1234", "1234", 1000.0);
        bank.createAccount("5678", "5678", 2000.0);

        while (true) {
            if (bank.getCurrentAccount() == null) {
                loginMenu();
            } else {
                mainMenu();
            }
        }
    }

    private static void loginMenu() {
        System.out.println("\nWelcome to the Banking System");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        if (bank.login(accountNumber, pin)) {
            System.out.println("Login successful!");
            System.out.println("Current balance: \u00A3" + bank.getCurrentAccount().getBalance());
            showTransactionHistory(1);
        } else {
            System.out.println("Invalid account number or PIN");
        }
    }

    private static void mainMenu() {
        System.out.println("\n1. View Balance");
        System.out.println("2. View Transaction History");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("6. Convert Currency");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> viewBalance();
            case "2" -> transactionHistoryMenu();
            case "3" -> deposit();
            case "4" -> withdraw();
            case "5" -> transfer();
            case "6" -> convertCurrency();
            case "7" -> logout();
            default -> System.out.println("Invalid option");
        }
    }

    private static void viewBalance() {
        System.out.println("Current balance: \u00A3" + bank.getCurrentAccount().getBalance());
    }

    private static void transactionHistoryMenu() {
        System.out.println("Select period:");
        System.out.println("1. One month");
        System.out.println("2. Three months");
        System.out.println("3. Six months");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> showTransactionHistory(1);
            case "2" -> showTransactionHistory(3);
            case "3" -> showTransactionHistory(6);
            default -> System.out.println("Invalid option");
        }
    }

    private static void showTransactionHistory(int months) {
        List<Transaction> history = bank.getCurrentAccount().getTransactionHistory(months);
        System.out.println("\nTransaction History (" + months + " month(s)):");
        for (Transaction t : history) {
            System.out.printf("%s: \u00A3%.2f (%s) - Balance: \u00A3%.2f%n",
                    t.getDate(), t.getAmount(), t.getType(), t.getBalanceAfter());
        }
    }

    private static void deposit() {
        System.out.print("Enter amount to deposit: \u00A3");
        double amount = Double.parseDouble(scanner.nextLine());
        bank.getCurrentAccount().deposit(amount);
        System.out.println("Deposit successful");
    }

    private static void withdraw() {
        System.out.print("Enter amount to withdraw: \u00A3");
        double amount = Double.parseDouble(scanner.nextLine());
        if (bank.getCurrentAccount().withdraw(amount)) {
            System.out.println("Withdrawal successful");
        } else {
            System.out.println("Insufficient funds");
        }
    }

    private static void transfer() {
        System.out.print("Enter recipient account number: ");
        String recipientNumber = scanner.nextLine();
        Account recipient = bank.getAccount(recipientNumber);
        if (recipient != null) {
            System.out.print("Enter amount to transfer: \u00A3");
            double amount = Double.parseDouble(scanner.nextLine());
            if (bank.getCurrentAccount().transfer(recipient, amount)) {
                System.out.println("Transfer successful");
            } else {
                System.out.println("Transfer failed");
            }
        } else {
            System.out.println("Recipient account not found");
        }
    }

    private static void convertCurrency() {
        System.out.print("Enter amount in GBP: \u00A3");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.println("Select currency:");
        System.out.println("1. EUR");
        System.out.println("2. USD");
        System.out.println("3. AUD");
        System.out.println("4. CNY");
        System.out.println("5. CHF");

        String choice = scanner.nextLine();
        String currency = switch (choice) {
            case "1" -> "EUR";
            case "2" -> "USD";
            case "3" -> "AUD";
            case "4" -> "CNY";
            case "5" -> "CHF";
            default -> "";
        };

        if (!currency.isEmpty()) {
            double converted = bank.getCurrentAccount().convertCurrency(amount, currency);
            System.out.printf("\u00A3%.2f = %.2f %s%n", amount, converted, currency);
        }
    }

    private static void logout() {
        bank = new BankingSystem();
        System.out.println("Logged out successfully");
    }
}