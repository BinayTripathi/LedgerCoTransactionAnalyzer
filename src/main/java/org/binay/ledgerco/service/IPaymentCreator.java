package org.binay.ledgerco.service;

public interface IPaymentCreator {

    public void createRepayments(String bank, String borrower, double lumpSumAmount, int emiNo);
}
