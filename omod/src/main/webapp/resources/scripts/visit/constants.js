angular.module('constants', [])
    .value('DatetimeFormats', {
        date: "d-MMM-yy",
        dateLocalized: { year: 'numeric', month: 'short', day: 'numeric' },
        time: "hh:mm a",
        // TODO: fix timeLocalized:
        //   'hour12' override is necessary because we're using `fr` in Haiti instead of `fr_HT`.
        //   This override will prevent time from displaying correctly in places that use a 24 hour clock.
        timeLocalized: { hour: '2-digit', minute: '2-digit', hour12: true },
        datetime: "d-MMM-yy (hh:mm a)"
    })

    .value('Concepts', {
        yes: {
            uuid: "3cd6f600-26fe-102b-80cb-0017a47871b2"
        },
        no: {
            uuid: "3cd6f86c-26fe-102b-80cb-0017a47871b2"
        },
        unknown: {
            uuid: "3cd6fac4-26fe-102b-80cb-0017a47871b2"
        },
        diagnosisConstruct: {
            uuid: "159947AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        codedDiagnosis: {
            uuid: "226ed7ad-b776-4b99-966d-fd818d3302c2"
        },
        nonCodedDiagnosis: {
            uuid: "970d41ce-5098-47a4-8872-4dd843c0df3f"
        },
        diagnosisOrder: {
            uuid: "159946AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        primaryOrder: {
            uuid: "159943AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        diagnosisCertainty: {
            uuid: "3cd9ef9a-26fe-102b-80cb-0017a47871b2"
        },
        dispositionConstruct: {
            uuid: "164e9e1b-d26c-4a93-9bdf-af1ce4ae8fce"
        },
        proceduresConstruct: {
            uuid: "c6a87394-4cd0-48b7-a8ac-c7dad55be2e6"
        },
        referralSite: {
            uuid: "1272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        disposition: {
            uuid: "c8b22b09-e2f2-4606-af7d-e52579996de3"
        },
        dispositionComment: {
            uuid: "b4457f1e-ef60-484c-b96a-08180a347e58"
        },
        transferOutLocation: {
            uuid: "113a5ce0-6487-4f45-964d-2dcbd7d23b67"
        },
        dateOfDeath: {
            uuid: "3cde64e4-26fe-102b-80cb-0017a47871b2"
        },
        admissionLocation: {
            uuid: "f3e04276-2db0-4181-b937-d73275dc1b15"
        },
        transferLocation: {
            uuid: "a96352e3-3afc-418b-b79f-3290fc26a3b3"
        },
        returnVisitDate: {
            uuid: "	3ce94df0-26fe-102b-80cb-0017a47871b2"
        },
        clinicalImpressions: {
            uuid: "3cd9d956-26fe-102b-80cb-0017a47871b2"
        },
        typeOfVisit: {
            uuid: "e2964359-790a-419d-be53-602e828dcdb9"
        },
        triageQueueStatus: {
            uuid: "66c18ba5-459e-4049-94ab-f80aca5c6a98"
        },
        paymentInformation: {
            uuid: "7a6330f1-9503-465c-8d63-82e1ad914b47"
        },
        systolicBloodPressure: {
            uuid: "3ce934fa-26fe-102b-80cb-0017a47871b2"
        },
        diastolicBloodPressure: {
            uuid: "3ce93694-26fe-102b-80cb-0017a47871b2"
        },
        temperature: {
            uuid: "3ce939d2-26fe-102b-80cb-0017a47871b2"
        },
        height: {
            uuid: "3ce93cf2-26fe-102b-80cb-0017a47871b2"
        },
        weight: {
            uuid: "3ce93b62-26fe-102b-80cb-0017a47871b2"
        },
        heartRate: {
            uuid: "3ce93824-26fe-102b-80cb-0017a47871b2"
        },
        respiratoryRate: {
            uuid: "3ceb11f8-26fe-102b-80cb-0017a47871b2"
        },
        oxygenSaturation: {
            uuid: "3ce9401c-26fe-102b-80cb-0017a47871b2"
        },
        pastMedicalHistoryConstruct: {
            uuid: "1633AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pastMedicalHistoryFinding: {
            uuid: "1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pastMedicalHistoryPresence: {
            uuid: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pastMedicalHistoryComment: {
            uuid: "160221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        asthma: {
            uuid: "3ccc4bf6-26fe-102b-80cb-0017a47871b2"
        },
        heartDisease: {
            uuid: "f40bf9bb-fcaa-4f90-8199-197bc6cb2b03"
        },
        surgery: {
            uuid: "a2bbe648-8b69-438a-9657-8148478cf951"
        },
        traumaticInjury: {
            uuid: "124193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        epilepsy: {
            uuid: "3cce0a90-26fe-102b-80cb-0017a47871b2"
        },
        haemoglobinopathy: {
            uuid: "117635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        hypertension: {
            uuid: "3cd50188-26fe-102b-80cb-0017a47871b2"
        },
        sexuallyTransmittedInfection: {
            uuid: "3cce6116-26fe-102b-80cb-0017a47871b2"
        },
        congenitalMalformation: {
            uuid: "143849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        malnutrition: {
            uuid: "3cccb654-26fe-102b-80cb-0017a47871b2"
        },
        weightLoss: {
            uuid: "3cd482ee-26fe-102b-80cb-0017a47871b2"
        },
        measles: {
            uuid: "3cccaf06-26fe-102b-80cb-0017a47871b2"
        },
        tuberculosis: {
            uuid: "3ccca7cc-26fe-102b-80cb-0017a47871b2"
        },
        varicella: {
            uuid: "3cd4e978-26fe-102b-80cb-0017a47871b2"
        },
        diphtheria: {
            uuid: "119399AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        acuteRheumaticFever: {
            uuid: "006ab3b2-a0ea-45bf-b495-83e06f26f87a"
        },
        diabetes: {
            uuid: "edf4ecc4-44f6-457a-b561-179f4426b16a"
        },
        prematureBirth: {
            uuid: "6b3447ed-c599-4f51-8ee8-fb78c4f6ef60"
        },
        otherNonCoded: {
            uuid: "3cee7fb4-26fe-102b-80cb-0017a47871b2"
        },
        currentMedications: {
            uuid: "20966786-903b-4ca7-9aa3-159c3ee4458a"
        },
        familyHistoryConstruct: {
            uuid: "160593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryRelative: {
            uuid: "3ce18156-26fe-102b-80cb-0017a47871b2"
        },
        familyHistoryDiagnosis: {
            uuid: "160592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryPresence: {
            uuid: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyHistoryComments: {
            uuid: "160618AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        vaccinationHistoryConstruct: {
            uuid: "74260088-9c83-41d5-b92b-03a41654daaf"
        },
        vaccinationGiven: {
            uuid: "2dc6c690-a5fe-4cc4-97cc-32c70200a2eb"
        },
        vaccinationSequenceNumber: {
            uuid: "ef6b45b4-525e-4d74-bf81-a65a41f3feb9"
        },
        vaccinationDate: {
            uuid: "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        vaccineLotNumber: {
            uuid: "1420AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        vaccineManufacturer: {
            uuid: "1419AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        covidVaccination: {
            uuid: "c3cd46de-21fb-475b-b76a-1c638b250378"
        },
        hepBVaccination: {
            uuid: "3cd42a9c-26fe-102b-80cb-0017a47871b2"
        },
        fluVaccination: {
            uuid: "78032AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        meningoVaccination: {
            uuid: "159900AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        bcgVaccination: {
            uuid: "3cd4e004-26fe-102b-80cb-0017a47871b2"
        },
        polioVaccination: {
            uuid: "3cd42c36-26fe-102b-80cb-0017a47871b2"
        },
        pentavalentVaccination: {
            uuid: "1423AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        rotavirusVaccination: {
            uuid: "83531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        pneumococcalVaccination: {
            uuid: "82213AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        measlesRubellaVaccination: {
            uuid: "162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        diptheriaTetanusVaccination: {
            uuid: "3ccc6b7c-26fe-102b-80cb-0017a47871b2"
        },
        zlLabOrders: {
            uuid: "d4d844de-50bd-4299-8575-1ce59b53908b"
        },
        testOrderNumber: {
            uuid: "393dec41-2fb5-428f-acfa-36ea85da6666"
        },
        hemoglobin: {
            uuid: "3ccc7158-26fe-102b-80cb-0017a47871b2"
        },
        hematocrit: {
            uuid: "3cd69a98-26fe-102b-80cb-0017a47871b2"
        },
        bloodtyping: {
            uuid: "3ccf4090-26fe-102b-80cb-0017a47871b2"
        },
        clinicalManagementPlanComment: {
            uuid: "162749AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        returnVisitDate: {
            uuid: "3ce94df0-26fe-102b-80cb-0017a47871b2"
        },
        supplementHistoryConstruct: {
            uuid: "2fe2b0ad-5b83-40a9-9b95-762afa663405"
        },
        supplementReceived: {
            uuid: "40258d8e-6ea5-46f4-b8e7-7cb302f1393f"
        },
        vitaminA: {
            uuid: "ed69b6e2-63c6-416d-b471-f95c9c5e8847"
        },
        ferrousSulfate: {
            uuid: "3ccee960-26fe-102b-80cb-0017a47871b2"
        },
        iode: {
            uuid: "3669eb6a-fe41-44e3-8263-10993c92e802"
        },
        deworming: {
            uuid: "d171e697-bead-4ca9-b5e0-906b8fa769ea"
        },
        zinc: {
            uuid: "75015399-72f0-4ee4-b45a-7efd9ede2051"
        },
        feedingHistoryConstruct: {
            uuid: "7f32715f-b2cd-4058-9663-1ba7a0bd2d42"
        },
        feedingMethodPresent: {
            uuid: "5b98dcbe-0efc-4cc2-9544-c1da596e7039"
        },
        feedingMethodAbsent: {
            uuid: "2f4b8b9c-0214-4f54-9446-fa306b3ddff0"
        },
        breastedExclusively: {
            uuid: "3cedd4ec-26fe-102b-80cb-0017a47871b2"
        },
        infantFormula: {
            uuid: "3ceb2c7e-26fe-102b-80cb-0017a47871b2"
        },
        mixedFeeding: {
            uuid: "3cf2ddc0-26fe-102b-80cb-0017a47871b2"
        },
        weaned: {
            uuid: "3cd78b10-26fe-102b-80cb-0017a47871b2"
        },
        normalExamFinding: {
            uuid: "3cd750a0-26fe-102b-80cb-0017a47871b2"
        },
        medicationOrders: {  // 1282
            uuid: "3cd9491e-26fe-102b-80cb-0017a47871b2"
        },
        labTestOrderedCoded: {
            uuid: "25fa3a49-ca69-4e8d-9e55-394a9964a1cd"
        },
        labTestOrderedNonCoded: {  // 11762
            uuid: "24102c5d-b199-406f-b49d-83ddd7ce83d5"
        },
        hivTestConstruct: { // PIH:11522
            uuid: "56740c9f-d86e-4240-ad59-7552385a8691"
        },
        hivPCRCoded: { // CIEL:1030
            uuid: "3cd6b1fe-26fe-102b-80cb-0017a47871b2"
        },
        prescriptionConstruct: {
            uuid: "9ab17798-1486-4d56-9218-e3578646a772"
        },
        chiefComplaint: {
            uuid: "160531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        mdToNotify: {
            uuid: "a787e577-5e32-42dc-b3a8-e4c6d5b107f5"
        },
        phoneNumberForClinician: {
            uuid: "f51531c0-e3f6-4611-9a32-d709551307a7"
        },
        procedureOrdered: {
            uuid: "d6d585b6-4887-4aac-8361-424c17b030f2"
        },
        procedureOrderedNonCoded: {
            uuid: "823242df-e317-4426-9bd6-548146546b15"
        },
        procedurePerformed: {
            uuid: "d2a8e2d1-88f9-45f9-9511-4ac5df877340"
        },
        procedurePerformedNonCoded: {
            uuid: "2ccfa5d8-b2a0-4ff0-9d87-c2471ef069f4"
        },
        postPathologyDiagnosisConstruct: {
            uuid: "2da3ec67-62aa-4be8-a32c-cb32723742c8"
        },
        specimenOneComment: {
            uuid: "7d557ddc-eca3-421e-98ae-5469a1ecba4d"
        },
        specimenTwoComment: {
            uuid: "a6f54c87-a6aa-4312-bbc9-1346842a7f3f"
        },
        specimenThreeComment: {
            uuid: "873d2496-4576-4948-80c3-e36913d2a9a7"
        },
        specimenFourComment: {
            uuid: "96010c0d-0328-4d5f-a4e4-b8bb391a3882"
        },
        specimenFiveComment: {
            uuid: "9c79f6ba-3331-44e9-9f64-2b951825dc06"
        },
        specimenSixComment: {
            uuid: "468ef788-c7e2-4a17-9344-60989a77134c"
        },
        specimenSevenComment: {
            uuid: "6ec3616f-4f0e-46f0-b479-68b28f887a52"
        },
        specimenEightComment: {
            uuid: "124ca694-b184-4ada-b6d2-b575b662d9f3"
        },
        specimenResultsComment: {
            uuid: "65a4cc8e-c27a-42d5-b9bf-e13674970d2a"
        },
        urgentReview: {
            uuid: "9e4b6acc-ab97-4ecd-a48c-b3d67e5ef778"
        },
        suspectedCancer: {
            uuid: "d0718b9e-31e3-4bc8-a8d3-cfc5cc1ae2cb"
        },
        immunohistochemistryNeeded: {
            uuid: "237dbbf8-b654-4fed-8c09-b130d35879ac"
        },
        immunohistochemistrySentToBoston: {
            uuid: "5338c4d5-2a7b-4a37-8acc-7d8bd249d2c4"
        },
        processedDate: {
            uuid: "160715AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        accessionNumber: {
            uuid: "162086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        testResultsDate: {
            uuid: "68d6bd27-37ff-4d7a-87a0-f5e0f9c8dcc0"
        },
        negativeTest: {
            uuid: "3cd28732-26fe-102b-80cb-0017a47871b2"
        },
        positiveTest: {
            uuid: "3cd3a7a2-26fe-102b-80cb-0017a47871b2"
        },
        indeterminateTest: {
            uuid: "3cd774d6-26fe-102b-80cb-0017a47871b2"
        },
        visitType: {
            uuid: "164181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        familyPlanningVisit: {
            uuid: "86a2cf11-1ea5-4b8a-9e4b-08f4cdbe1346"
        },
        reasonForVisit: {
            uuid: "e2964359-790a-419d-be53-602e828dcdb9"
        },
        deliveryType: {
            uuid: "fec005b5-6d44-487f-ae34-9f0f483b4ae8"
        },
        pregnancyRiskFactor: {
            uuid: "160079AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        estimatedDueDate: {
            uuid: "3cee56a6-26fe-102b-80cb-0017a47871b2"
        },
        ncdCategory: {
            uuid: "27b30028-0ed0-4f62-a4d8-52a9c5b600e3"
        },
        adverseReaction: {
            uuid: "121764AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        tbSite: {
            uuid: "160040AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        whoHIVStages: {
            uuid: "3cebd8b8-26fe-102b-80cb-0017a47871b2"
        },
        clinicalNotes: {
            uuid: "3cd9d956-26fe-102b-80cb-0017a47871b2"
        },
        emergency: {
            uuid: "9e4b6acc-ab97-4ecd-a48c-b3d67e5ef778"
        }
    })
    .value('PrimaryCareExamConcepts', {
        generalExam: {
            uuid: "0adeea3a-15f5-102d-96e4-000c29c2a5d7"
        },
        abdominalExam: {
            uuid: "3cd76054-26fe-102b-80cb-0017a47871b2"
        },
        cardiovascularExam: {
            uuid: "3cd75e9c-26fe-102b-80cb-0017a47871b2"
        },
        chestExam: {
            uuid: "3cd75d0c-26fe-102b-80cb-0017a47871b2"
        },
        heentExam: {
            uuid: "3cd75b86-26fe-102b-80cb-0017a47871b2"
        },
        lymphExam: {
            uuid: "3cd759f6-26fe-102b-80cb-0017a47871b2"
        },
        mentalStatusExam: {
            uuid: "163043AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        musculoskeletalExam: {
            uuid: "3cd7650e-26fe-102b-80cb-0017a47871b2"
        },
        neuroExam: {
            uuid: "3cd766a8-26fe-102b-80cb-0017a47871b2"
        },
        gynExam: {
            uuid: "20e66bef-d91e-4204-bd60-359dd30444de"
        },
        postpartumExam: {
            uuid: "f74752c7-f490-4218-b8e8-81a65c24666f"
        },
        breastExam: {
            uuid: "159780AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        },
        skinExam: {
            uuid: "3cd75866-26fe-102b-80cb-0017a47871b2"
        },
        uroExam: {
            uuid: "ea4d0ae4-3bac-44fa-96d5-77d3f49d9e77"
        },
        urogenitalExam: {
            uuid: "3cd761ee-26fe-102b-80cb-0017a47871b2"
        }

    })
    .value('EncounterRoles', {
        attendingSurgeon: {
            uuid: "9b135b19-7ebe-4a51-aea2-69a53f9383af"
        },
        assistingSurgeon: {
            uuid: "6e630e03-5182-4cb3-9a82-a5b1a85c09a7"
        },
        consultingClinician: {
            uuid: "4f10ad1a-ec49-48df-98c7-1391c6ac7f05"
        }
    })
    .value('OrderTypes', {
        drugOrder: {
            uuid: "131168f4-15f5-102d-96e4-000c29c2a5d7"
        }
    })
    .value('VisitTypes', {
        clinicalOrHospitalVisit: {
            uuid: "f01c54cb-2225-471a-9cd5-d348552c337c"
      }
    });
