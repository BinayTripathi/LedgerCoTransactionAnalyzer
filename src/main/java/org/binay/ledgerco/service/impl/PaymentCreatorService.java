package org.binay.ledgerco.service.impl;

import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.model.Repayment;
import org.binay.ledgerco.service.ILoanStore;
import org.binay.ledgerco.service.IPaymentCreator;

public class PaymentCreatorService implements IPaymentCreator {


    private final ILoanStore bankCustomerLoanStore;

    public PaymentCreatorService(ILoanStore bankCustomerLoanStore) {
        this.bankCustomerLoanStore = bankCustomerLoanStore;
    }


    public void createRepayments(String bank, String borrower, double lumpSumAmount, int emiNo) {

        Loan loan = bankCustomerLoanStore.getLoanForBankAndBorrower(bank, borrower)
                .orElseThrow(() -> new RuntimeException("Loan does not exist"));


        Repayment repayment = loan.getLastRepayment();


        if (null == repayment) {

            double balanceAfterEmiAndLumpSumPayment = loan.getTotalAmoutToPayback() - (loan.getMonthlyEmiAmount() * emiNo + lumpSumAmount);
            if (balanceAfterEmiAndLumpSumPayment < 0)
                throw new RuntimeException("Excess lumpsum amount payment attempted");
            double noOfEmisPending = balanceAfterEmiAndLumpSumPayment / loan.getMonthlyEmiAmount();
            repayment = new Repayment(emiNo, balanceAfterEmiAndLumpSumPayment, noOfEmisPending);
            loan.registerRepayment(emiNo, repayment);

        } else { // ensure repayment does not exceed the amount to repay

            double balanceSinceLastRepayment = repayment.getBalanceAfterRepayment();
            int noOfEmisSinceLastRepayment = emiNo - repayment.getEmiNoBeforeRepayment();
            double balanceAfterEmiAndLumpSumPayment = balanceSinceLastRepayment - (loan.getMonthlyEmiAmount() * noOfEmisSinceLastRepayment + lumpSumAmount);
            if (balanceAfterEmiAndLumpSumPayment < 0)
                throw new RuntimeException("Excess lumpsum amount payment attempted");
            double noOfEmisPending = balanceAfterEmiAndLumpSumPayment / loan.getMonthlyEmiAmount();
            repayment = new Repayment(emiNo, balanceAfterEmiAndLumpSumPayment, noOfEmisPending);
            loan.registerRepayment(emiNo, repayment);
        }

    }

}


