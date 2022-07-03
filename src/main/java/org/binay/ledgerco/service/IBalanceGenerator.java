package org.binay.ledgerco.service;

import org.binay.ledgerco.model.Balance;

public interface IBalanceGenerator {

    public Balance generateBalance(String bank, String borrower, int emiNo);
}
