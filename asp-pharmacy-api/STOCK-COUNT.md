# ASP Nearby Stock Status Programme

## Basics

It is possible to request stock count data for all pharmacies that participate in the ASP Nearby Stock Status Programme,
but this can only be done in two ways with some intended limitations:

- A pharmacy can retrieve a list of all pharmacies (that participate), but no more than 5 pharmacies can have stock
  status IN_STOCK ordered by their distance to the requesting pharmacy. All pharmacies located further away than the 5th
  IN_STOCK-pharmacy will have status EXCLUDED_FROM_CURRENT_QUERY. Pharmacies may have status MISSING_DATA if they cannot
  be queried for technical reasons (e.g., temporarily missing data from a specific pharmacy).
- A pharmacy can query a single specified pharmacy (that participate).

The requesting pharmacy must be authorised to act on behalf on the pharmacy specified in the request. Additionally, the
specified pharmacy must be participating in the nearby stock status program.

## Unavailable pharmacies

If stock count data for a pharmacy is unavailable for any reason, the API response will indicate this instead of
returning wrong data for that pharmacy. If a pharmacist recognises that the pharmacy nearest their location fails to
respond, they might tell the inquiring customer that the drug _could_ be in stock there or perhaps call the pharmacy to
ask directly.

## Back order status

Whether the specified drug package is back ordered at the distributor or not, is also included in the response. The
information is available for all the known distributors, currently `TMJ` and `Nomeco`.