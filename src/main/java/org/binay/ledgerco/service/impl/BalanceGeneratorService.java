package org.binay.ledgerco.service.impl;

import org.binay.ledgerco.model.Balance;
import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.model.Repayment;
import org.binay.ledgerco.service.IBalanceGenerator;
import org.binay.ledgerco.service.ILoanStore;

import java.util.Collections;
import java.util.List;

public class BalanceGeneratorService implements IBalanceGenerator {


    private final ILoanStore bankCustomerLoanStore;

    public BalanceGeneratorService(ILoanStore bankCustomerLoanStore) {
        this.bankCustomerLoanStore = bankCustomerLoanStore;
    }


    public Balance generateBalance(String bank, String borrower, int emiNo) {

        Loan loan = bankCustomerLoanStore.getLoanForBankAndBorrower(bank, borrower)
                .orElseThrow(() -> new RuntimeException("Loan does not exist"));

        List<Integer> allRepayments = loan.getAllRepaymentEmiNos();

        if (null == allRepayments) {  // No repayments done

            return getBalanceWhenNoRepayments(bank, borrower, emiNo, loan);

        } else if (null != loan.getRepaymentByEmiNo(emiNo)) {  // requested emi matches with a repayment

            Repayment repayment = loan.getRepaymentByEmiNo(emiNo);
            double amountPaid = loan.getTotalAmoutToPayback() - repayment.getBalanceAfterRepayment();
            return new Balance(bank, borrower, amountPaid, (int) Math.ceil(repayment.getEmiCountRemaining()));
        } else {

            int insertionIndex = (-1 * (Collections.binarySearch(allRepayments, emiNo) + 1)) - 1;

            if (insertionIndex == -1) {  // insertion index is begining of the array that is no repayments yet
                return getBalanceWhenNoRepayments(bank, borrower, emiNo, loan);
            } else {  // Asked emi somewhere in between repayments
                Repayment lastRepaymentBeforeAskedEmi = loan.getRepaymentByEmiNo(allRepayments.get(insertionIndex));
                return getBalanceAfterLastRepaymentsAndAskedEmiNumber(bank, borrower, emiNo, loan, lastRepaymentBeforeAskedEmi);
            }
        }

    }

    private Balance getBalanceAfterLastRepaymentsAndAskedEmiNumber(String bank, String borrower, int emiNo, Loan loan, Repayment lastRepaymentBeforeAskedEmi) {
        double amountPaid = (loan.getTotalAmoutToPayback() - lastRepaymentBeforeAskedEmi.getBalanceAfterRepayment()) + loan.getMonthlyEmiAmount() * (emiNo - lastRepaymentBeforeAskedEmi.getEmiNoBeforeRepayment());
        double balanceAmt = loan.getTotalAmoutToPayback() - amountPaid;

        int noOfRemainEmis = (int) Math.ceil((loan.getTotalAmoutToPayback() - amountPaid) / loan.getMonthlyEmiAmount());
        return new Balance(bank, borrower, amountPaid, noOfRemainEmis);
    }

    private Balance getBalanceWhenNoRepayments(String bank, String borrower, int emiNo, Loan loan) {
        double amountPaid = loan.getMonthlyEmiAmount() * emiNo;
        double balanceAmt = loan.getTotalAmoutToPayback() - amountPaid;
        int noOfRemainEmis = (int) Math.ceil((loan.getTotalAmoutToPayback() - amountPaid) / loan.getMonthlyEmiAmount());
        return new Balance(bank, borrower, amountPaid, noOfRemainEmis);

    }

}


