package com.refresh.customer_api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SalesOrderSummaryView {
    Integer getId();
    String getSalesOrderNumber();
    LocalDateTime getOrderDate();
    Integer getStatus();
    Integer getCustomerId();
    String getPersonName();
    String getStoreName();
    BigDecimal getTotalDue();
}
