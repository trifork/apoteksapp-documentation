# ASP Auto Prescription Algorithm

This document supplements the OpenAPI specification for the `/api/v1/auto-prescription` endpoint with technical notes on validation logic and expected behaviors. It is for implementers integrating with the ASP backend.

---

## Function and Scope

The endpoint evaluates a patient’s prescriptions using a fixed set of rules to assist with automated dispensing and to flag potential issues, as defined by Danmarks Apotekerforening. It outputs both eligibility signals (blocking validations) and informational messages.

---

## Workflow and Rule Model

- Receives a medicine card with drug medications and prescriptions.
- Pulls out all prescriptions for per-prescription rule evaluation.
- Executes all validation and informative rules on each prescription.
- Bundles rule results per prescription and returns these in the HTTP response.

---

## Validation Rule Catalogue

| Rule Name                         | Description                                                                                              | Enum                              |
|-----------------------------------|----------------------------------------------------------------------------------------------------------|-----------------------------------|
| Dispensing regulations            | Dispensation type must be one of: B, HA, HF, HA18, HX18, HX.                                             | DISPENSING_REGULATIONS            |
| Unknown or expired package number | Package number must be recognized and not expired.                                                       | UNKNOWN_OR_EXPIRED_PACKAGE_NUMBER |
| Stable treatment                  | The patient must have had the same treatment for at least 6 months.                                      | STABLE_TREATMENT                  |
| Ordination matches prescription   | Ordination must match prescription dose (currently only checks dose text).                               | ORDINATION_MATCHES_PRESCRIPTION   |
| Dispensing interval               | If the prescription is a reiterated type, the effectuations must happen within the reiteration interval. | DISPENSING_INTERVAL               |
| Not dosis dispensed               | Prescription must not be marked as dose-dispensed.                                                       | NOT_DOSIS_DISPENSED               |
| Total amount dispensed            | If there is a residual on the prescription, it must be positive.                                         | TOTAL_AMOUNT_DISPENSED            |
| Drug blacklist                    | Prescription’s package number must not be found on DA's blacklist.                                       | DRUG_BLACKLIST                    |
| Medicine interactions             | Cross-drug interactions must not be present between prescribed medications.                              | MEDICINE_INTERACTIONS             |

---

## Informative Rule Catalogue

| Rule Name                    | Description                                                           | Enum                        |
|------------------------------|-----------------------------------------------------------------------|-----------------------------|
| Storage conditions           | Informs about storage requirements for the drug.                      | STORAGE_CONDITIONS          |
| Is next dispensing the last? | Informs if this or the next dispensing will exhaust the prescription. | IS_NEXT_DISPENSING_THE_LAST |

---

## Endpoint Usage

### Request

POST `/api/v1/auto-prescription`

#### Example Request Body

```json
{
  "simpleMedicineCard": {
    "drugMedications": [
      {
        "id": 101,
        "drugIdentifier": {
          "id": 504878,
          "source": "Medicinpriser"
        },
        "indicationCode": 42,
        "dosage": {
          "startDate": "2024-07-01",
          "endDate": "2025-12-31",
          "shortText": "1 tablet morgen og aften"
        },
        "created": "2024-07-01T10:00:00Z",
        "modified": "2024-07-15T08:00:00Z",
        "doseDispensed": false,
        "prescriptions": [
          {
            "id": 201,
            "drugIdentifier": {
              "id": 504878,
              "source": "Medicinpriser"
            },
            "singlePrescriptionDispensing": {
              "packageNumber": 504878,
              "packageDetails": {
                "packageNumber": 504878,
                "storageConditionText": "Opbevares på køl",
                "dispensing": "B"
              }
            },
            "validFrom": "2024-07-01",
            "validTo": "2025-07-01",
            "indicationCode": 42,
            "dosageText": "1 tablet morgen og aften",
            "effectuationTimestamps": [
              "2024-07-02T13:30:00Z",
              "2024-08-01T09:15:00Z"
            ],
            "residual": {
              "unit": "stk",
              "amount": 24.0
            }
          },
          ... more prescriptions, if any ...
        ]
      },
      ... more drugMedications, if any ...
    ]
  }
}
```

---

### Example Response

```json
{
  "passedPrescriptions": {
    "201": true,
    "202": false,
    "203": false
  },
  "ruleResults": {
    "201": [
      {
        "ruleName": "DISPENSING_REGULATIONS",
        "ruleResult": true
      },
      {
        "ruleName": "ORDINATION_MATCHES_PRESCRIPTION",
        "ruleResult": true
      },
      {
        "ruleName": "STORAGE_CONDITIONS",
        "ruleResult": true,
        "ruleNotes": {
          "text": "Opbevares på køl"
        }
      },
        ... All other rules for prescription 201 are in this list, but are omitted here for brevity...
    ],
    "202": [
        ...
    ],
    "203": [
        ...
    ]
  }
}
```

**Explanations:**

- `passedPrescriptions` contains per-prescription booleans: `true` if all validation rules pass, `false` otherwise.
- `ruleResults` contains arrays of results per prescription. Each entry details the rule evaluated, the outcome, and optional explanatory notes where assessment produces context (e.g., reasons for failure or required storage conditions).
- Validation rules (blocking) and informative rules (as context) are both included; result structure is the same.

## Implementation Guide

### 1. Prerequisites
- Ensure your system can construct and serialize the `simpleMedicineCard` object according to the OpenAPI schema.
- Confirm the endpoint can be accessed, this should be already be the case if you use some of the other ASP endpoints, as the authentication is the same.

### 2. Request Construction
- Gather relevant data for the medicine card:
    - Each `drugMedication` must include at least one `prescription` with the necessary identifiers and relevant fields.
    - Populate all fields required for rule evaluations: package number, dispensing type, dosage, timestamps, residual, etc.
- Follow the request example for field arrangement and data structure.
- Validate outbound payloads against the OpenAPI schema to avoid rejections due to bad input.

### 3. Handling the Response
- On HTTP 200, parse the `passedPrescriptions` and `ruleResults` sections:
    - Use `passedPrescriptions` as an overview for each prescription relevant for the patient, this should give the pharmacist a quick indication of which prescriptions can be handed out without further checks or explanations to the patient.
    - Show `ruleResults` and any `ruleNotes` in UI or logs, so the pharmacist can see detailed reasons information about what they should inform the patient about, or why a prescription cannot be handed out.
- Responses contain all validation and informational rules for every prescription. Informative rules must be displayed as notices, not as alerts.

### 4. Best Practices
- Always provide up-to-date and complete data to the endpoint for reliable results.
- For integration testing, include diverse edge cases (expired prescriptions, multiple package numbers, known blacklist items, etc.).
- Information from informative rules should be clearly distinguished from validation failures in any user interface.
- Validation failures should halt automatic dispensing processes if available, but should not prevent pharmacists from manually overriding decisions if they deem it safe and appropriate.
- The response is only a guideline; pharmacists retain ultimate responsibility for dispensing decisions, and this is merely a tool to assist them.


---

## Notes

- Intended to be called either by the pharmacy IT system or through an app by/for the citizen.
- The endpoint is advisory: all handing out of prescription drugs remains under professional pharmacist responsibility.
- Only calls made by authenticated pharmacies are processed.
- For DA integration help or questions, consult the project maintainer or DA directly.

---

[OpenAPI spec (repo link)](https://github.com/trifork/apoteksapp-documentation/blob/main/asp-pharmacy-api/asp-pharmacy-api.yml)