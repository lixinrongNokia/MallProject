package iliker.entity;

import java.util.ArrayList;
import java.util.List;

public class StoreAccountInfo {
    private double remainingSum;
    private List<IncomeInfo> incomeInfos = new ArrayList<>();

    public double getRemainingSum() {
        return remainingSum;
    }

    public void setRemainingSum(double remainingSum) {
        this.remainingSum = remainingSum;
    }

    public List<IncomeInfo> getIncomeInfos() {
        return incomeInfos;
    }
}
