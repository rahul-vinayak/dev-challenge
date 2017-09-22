package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.web.MoneyTransfer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

    @MockBean
    private NotificationService mockNotificationService;

    @Autowired
    private AccountsService accountsService;

    @Before
    public void before() {
        accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void addAccount() throws Exception {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    public void addAccount_failsOnDuplicateId() throws Exception {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }

    }

    @Test
    public void transferAmountSuccess() {
        Account accountFrom = new Account("from-id");
        accountFrom.setBalance(new BigDecimal(200));
        accountsService.createAccount(accountFrom);

        Account accountTo = new Account("to-id");
        accountTo.setBalance(new BigDecimal(200));
        accountsService.createAccount(accountTo);

        MoneyTransfer moneyTransfer = new MoneyTransfer("from-id", "to-id", new BigDecimal(100));
        accountsService.transferMoney(moneyTransfer);

        assertEquals(accountsService.getAccount("to-id").getBalance(), new BigDecimal("300"));
        verify(mockNotificationService).notifyAboutTransfer(accountFrom, "An amount of 100 transferred to Account to-id");
        verify(mockNotificationService).notifyAboutTransfer(accountTo, "An amount of 100 transferred from Account from-id");
    }

    @Test
    public void transferAmountFailsForInsufficientFunds() {
        Account account = new Account("from-id");
        account.setBalance(new BigDecimal(200));
        accountsService.createAccount(account);

        Account account_2 = new Account("to-id");
        account_2.setBalance(new BigDecimal(200));
        accountsService.createAccount(account_2);

        MoneyTransfer moneyTransfer = new MoneyTransfer("from-id", "to-id", new BigDecimal(300));
        try {
            accountsService.transferMoney(moneyTransfer);
            fail("should throw insufficient funds exception");
        } catch (Exception e) {
            assertEquals(InsufficientFundsException.class, e.getClass());
            assertEquals("Account id from-id does not have sufficient funds to do this transfer", e.getMessage());
        }
        verifyZeroInteractions(mockNotificationService);
    }
}
