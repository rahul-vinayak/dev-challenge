package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.web.MoneyTransfer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;

    private final NotificationService notificationService;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    public void transferMoney(MoneyTransfer moneyTransfer) {
        BigDecimal amount = moneyTransfer.getAmount();
        Account accountFrom = getAccount(moneyTransfer.getAccountFromId());
        Account accountTo = getAccount(moneyTransfer.getAccountToId());

        if (accountFrom != null && accountTo != null) {
            accountsRepository.transferMoney(accountFrom, accountTo, amount);
            notificationService.notifyAboutTransfer(accountFrom, "An amount of " + amount + " transferred to Account " + accountTo.getAccountId());
            notificationService.notifyAboutTransfer(accountTo, "An amount of " + amount + " transferred from Account " + accountFrom.getAccountId());
        }
    }
}
