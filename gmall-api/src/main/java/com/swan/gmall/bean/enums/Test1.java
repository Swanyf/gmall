package com.swan.gmall.bean.enums;

public class Test1 {

    public static void main(String[] args) {
        String comment = OrderStatus.UNPAID.getComment();
        System.out.println(comment);

        String name = PaymentStatus.PAID.getName();

        System.out.println(name);
    }
}
