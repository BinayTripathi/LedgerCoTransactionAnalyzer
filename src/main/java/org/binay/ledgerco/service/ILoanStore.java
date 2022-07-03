package org.binay.ledgerco.service;

import org.binay.ledgerco.model.Loan;

import java.util.Optional;

public interface ILoanStore {

    Optional<Loan> getLoanForBankAndBorrower(String bank, String borrower);

    void putLoanForBankAndBorrower(String bank, String borrower, Loan loan);
}
