package banking;

import java.util.Random;
import java.util.Scanner;

public class BankSystem {
    private State state = State.MAIN;
    private final Scanner scanner = new Scanner(System.in);
    private final DataBase dataBase;
    private int currentId;

    public BankSystem(String fileName) {
        this.dataBase = new DataBase(fileName);
    }

    public void welcomeScreen() {
        int userInput;
        dataBase.select();
        while (true) {

            displayScreen();
            userInput = scanner.nextInt();
            if (state == State.MAIN) {
                switch (userInput) {
                    case 1:
                        createAcc();
                        break;
                    case 2:
                        logIntoAcc();
                        break;
                    case 0:
                        System.out.println("Bye!");
                        System.exit(0);
                        break;
                }
            } else if (state == State.AUTHORIZED) {
                switch (userInput) {
                    case 1:
                        displayBalance();
                        break;
                    case 2:
                        addIncome();
                        break;
                    case 3:
                        doTransfer();
                        break;
                    case 4:
                        closeAcc();
                        break;
                    case 5:
                        logOutAcc();
                        break;
                    case 0:
                        System.out.println("Bye!");
                        System.exit(0);
                        break;
                }
            }
        }
    }

    private void displayScreen() {

        System.out.println();

        if (state == State.MAIN) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
        } else if (state == State.AUTHORIZED) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
        }
    }

    private void logIntoAcc() {

        System.out.println();
        System.out.println("Enter your card number:");
        String inputCard = scanner.next();
        System.out.println("Enter your PIN:");
        String inputPIN = scanner.next();
        System.out.println();

        Card currentCard = dataBase.findByNumber(inputCard);
        if (currentCard == null || !checkPIN(currentCard.getPIN(), inputPIN)) {
            System.out.println("Wrong card number or PIN");
        } else {
            state = State.AUTHORIZED;
            currentId = currentCard.getId();
            System.out.println("You have successfully logged in!");
        }
    }

    private void displayBalance() {
        System.out.println();
        Card currentCard = dataBase.findById(currentId);
        System.out.println("Balance: " + currentCard.getBalance());
    }

    private void addIncome() {
        System.out.println();
        System.out.println("Enter income:");
        String input = scanner.next();

        if (input.matches("\\d+")) {
            Card currentCard = dataBase.findById(currentId);
            currentCard.toUpBalance(Integer.parseInt(input));
            dataBase.saveBalance(currentCard);
            System.out.println("Income was added!");
        } else if (Integer.parseInt(input) < 0 && !input.matches("\\d+")){
            System.out.println("Invalid amount");
        }
    }

    private void doTransfer() {
        Card currentCard = dataBase.findById(currentId);

        System.out.println();
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String recipientCardNumber = scanner.next();
        int digit = recipientCardNumber.length() - 1;

        if (LuhnAlgorithm(recipientCardNumber) != Integer.parseInt(recipientCardNumber.substring(digit, digit + 1))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        Card recipientCard = dataBase.findByNumber(recipientCardNumber);
        if (recipientCard == null) {
            System.out.println("Such a card does not exist.");
            return;
        }
        if (currentCard.equals(recipientCard)) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        String input = scanner.next();
        if (input.matches("\\d+")) {
            int sum = Integer.parseInt(input);
            if (currentCard.getBalance() < sum) {
                System.out.println("Not enough money!");
                return;
            }
            dataBase.transaction(currentCard, recipientCard, sum);
            System.out.println("Success!");
        }
    }

    private void closeAcc() {
        dataBase.closeCard(currentId);
        state = State.MAIN;
    }

    private void logOutAcc() {
        System.out.println();
        state = State.MAIN;
        System.out.println("You have successfully logged out!");
    }

    private boolean checkPIN(String currentPIN, String PIN) {
        boolean result = true;

        if (!currentPIN.equals(PIN)) {
            result = false;
        }
        return result;
    }

    private void createAcc() {

        Random random = new Random();
        int checkDigit;

        StringBuilder cardNumber = new StringBuilder();
        cardNumber.append("400000");
        for (int i = 0; i < 9; i++) {
            cardNumber.append(random.nextInt(10));
        }
        cardNumber.append("0");


        StringBuilder PIN = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            PIN.append(random.nextInt(10));
        }

        checkDigit = LuhnAlgorithm(cardNumber.toString());
        cardNumber.deleteCharAt(cardNumber.length() - 1).append(checkDigit);
        Card card = new Card(0, cardNumber.toString(), PIN.toString(), 0);
        dataBase.add(card);
        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(PIN.toString());
    }

    private int LuhnAlgorithm(String card) {

        int count = 1;
        int sum = 0;
        int digit;

        for (int i = 0; i < card.length(); i++) {
            int x = Integer.parseInt(String.valueOf(card.charAt(i)));

            if (i != card.length() - 1) {

                if (count % 2 != 0) {
                    x = x * 2;
                }
                if (x > 9) {
                    x -= 9;
                }
                sum += x;
            }
            count++;
        }

        digit = sum;
        while (digit % 10 != 0) {
            digit++;
        }
        return digit - sum;
    }
}
