package org.binay.ledgerco.service;

import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.model.Repayment;
import org.binay.ledgerco.service.impl.BankCustomerLoanStore;
import org.binay.ledgerco.service.impl.PaymentCreatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentCreatorServiceTest {

    PaymentCreatorService subject;


    //@Mock - NOT AVAILABLE
    ILoanStore bankCustomerLoanStore;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        bankCustomerLoanStore = BankCustomerLoanStore.getInstance();
        subject = new PaymentCreatorService(bankCustomerLoanStore);
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
    void should_throw_exception_when_registering_repayment_when_loan_does_not_already_exists() throws IllegalAccessException {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> subject.createRepayments("bank", "borrower", 1000, 5));
        assertEquals("Loan does not exist", exception.getMessage());
    }

    @Test
    void should_register_repayment_when_first_repayment_is_made() {

        //Given
        Loan loan = new Loan(5400, 24, 225);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);
        assertNull(loan.getLastRepayment());

        //When
        subject.createRepayments("bank", "borrower", 500, 5);

        //Then
        Repayment repayment = loan.getLastRepayment();
        assertNotNull(repayment);
        assertEquals(5, repayment.getEmiNoBeforeRepayment());
        assertEquals(3775, repayment.getBalanceAfterRepayment());
        assertEquals(17, (int) Math.ceil(repayment.getEmiCountRemaining()));

    }

    @Test
    void should_throw_exception_when_first_repayment_is_attempted_more_than_balance() {
        //Given
        Loan loan = new Loan(5400, 24, 225);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);
        assertNull(loan.getLastRepayment());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> subject.createRepayments("bank", "borrower", 5000, 10));


    }

    @Test
    void should_register_new_repayment_when_second_repayment_is_made() {
        //Given
        Loan loan = new Loan(5400, 24, 225);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);
        assertNull(loan.getLastRepayment());

        subject.createRepayments("bank", "borrower", 500, 5);

        Repayment repayment = loan.getLastRepayment();
        assertNotNull(repayment);
        assertEquals(5, repayment.getEmiNoBeforeRepayment());
        assertEquals(3775, repayment.getBalanceAfterRepayment());
        assertEquals(17, (int) Math.ceil(repayment.getEmiCountRemaining()));


        subject.createRepayments("bank", "borrower", 1000, 10);
        repayment = loan.getLastRepayment();
        assertNotNull(repayment);
        assertEquals(10, repayment.getEmiNoBeforeRepayment());
        assertEquals(1650, repayment.getBalanceAfterRepayment());
        assertEquals(8, (int) Math.ceil(repayment.getEmiCountRemaining()));
    }

    @Test
    void should_throw_exception_when_second_repayment_higer_than_amout_due_is_attempted() {
        //Given
        Loan loan = new Loan(5400, 24, 225);
        bankCustomerLoanStore.putLoanForBankAndBorrower("bank", "borrower", loan);
        assertNull(loan.getLastRepayment());

        //when
        subject.createRepayments("bank", "borrower", 500, 5);
        Repayment repayment = loan.getLastRepayment();

        //when, then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> subject.createRepayments("bank", "borrower", 5000, 10));

    }
}
