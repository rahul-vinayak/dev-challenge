package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * These tests have been added to proove that the money transfer operation is thread safe.
 * There are two tests, one calls the thread safe money transfer method and the other one calls the money transfer method which is not thread safe.
 * Both the tests start with two accounts account-1 having 10000 balance and account-2 having 0 balance. Then 1 unit transfer is done from account-1 to account-2 in 10000 threads.
 * After the execution of threads the thread-safe method has account-1 has 0 balance and the account-2 has 10000 balance.
 * Whereas in non thread-safe method testing the remaining balances are always unpredictable and wrong because of the race-condition and the test will keep failing for wrong balances.
 *
 * I have used the method which is the thread safe one and this proves the point.
 */
@Slf4j
public class AccountsRepositoryInMemoryTest {

    private AccountsRepositoryInMemory accountsRepositoryInMemory;
    private Account accountFrom = new Account("Account-1", new BigDecimal(10000));
    private Account accountTo = new Account("Account-2", new BigDecimal(0));

    @Before
    public void before() {
        accountsRepositoryInMemory = new AccountsRepositoryInMemory();
        accountsRepositoryInMemory.createAccount(accountFrom);
        accountsRepositoryInMemory.createAccount(accountTo);
    }

    @Test
    // always passes
    public void shouldTransferMoneyThreadSafeMode() throws Exception {
        log.info("account from: " + accountFrom.getAccountId() + "=" + accountFrom.getBalance());
        log.info("account to: " + accountTo.getAccountId() + "=" + accountTo.getBalance());

        for (int i=0; i<10000; i++) {
            Runnable task3 = () -> accountsRepositoryInMemory.transferMoney(accountFrom, accountTo, BigDecimal.valueOf(1));
            new Thread(task3).start();
        }
        Thread.sleep(100);
        log.info("account from: " + accountFrom.getAccountId() + "=" + accountFrom.getBalance());
        log.info("account to: " + accountTo.getAccountId() + "=" + accountTo.getBalance());

        assertEquals("balance in account-1(from) should be 0", BigDecimal.valueOf(0), accountFrom.getBalance());
        assertEquals("balance in account-2(to) should be 10000", BigDecimal.valueOf(10000), accountTo.getBalance());
    }

    @Test
    // mostly fails
    public void transfersMoneyInNonThreadSafe() throws Exception {
        log.info("account from: " + accountFrom.getAccountId() + "=" + accountFrom.getBalance());
        log.info("account to: " + accountTo.getAccountId() + "=" + accountTo.getBalance());

        for (int i=0; i<10000; i++) {
            Runnable task3 = () -> accountsRepositoryInMemory.transferMoneyNonThreadSafe(accountFrom, accountTo, BigDecimal.valueOf(1));
            new Thread(task3).start();
        }
        Thread.sleep(100);
        log.info("account from: " + accountFrom.getAccountId() + "=" + accountFrom.getBalance());
        log.info("account to: " + accountTo.getAccountId() + "=" + accountTo.getBalance());

        assertEquals("balance in account-1(from) should be 0", BigDecimal.valueOf(0), accountFrom.getBalance());
        assertEquals("balance in account-2(to) should be 10000", BigDecimal.valueOf(10000), accountTo.getBalance());
    }

}
