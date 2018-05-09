package iliker.entity;

import java.io.Serializable;

/**
 * Created by LIXINRONG on 2016/8/9.
 */
public class Reward_Item implements Serializable {
    /*
    incomeTime,incomeDESC,incomeAmount
     */
    private String incomeTime;
    private String incomeDESC;
    private String incomeAmount;

    public String getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(String incomeTime) {
        this.incomeTime = incomeTime;
    }

    public String getIncomeDESC() {
        return incomeDESC;
    }

    public void setIncomeDESC(String incomeDESC) {
        this.incomeDESC = incomeDESC;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = incomeAmount;
    }
}
