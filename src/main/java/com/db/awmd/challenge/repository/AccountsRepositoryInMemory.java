package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.db.awmd.challenge.exception.InsufficientFundsException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public void transferMoney(Account accountFrom, Account accountTo, BigDecimal amount) {
        if (accountFrom.getBalance().compareTo(amount) == -1) {
            throw new InsufficientFundsException(
                    "Account id " + accountFrom.getAccountId() + " does not have sufficient funds to do this transfer");
        }
        settleMoney(accountFrom, accountTo, amount);
    }

    private void settleMoney(Account accountFrom, Account accountTo, BigDecimal amount) {
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));
        accounts.put(accountFrom.getAccountId(), accountFrom);
        accounts.put(accountTo.getAccountId(), accountTo);
    }
}
