package org.binay.ledgerco.service;

public interface ILoanCreator {

    public void createLoan(String bank, String borrower, double principal, double tenureInYears, double rateOfInterest);
}
