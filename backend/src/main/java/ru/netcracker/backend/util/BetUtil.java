package ru.netcracker.backend.util;

import ru.netcracker.backend.exception.bet.BankLessThanMinException;
import ru.netcracker.backend.exception.bet.BankLessThanOldException;
import ru.netcracker.backend.exception.bet.BankLessThanStepException;
import ru.netcracker.backend.model.Bet;

public class BetUtil {
    public static void validate(Bet oldBet, Bet newBet)
            throws BankLessThanMinException, BankLessThanOldException, BankLessThanStepException {
        if (newBet.getCurrentBank() < oldBet.getLot().getMinBank()) {
            throw new BankLessThanMinException(
                    "Bank is less than minimum: " + oldBet.getLot().getMinBank());
        }

        if (newBet.getCurrentBank() < oldBet.getCurrentBank()) {
            throw new BankLessThanOldException(
                    "Bank is less than the old one: " + oldBet.getCurrentBank());
        }

        if ((newBet.getCurrentBank() - oldBet.getCurrentBank()) < oldBet.getLot().getStep()) {
            throw new BankLessThanStepException(
                    "Bet step is less than the minimal one: " + oldBet.getLot().getStep());
        }
    }
}
