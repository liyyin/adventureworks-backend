package com.refresh.customer_api.enums;

public enum SalesOrderStatus {

    IN_PROCESS(1, "In Process"),
    APPROVED(2, "Approved"),
    BACKORDERED(3, "Backordered"),
    REJECTED(4, "Rejected"),
    SHIPPED(5, "Shipped"),
    CANCELLED(6, "Cancelled");

    private final int code;
    private final String label;

    SalesOrderStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    //ubah angka dari database jadi enum
    public static SalesOrderStatus fromCode(Integer code) {
        for (SalesOrderStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status Order Tidak di kenali: " + code);
    }

    //Aturan perpindahan status
    public boolean canTransitionTo(SalesOrderStatus target) {
        return switch (this) {
            case IN_PROCESS -> target == APPROVED || target == REJECTED || target == CANCELLED;
            case APPROVED -> target == BACKORDERED || target == SHIPPED || target == CANCELLED;
            case BACKORDERED -> target == SHIPPED || target == CANCELLED;
            case SHIPPED, REJECTED, CANCELLED -> false;
        };
    }

    //Item Order hanya boleh diuabh selama masihi in process
    public boolean isEditable() {
        return this == IN_PROCESS;
    }
}
