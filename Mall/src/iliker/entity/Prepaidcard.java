package iliker.entity;

public class Prepaidcard {
    private String id;
    private double denomination;//面值
    private double voucher_value;//可用金额
    private String cardUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDenomination() {
        return denomination;
    }

    public void setDenomination(double denomination) {
        this.denomination = denomination;
    }

    public double getVoucher_value() {
        return voucher_value;
    }

    public void setVoucher_value(double voucher_value) {
        this.voucher_value = voucher_value;
    }

    public String getCardUrl() {
        return cardUrl;
    }

    public void setCardUrl(String cardUrl) {
        this.cardUrl = cardUrl;
    }
}
