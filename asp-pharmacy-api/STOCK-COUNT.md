# Stock counts

## Basics

It is possible to retrieve a list of the (up to) five nearest pharmacies that have a specific drug package in stock.
If fewer than five pharmacies are returned, the pharmacies that are returned are the only ones in the country with the specified drug package in stock.

The requesting pharmacy must be authorised to act on behalf on the pharmacy specified in the request body. 
Additionally, the specified pharmacy must be participating in the ASP-wide nearby-stock status program.
The returned list is sorted by the pharmacies' distance from the pharmacy specified in the request body. 

## Unavailable pharmacies

In case some nearby pharmacies fail to respond to a request for their stock count, they are included in the response, but in a separate list. 
If a pharmacist recognizes that the pharmacy nearest their location fails to respond, they might tell the inquiring customer that the drug _could_ be in stock there or perhaps call the pharmacy to ask directly.

## Back order status
Whether the specified drug package is back ordered at the distributor or not, is also included in the response. 
The information is available for all the known distributors, currently `TMJ` and `Nomeco`.