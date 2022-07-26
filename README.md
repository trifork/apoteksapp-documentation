# Apoteksapp Public Documentation

This repository contains the public documentation for **Apotekernes Service Platform (ASP)**.

## ASP Pharmacy API

More information can be found in the [/asp-pharmacy-api](./asp-pharmacy-api) subfolder. 

## Pharmacy System API

More information can be found in the [/pharmacy-system-api](./pharmacy-system-api) subfolder.

## General dependency overview
The following figure illustrates the dependencies of the ASP. 
An arrow from `A` -> `B` means that `A` depends on `B`, or alternatively: `A` initiates sends requests to `B`.
Note that Kafka is also used as a means of data transfer, but is not shown in the dependency graph, as its bi-directional arrows only serve to muddle the picture

```mermaid
flowchart LR

    subgraph asp["ASP"]
        core[(core)]
        nspMasterData[(nspMasterData)]
        subscriptions[(subscriptions)]
        userAdmin
        userData[(userData)]
        nas[(nas)]
        drugPackages[(drugPackages)]
        notifications
        pharmacyMasterData[(pharmacyMasterData)]
        pharmacyApi
        pharmacyGateway
        nasClient[(nasClient)]
    end

    subgraph nsp["NSP"]
        fmk
        ddv
        sces
        taksten
        sosi
    end

    app
    www
    tmj
    nomeco
    sitecore
    citosys
    pharmaNet

    postnord
    gls

    app --> core
    www --> userAdmin
    core --> subscriptions & userData & nspMasterData & drugPackages & notifications & pharmacyGateway & pharmacyMasterData & postnord & gls & fmk & ddv & sosi
    nspMasterData --> taksten
    userAdmin --> userData
    userData --> sces & sosi
    nasClient --> nas
    drugPackages --> pharmacyMasterData & pharmacyGateway & tmj & nomeco
    notifications --> userData
    pharmacyMasterData --> sitecore
    pharmacyGateway --> pharmacyMasterData & citosys & pharmaNet
    pharmacyApi --> drugPackages & nspMasterData & pharmacyMasterData & subscriptions & userData
    subscriptions --> notifications & nspMasterData & pharmacyMasterData & userData
    citosys & pharmaNet --> pharmacyApi

    classDef database fill:#faa;
```

