package com.db.awmd.challenge.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MoneyTransfer {

    @NotNull
    private final String accountFromId;

    @NotNull
    private final String accountToId;

    @NotNull
    @Min(value = 0, message = "Money to transfer must be positive.")
    private final BigDecimal amount;

    @JsonCreator
    public MoneyTransfer(@JsonProperty("accountFromId") String accountFromId,
                         @JsonProperty("accountToId") String accountToId,
                         @JsonProperty("amount") BigDecimal amount) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

}
