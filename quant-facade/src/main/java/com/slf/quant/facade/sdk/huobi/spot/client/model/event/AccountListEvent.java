package com.slf.quant.facade.sdk.huobi.spot.client.model.event;

import java.util.List;

import com.slf.quant.facade.sdk.huobi.spot.client.model.Account;

/**
 * The account list event information received by request of account list.
 */

public class AccountListEvent {

  private Long timestamp;

  private List<Account> accountList;

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public List<Account> getAccountList() {
    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }
}
