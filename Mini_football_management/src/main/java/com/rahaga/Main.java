package com.rahaga;

import org.td.entity.*;

public class Main {

    public static void main(String[] args) {

        
        System.out.println(System.getenv("JDBC_URL"));
        System.out.println(System.getenv("USERNAME"));
        System.out.println(System.getenv("PASSWORD"));

        DataRetriever dr = new DataRetriever();

        Order o = new Order();
        o.setReference("CMD-001");
        o.setOrderType(OrderTypeEnum.EAT_IN);
        o.setOrderStatus(OrderStatusEnum.CREATED);

       
        dr.saveOrder(o);
        System.out.println("Commande créée");

        
        o.setOrderStatus(OrderStatusEnum.READY);
        dr.saveOrder(o);
        System.out.println("Commande READY");

       
        o.setOrderStatus(OrderStatusEnum.DELIVERED);
        dr.saveOrder(o);
        System.out.println("Commande DELIVERED");

        
        try {
            o.setOrderType(OrderTypeEnum.TAKE_AWAY);
            dr.saveOrder(o);
        } catch (Exception e) {
            System.out.println("Exception attendue : " + e.getMessage());
        }
    }
}
