package org.binay.ledgerco.service.impl;

import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.service.ILoanCreator;
import org.binay.ledgerco.service.ILoanStore;

public class LoanCreatorService implements ILoanCreator {


    private final ILoanStore bankCustomerLoanStore;

    public LoanCreatorService(ILoanStore bankCustomerLoanStore) {
        this.bankCustomerLoanStore = bankCustomerLoanStore;
    }


    //Create new loan
    public void createLoan(String bank, String borrower, double principal, double tenureInYears,
                           double rateOfInterest) {

        if (bankCustomerLoanStore.getLoanForBankAndBorrower(bank, borrower).isPresent()) {
            throw new RuntimeException("Loan already exists");
        }

        double totalAmountToPayBack = principal + (principal * tenureInYears * rateOfInterest);
        int monthlyEmiAmount = (int) Math.ceil(totalAmountToPayBack / (tenureInYears * 12));
        double initialEmiCount = totalAmountToPayBack / monthlyEmiAmount;

        Loan loan = new Loan(totalAmountToPayBack, initialEmiCount, monthlyEmiAmount);
        bankCustomerLoanStore.putLoanForBankAndBorrower(bank, borrower, loan);

    }
}


