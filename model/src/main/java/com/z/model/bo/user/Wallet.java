package com.z.model.bo.user;

import com.z.model.mysql.GWallet;



public class Wallet {
    GWallet wallet;
    boolean change;

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public void addGold(long gold){
        wallet.setGold(wallet.getGold() + gold);
    }
    public void subGold(long gold){
        long last = wallet.getGold() - gold;
        last = last < 0 ? 0 : last;
        wallet.setGold(last);
    }
    public void addBankGold(long gold){
        wallet.setBankGold(wallet.getBankGold() + gold);
    }
    public void subBankGold(long gold){
        long last = wallet.getBankGold() - gold;
        last = last < 0 ? 0 : last;
        wallet.setBankGold(last);
    }
    public void addBetGold(long gold){
        long max = Math.min(Long.MAX_VALUE, wallet.getBetGold()+gold);
        wallet.setBetGold(max);
    }
    public void addWinGold(long gold){
        long max = Math.min(Long.MAX_VALUE, wallet.getWinGold()+gold);
        wallet.setWinGold(max);
    }

    public void addWins(){
        long max = Math.min(Long.MAX_VALUE, wallet.getWins()+1);
        wallet.setWins(max);
    }
    public void setWallet(GWallet wallet) {
        this.wallet = wallet;
    }

    public GWallet getWallet() {
        return wallet;
    }

    public long getId() {
        return wallet.getId();
    }
    public long getGold(){
        return wallet.getGold();
    }
    public long getBankGold(){
        return wallet.getBankGold();
    }

    public long getBetGold(){
        return wallet.getBetGold();
    }
    public long getWinGold(){
        return wallet.getWinGold();
    }
    public long getWins(){
        return wallet.getWins();
    }
    public long getBetC(){
        return wallet.getBetC();
    }
    public void addBetC(){
        long max = Math.min(Long.MAX_VALUE, wallet.getBetC()+1);
        wallet.setBetC(max);
    }

}
