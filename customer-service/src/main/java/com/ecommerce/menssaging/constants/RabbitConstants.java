package com.ecommerce.menssaging.constants;

public class RabbitConstants {

    private RabbitConstants() {}

    public static final String ECOMMERCE_EXCHANGE =
            "ecommerce.events";

    public static final String DEAD_LETTER_EXCHANGE =
            "ecommerce.dlx";

    public static final String USER_CREATED_QUEUE =
            "customer.user.created";

    public static final String USER_CREATED_DLQ =
            "customer.user.created.dlq";

    public static final String USER_CREATED_ROUTING_KEY =
            "user.created";
}
