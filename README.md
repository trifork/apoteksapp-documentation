# Apoteksapp Public Documentation

This repository contains the public documentation for **Apotekernes Service Platform (ASP)**.

## ASP Pharmacy API

More information can be found in the [/asp-pharmacy-api](./asp-pharmacy-api) subfolder.

## Pharmacy System API

More information can be found in the [/pharmacy-system-api](./pharmacy-system-api) subfolder.

## Dependency graph

The following figure illustrates the dependencies of the ASP.
An arrow from `A` -> `B` means that `A` depends on `B`, or alternatively: `A` initiates sends requests to `B`.
A cylinder shape indicates an ASP component with a database.

*Note that Kafka is also used as a means of data transfer, but is not shown in the dependency graph, as its bi-directional arrows only serve to muddle the picture*

```mermaid
graph LR
    subgraph asp["ASP"]
        core[("core")]
        click core "https://github.com/trifork/apoteksapp-backend-core"
        
        userdata[("userdata")]
        click userdata "https://github.com/trifork/apoteksapp-backend-userdata"
        
        pharmacyMasterdata[("pharmacy-masterdata")]
        click pharmacyMasterdata "https://github.com/trifork/apoteksapp-backend-pharmacy-masterdata"
        
        nspMasterdata[("nsp-masterdata")]
        click nspMasterdata "https://github.com/trifork/apoteksapp-backend-nsp-masterdata"
        
        drugPackages[("drug-packages")]
        click drugPackages "https://github.com/trifork/apoteksapp-backend-drug-packages"
        
        subscriptions[("subscriptions")]
        click subscriptions "https://github.com/trifork/apoteksapp-backend-subscriptions"
               
        pharmacyApi["pharmacy-api"]
        click pharmacyApi "https://github.com/trifork/apoteksapp-backend-pharmacy-api"
        
        pharmacyGateway["pharmacy-gateway"]
        click pharmacyGateway "https://github.com/trifork/apoteksapp-backend-pharmacy-gateway"
        
        nasClient[("nas-client")]
        click nasClient "https://github.com/trifork/apoteksapp-backend-nas-client"
        
        userAdmin["user-administration"]
        click userAdmin "https://github.com/trifork/apoteksapp-backend-user-administration"
        
        notifications["notifications"]
        click notifications "https://github.com/trifork/apoteksapp-backend-notifications"

        analytics["analytics"]
        click analytics "https://github.com/trifork/apoteksapp-backend-analytics"
        
        dosePriceWeb["dosis-pris"]
        click dosePriceWeb "https://github.com/trifork/apoteksapp-dosis-pris"
    end

    subgraph www["Web"]
        procurationAdmin["Fuldmagts administration"]
        click procurationAdmin "https://app.apoteket.dk/login"
    
        dosePrice["Prisberegner dosispakket medicin"]
        click dosePrice "https://app.apoteket.dk/prisberegner-dosispakket-medicin"
    end

    subgraph nsp["NSP"]
        sosi
        sces
        fmk
        ddv
        nas
        taksten
    end

    app
    sitecore["Sitecore"]
    citosys["C2 (Citosys)"]
    pharmaNet["PharmaNet"]
    tmj["TMJ"]
    nomeco["Nomeco"]
    postnord["PostNord"]
    gls["GLS"]
    
    core --> userdata & pharmacyMasterdata & nspMasterdata & drugPackages & subscriptions & pharmacyGateway & notifications & fmk & ddv & sosi & postnord & gls
    nspMasterdata --> taksten
    drugPackages --> pharmacyMasterdata & pharmacyGateway & tmj & nomeco
    subscriptions --> userdata & pharmacyMasterdata & nspMasterdata & notifications
    pharmacyApi --> userdata & pharmacyMasterdata & nspMasterdata & drugPackages & subscriptions
    pharmacyGateway --> pharmacyMasterdata & citosys & pharmaNet
    nasClient --> nas
    userAdmin --> userdata
    notifications --> userdata

    app --> core
    procurationAdmin --> userAdmin
    dosePrice --> dosePriceWeb
    sitecore --> pharmacyMasterdata
    citosys & pharmaNet --> pharmacyApi
```
