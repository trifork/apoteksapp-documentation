# Business Concepts and Flow

## FMK Orders

It is possible to identify orders in FMK that are made by the app (technically by ASP). When ASP create an FMK order it
will set the following field and content:

```
CreateOrderRequest
    PrescriptionMedication
        OrderInstruction = "DA App bestilling"
```

## Order Status

The pharmacy can set any of these status for an order at any time.

**APPROVED**\
Triggers notification on citizens device: no\
This is the initial status. The citizen has placed an order that is ready to be effectuated by the pharmacy.

**PACKAGED**\
Triggers notification on citizens device: no\
The pharmacy can use this as an internal status to keep track of the effectuation.

**READY_FOR_PICKUP**\
Triggers notification on citizens device: yes\
The order is ready for pickup at a pharmacy or delivery location.

**SENT**\
Triggers notification on citizens device: yes\
The order has been sent using a distributor company (PostNord or GLS).

**DELIVERED**\
Triggers notification on citizens device: yes\
The order has been delivered by a delivery man.

**CANCELLED**\
Triggers notification on citizens device: yes\
The pharmacy has cancelled the order.

