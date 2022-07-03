package org.binay.ledgerco;

import org.binay.ledgerco.model.Balance;
import org.binay.ledgerco.service.IBalanceGenerator;
import org.binay.ledgerco.service.ILoanCreator;
import org.binay.ledgerco.service.ILoanStore;
import org.binay.ledgerco.service.IPaymentCreator;
import org.binay.ledgerco.service.impl.BalanceGeneratorService;
import org.binay.ledgerco.service.impl.BankCustomerLoanStore;
import org.binay.ledgerco.service.impl.LoanCreatorService;
import org.binay.ledgerco.service.impl.PaymentCreatorService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Geektrust {

    //Skipping auto - dependency injection for the sake of simplicity - instead performing manual constructor injection from the client
    //No mockito provided in POM.xml. Hence adding real instance of BankCustomerLoanStore in test instead of Mocks
    private static final ILoanStore bankCustomerLoanStore = BankCustomerLoanStore.getInstance();
    private static final ILoanCreator loanCreator = new LoanCreatorService(bankCustomerLoanStore);
    private static final IPaymentCreator paymentCreator = new PaymentCreatorService(bankCustomerLoanStore);
    private static final IBalanceGenerator balanceGenerator = new BalanceGeneratorService(bankCustomerLoanStore);

    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream(args[0]);
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                executeCommand(sc.nextLine());
            }
            sc.close();
        } catch (IOException e) {
        }
    }

    private static void executeCommand(String commandInput) {

        String[] inputEntry = commandInput.split(" ");

        switch (inputEntry[0]) {
            case "LOAN":
                loanCreator.createLoan(inputEntry[1], inputEntry[2], Double.parseDouble(inputEntry[3])
                        , Double.parseDouble(inputEntry[4]), Double.parseDouble(inputEntry[5]) / 100);
                break;
            case "PAYMENT":
                paymentCreator.createRepayments(inputEntry[1], inputEntry[2], Double.parseDouble(inputEntry[3]),
                        Integer.parseInt(inputEntry[4]));
                break;
            case "BALANCE":
                Balance balance = balanceGenerator.generateBalance(inputEntry[1], inputEntry[2], Integer.parseInt(inputEntry[3]));
                System.out.println(balance.getBalance());
                break;
            default:
                System.out.println("Wrong command");
        }

    }
}
