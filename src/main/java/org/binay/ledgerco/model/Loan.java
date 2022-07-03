package org.binay.ledgerco.model;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Loan {


    private final double totalAmoutToPayback;
    private final double initialEmiCount;
    private final int monthlyEmiAmount;

    //Key is emi no and value is the repayment
    private final TreeMap<Integer, Repayment> repayments;


    public Loan(double totalAmoutToPayback, double initialEmiCount, int monthlyEmiAmt) {
        this.totalAmoutToPayback = totalAmoutToPayback;
        this.initialEmiCount = initialEmiCount;
        this.monthlyEmiAmount = monthlyEmiAmt;
        this.repayments = new TreeMap<>();
    }

    public double getTotalAmoutToPayback() {
        return totalAmoutToPayback;
    }

    public double getInitialEmiCount() {
        return initialEmiCount;
    }

    public double getMonthlyEmiAmount() {
        return monthlyEmiAmount;
    }

    public Repayment getRepaymentByEmiNo(int emiNo) {
        if (repayments.isEmpty() || !repayments.containsKey(emiNo))
            return null;

        return repayments.get(emiNo);
    }

    public Repayment getLastRepayment() {
        if (repayments.isEmpty())
            return null;

        return repayments.lastEntry().getValue();
    }


    public void registerRepayment(int emiNo, Repayment repayment) {
        repayments.put(emiNo, repayment);
    }

    public List<Integer> getAllRepaymentEmiNos() {

        if (0 == repayments.size()) return null;

        return repayments.keySet().stream().collect(Collectors.toList());


    }

}
