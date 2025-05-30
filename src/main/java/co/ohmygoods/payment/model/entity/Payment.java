package co.ohmygoods.payment.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private PaymentAPIProvider paymentAPIProvider;

    @Column(nullable = false, updatable = false)
    private int paymentAmount;

    private String transactionId;

    private LocalDateTime transactionEndedAt;

    private LocalDateTime transactionReadyAt;

    public static Payment start(Account account, Order order, PaymentAPIProvider vendor, int paymentAmount) {
        if (paymentAmount < 0) {
            throw PaymentException.INVALID_PURCHASE_AMOUNT;
        }

        return new Payment(0L, account, order, PaymentStatus.PAYMENT_START, vendor,
                paymentAmount, null, null, null);
    }

    public void ready(String transactionId, LocalDateTime transactionReadyAt) {
        this.transactionId = transactionId;
        this.transactionReadyAt = transactionReadyAt;
        status = PaymentStatus.PAYMENT_READY;
    }

    public void cancel(LocalDateTime transactionEndedAt) {
        this.transactionEndedAt = transactionEndedAt;
        status = PaymentStatus.PAYMENT_CANCEL;
    }

    public void fail(PaymentStatus cause, LocalDateTime transactionEndedAt) {
        this.transactionEndedAt = transactionEndedAt;
        status = cause;
    }

    public void succeed(LocalDateTime transactionEndedAt) {
        this.transactionEndedAt = transactionEndedAt;
        status = PaymentStatus.PAID;
    }
}
