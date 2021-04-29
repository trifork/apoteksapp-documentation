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

## Orders and Person Information

There are 3 different properties stating information about persons related to an order: `patient`, `orderedBy` and
`paymentBy`.\
The `paymentBy` property is not required and therefore only the `orderedBy` property states who actually placed the
order. This could be relevant e.g. if the pharmacy needs to know who is responsible for placing an order that never gets
collected.\
The following example illustrates different possible scenarios.

- A user makes an order for oneself:
    - `paymentMethod`: BY_PICKUP
        - `patient`: the user
        - `orderedBy`: the user
        - `paymentBy`: `null`
    - `paymentMethod`: BY_AGREEMENT
        - `patient`: the user
        - `orderedBy`: the user
        - `paymentBy`: the user
- A user makes an order for its child:
    - `paymentMethod`: BY_PICKUP
        - `patient`: the child
        - `orderedBy`: the user
        - `paymentBy`: `null`
    - `paymentMethod`: BY_AGREEMENT
        - `patient`: the child
        - `orderedBy`: the user
        - `paymentBy`: the user
- A user uses its authorisation to make an order for another user:
    - `paymentMethod`: BY_PICKUP
        - `patient`: the other user
        - `orderedBy`: the user
        - `paymentBy`: `null`
    - `paymentMethod`: BY_AGREEMENT
        - `patient`: the other user
        - `orderedBy`: the user
        - `paymentBy`: the user OR the other user

