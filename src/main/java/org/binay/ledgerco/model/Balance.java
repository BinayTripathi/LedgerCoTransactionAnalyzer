package org.binay.ledgerco.model;

public class Balance {

    private final String bank;
    private final String borrower;
    private final double amountPaid;
    private final int noOfEmisLeft;

    public Balance(String bank, String borrower, double amountPaid, int noOfEmisLeft) {
        this.bank = bank;
        this.borrower = borrower;
        this.amountPaid = amountPaid;
        this.noOfEmisLeft = noOfEmisLeft;
    }

    public String getBalance() {
        return String.format("%s %s %d %d", bank, borrower, (int) amountPaid, noOfEmisLeft);
    }
}
