# Testing an integration in the app
Upon request, it is possible to create a pharmacy in our test environment set to a specific webshop URL. 
An invitation to download and use the test version of the app on either iOS or Android can also be sent. 


The following pharmacies have been created in the test environment:

|Name                 |Pharmacy number|
|---------------------|---------------|
|Din Apoteker Apotek  |`99902`        |
|Apotekeren Apotek    |`99903`        |


The following test cases describe data that exists in the FMK test2 environment, which the test app uses.
They provide a good starting point for testing different values for each parameter.
_As multiple webshop integrators/developers are likely to use the same users, it is important that they check that the pharmacy of the user is set to the relevant one whenever they test._


#### Case 1
Emil (151076-9194) has a prescription for two _Telfast_ and substitutes it with a single _Fexofenadin "Cipla"_

|Parameter              |Value            |
|-----------------------|-----------------|
|`pharmacy_number`      |`9990X`*         |
|`prescription_id`      |`350149670131221`|
|`package_number`       |`493636`         |
|`package_quantity`     |`1`              |
|`prescription_quantity`|`2`              |


#### Case 2
Johannes (040580-9885) has a prescription for one _Pamol_

|Parameter              |Value            |
|-----------------------|-----------------|
|`pharmacy_number`      |`9990X`*         |
|`prescription_id`      |`350149670456221`|
|`package_number`       |`523801`         |
|`package_quantity`     |`1`              |
|`prescription_quantity`|`1`              |


#### Case 3
Marie (230560-9717) has a prescription for one _Ibumax 100 stk._ and substitutes it with two _Ibuprofen "Aristo" 50 stk._

|Parameter              |Value            |
|-----------------------|-----------------|
|`pharmacy_number`      |`9990X`*         |
|`prescription_id`      |`369327875087021`|
|`package_number`       |`167827`         |
|`package_quantity`     |`2`              |
|`prescription_quantity`|`1`              |


_*The pharmacy number parameter is dependent on the pharmacy chosen, but pharmacies created for this will share the first four digits_
