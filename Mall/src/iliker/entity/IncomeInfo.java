package iliker.entity;

public class IncomeInfo {
    private Integer incomeID;
    private String incomeTime;
    private String incomeDESC;
    private String incomeAmount;

    public Integer getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(Integer incomeID) {
        this.incomeID = incomeID;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public String getIncomeDESC() {
        return incomeDESC;
    }

    public void setIncomeDESC(String incomeDESC) {
        this.incomeDESC = incomeDESC;
    }

    public String getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(String incomeTime) {
        this.incomeTime = incomeTime;
    }
}
