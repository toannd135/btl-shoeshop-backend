package vn.edu.ptit.shoe_shop.constant.enums;


public enum StepTypeEnum {
    INCREASE(1),
    DECREASE(-1);

   private final int value;

    StepTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}