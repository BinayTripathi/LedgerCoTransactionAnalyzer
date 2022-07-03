package org.binay.ledgerco.service;

import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.service.impl.BankCustomerLoanStore;
import org.binay.ledgerco.service.impl.LoanCreatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LoanCreatorServiceTest {

    LoanCreatorService subject;


    //@Mock - NOT AVAILABLE
    ILoanStore bankCustomerLoanStore;


    int principal = 5000;
    double rate = 0.04;
    int time = 2;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        bankCustomerLoanStore = BankCustomerLoanStore.getInstance();
        subject = new LoanCreatorService(bankCustomerLoanStore);
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
    void should_throw_exception_when_loan_already_created() throws IllegalAccessException {

        Loan loan = new Loan(1, 2, 3);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> subject.createLoan("bank", "borrower", 5000, 1, 2));
        assertEquals("Loan already exists", exception.getMessage());

    }

    @Test
    void should_create_new_loan_in_loan_store() throws IllegalAccessException {

        double totalLoan = principal + (principal * rate * time);
        int monthlyEmi = (int) Math.ceil(totalLoan / (time * 12));
        int numOfEmi = 24;

        assertEquals(Optional.empty(), bankCustomerLoanStore.getLoanForBankAndBorrower("bank", "borrower"));
        subject.createLoan("bank", "borrower", principal, time, rate);
        Loan loan = bankCustomerLoanStore.getLoanForBankAndBorrower("bank", "borrower").get();
        assertNotNull(loan);


        assertEquals(totalLoan, loan.getTotalAmoutToPayback());
        assertEquals(numOfEmi, loan.getInitialEmiCount());
        assertEquals(monthlyEmi, loan.getMonthlyEmiAmount());

    }

}
