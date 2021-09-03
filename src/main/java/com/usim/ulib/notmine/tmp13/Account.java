package com.usim.ulib.notmine.tmp13;

public class Account {
    private Person owner;
    private int money;
    private long accountId;

    public Account(Person owner, long accountId) {
        this.owner = owner;
        this.accountId = accountId;
        money = 0;
    }

    public int dailyProfit(double annuallyProfitPercentage) {
        return (int) (annuallyProfitPercentage / 100 / 12 * money / 30);
    }

    public void payMoney(int money) {
        this.money += money;
    }

    public void getMoney(int money) {
        if (this.money < money)
            System.out.println("you can not get this amount of money");
        this.money -= money;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void showInformation() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Account{" + "owner=" + owner + ", money=" + money + ", accountId=" + accountId + '}';
    }
}
