package org.binay.ledgerco.service.impl;

import org.binay.ledgerco.model.Loan;
import org.binay.ledgerco.service.ILoanStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BankCustomerLoanStore implements ILoanStore {


    private static final BankCustomerLoanStore singletonSinstance = new BankCustomerLoanStore();
    //Outer map's key is Bank
    //Inner map's key is customer and value is Loan
    private Map<String, Map<String, Loan>> bankCustomerLoanStore;

    private BankCustomerLoanStore() {
        bankCustomerLoanStore = new HashMap<>();
    }

    public static BankCustomerLoanStore getInstance() {
        return singletonSinstance;
    }

    public Optional<Loan> getLoanForBankAndBorrower(String bank, String borrower) {

        Map<String, Loan> customerLoanStore = bankCustomerLoanStore.getOrDefault(bank, new HashMap<String, Loan>());
        return Optional.ofNullable(customerLoanStore.getOrDefault(borrower, null));

    }

    public void putLoanForBankAndBorrower(String bank, String borrower, Loan loan) {

        Map<String, Loan> customerLoanStore = bankCustomerLoanStore.getOrDefault(bank, new HashMap<String, Loan>());
        if (customerLoanStore.size() == 0) bankCustomerLoanStore.put(bank, customerLoanStore);
        customerLoanStore.put(borrower, loan);
    }
}
