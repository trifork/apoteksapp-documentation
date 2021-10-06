# ASP Nearby Stock Status Programme

This description of the nearby stock status functionality supplements the OpenAPI-description with some additional notes
about the functionality.

## Basics

It is possible to request stock status data for all pharmacies that participate in the ASP Nearby Stock Status
Programme, but this can only be done in two ways with some intended limitations:

- A pharmacy can retrieve a list of all pharmacies (including those that do not participate), but no more than 5
  participating pharmacies can have stock status `IN_STOCK` ordered by their distance to the requesting pharmacy. All
  pharmacies located further away than the 5th `IN_STOCK`-pharmacy will have status `EXCLUDED_FROM_CURRENT_QUERY`
  or `NOT_PARTICIPATING`. Pharmacies may have status `MISSING_DATA` if they cannot be queried for technical reasons (
  e.g., temporarily missing data from a specific pharmacy).
- A pharmacy can query a single specified pharmacy (that participate).

The requesting/acting pharmacy must also be participating in the nearby stock status program.

## Unavailable pharmacies

If stock status data for a pharmacy is unavailable for any reason, the API response will indicate this instead of
returning wrong data for that pharmacy. If a pharmacist recognises that the pharmacy nearest their location fails to
respond, they might tell the inquiring customer that the drug _could_ be in stock there or perhaps call the pharmacy to
ask directly.

## Data frequency

Currently, data is acquired in two different ways:
- For PharmaNet pharmacies the data is queried live (at request time).
- For Cito Pharmacies the data is updated every 15th min.

## Expiry of stock data

For pharmacies that report stock data to the ASP (Cito pharmacies at the time of writing) a threshold is enforced
regarding the age of the data. A pharmacy's most recent complete stock update must be at most 26 hours old or else all
stock status for that specific pharmacy will be returned as `MISSING_DATA`.

## Back order status

Whether the specified drug package is back ordered at the distributor or not, is also included in the response. The
information is available for all the known distributors, currently TMJ (returned as `tmj` in the API) and Nomeco (
returned as `nomeco` in the API).
