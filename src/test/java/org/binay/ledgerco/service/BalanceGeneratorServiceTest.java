package org.binay.ledgerco.service;

import org.binay.ledgerco.model.Balance;
import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.model.Repayment;
import org.binay.ledgerco.service.impl.BalanceGeneratorService;
import org.binay.ledgerco.service.impl.BankCustomerLoanStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceGeneratorServiceTest {

    BalanceGeneratorService subject;


    //@Mock - NOT AVAILABLE
    ILoanStore bankCustomerLoanStore;

    int principal = 5000;
    double rate = 0.04;
    int time = 2;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        bankCustomerLoanStore = BankCustomerLoanStore.getInstance();
        subject = new BalanceGeneratorService(bankCustomerLoanStore);
    }

    @AfterEach
    public void after() throws NoSuchFieldException, IllegalAccessException {
        // cleaning the singleton instance
        Field privateStringField = BankCustomerLoanStore.class.
                getDeclaredField("bankCustomerLoanStore");
        privateStringField.setAccessible(true);
        Map<String, Map<String, Loan>> map = (Map<String, Map<String, Loan>>) privateStringField.get(bankCustomerLoanStore);
        map.clear();
    }

    @Test
    void should_throw_exception_when_fetching_balance_if_loan_does_not_exist() {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> subject.generateBalance("bank", "borrower", 5));
        assertEquals("Loan does not exist", exception.getMessage());

    }

    @Test
    void should_return_appropriate_balance_when_no_repayments_present() {

        //Given
        Loan loan = new Loan(5400, 24, 225);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);

        Balance balance = subject.generateBalance("bank", "borrower", 5);
        assertEquals("bank borrower 1125 19", balance.getBalance());

    }

    @Test
    void should_return_appropriate_balance_when_balance_requested_for_the_emi_where__repayment_done_immediately_before_emi() {

        //Given
        Loan loan = new Loan(5400, 24, 225);
        Repayment repayment = new Repayment(6, 3550, 15.7777777);
        loan.registerRepayment(6, repayment);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);

        //when
        Balance balance = subject.generateBalance("bank", "borrower", 6);

        //then
        assertEquals("bank borrower 1850 16", balance.getBalance());

    }

    @Test
    void should_return_appropriate_balance_when_balance_requested_before_repayment() {

        //Given
        Loan loan = new Loan(5400, 24, 225);
        Repayment repayment = new Repayment(6, 3550, 15.7777777);
        loan.registerRepayment(6, repayment);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);

        //when
        Balance balance = subject.generateBalance("bank", "borrower", 5);

        //then
        assertEquals("bank borrower 1125 19", balance.getBalance());

    }


    @Test
    void should_return_appropriate_balance_when_balance_requested_between_repayment() {

        //Given
        Loan loan = new Loan(5400, 24, 225);
        Repayment repayment = new Repayment(6, 3550, 15.7777777);
        loan.registerRepayment(6, repayment);

        Repayment repayment1 = new Repayment(11, 1925, 8.55555);
        loan.registerRepayment(6, repayment);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);


        //when
        Balance balance = subject.generateBalance("bank", "borrower", 8);

        //then
        assertEquals("bank borrower 2300 14", balance.getBalance());
    }

    @Test
    void should_return_appropriate_balance_when_balance_requested_after_repayment() {

        //Given
        Loan loan = new Loan(5400, 24, 225);
        Repayment repayment = new Repayment(6, 3550, 15.7777777);
        loan.registerRepayment(6, repayment);

        Repayment repayment1 = new Repayment(11, 1925, 8.55555);
        loan.registerRepayment(11, repayment1);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);

        //when
        Balance balance = subject.generateBalance("bank", "borrower", 12);

        //then
        assertEquals("bank borrower 3700 8", balance.getBalance());
    }
}
