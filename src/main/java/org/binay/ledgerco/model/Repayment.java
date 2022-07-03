package org.binay.ledgerco.model;

public class Repayment {

    private final int emiNoBeforeRepayment;
    private final double balanceAfterRepayment;
    private final double emiCountRemaining;

    public Repayment(int emiNoBeforeRepayment, double balanceAfterRepayment, double emiCountRemaining) {
        this.emiNoBeforeRepayment = emiNoBeforeRepayment;
        this.balanceAfterRepayment = balanceAfterRepayment;
        this.emiCountRemaining = emiCountRemaining;
    }

    public int getEmiNoBeforeRepayment() {
        return emiNoBeforeRepayment;
    }

    public double getBalanceAfterRepayment() {
        return balanceAfterRepayment;
    }

    public double getEmiCountRemaining() {
        return emiCountRemaining;
    }

}
