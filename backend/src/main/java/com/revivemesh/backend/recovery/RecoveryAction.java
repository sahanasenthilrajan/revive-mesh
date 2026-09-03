package com.revivemesh.backend.recovery;

public enum RecoveryAction {
    RETRY_30M,
    RETRY_TOMORROW,
    PAYMENT_LINK,
    ALTERNATE_METHOD,
    CUSTOMER_CONTACT,
    DO_NOTHING
}
