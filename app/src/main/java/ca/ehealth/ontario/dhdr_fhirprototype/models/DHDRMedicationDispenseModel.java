/*
    *** DISCLAIMER ***
    This file was copy/pasted from the FHIR North 2018 Java project which was posted on Innovation Lab's Code Sharing repository.

    I have made some changes to this file to implement it to our application.

    Link to the Innovation Lab Repo: https://www.innovation-lab.ca/repository/ViewRepository?id=1911d245-5c6c-4156-bd7d-2f0701d6ce28
    Link to the GitHub: https://github.com/EHO-Innovation-Lab/FHIRNorthJava

    Compliments to the author, Dawne Pierce from Ideaworks MEDIC (from Mohawk College)
 */

package ca.ehealth.ontario.dhdr_fhirprototype.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;

public class DHDRMedicationDispenseModel implements Parcelable
{
    private String dispenseDate = "n/a";
    private String brandName = "n/a";
    private String genericName = "n/a";
    private String strength = "n/a";
    private String form = "n/a";
    private String quantity = "n/a";
    private String daysSupply = "n/a";
    private String daysSupplyLeft = "n/a";
    private String dispenseStatus = "n/a";
    private String refillsLeft = "n/a";
    private String expirationDate = "n/a";
    private String requestStatus = "n/a";
    private String drugIdentificationNumber = "n/a";
    private String prescriptionNumber = "n/a";
    private String reasonForUse = "n/a";
    private String dosageInstruction = "n/a";
    private String prescriberName = "n/a";
    private String prescriberPhone = "n/a";
    private String prescriberId = "n/a";
    private String pharmacyName = "n/a";
    private String pharmacyPhone = "n/a";
    private String pharmacistName = "n/a";
    private String classification = "n/a";
    private String subClassification = "n/a";
    private ArrayList<String> responseCodes = new ArrayList<>();
    private ArrayList<String> interventionCodes = new ArrayList<>();
    private ArrayList<String> durTexts = new ArrayList<>();
    private int durTotal; //
    private boolean isConsentGiven = true;

    public DHDRMedicationDispenseModel()
	{
        // use default values
    }

    /**
     * Constructor to handle simple patient implementation with explicitly given parameters
     */
    public DHDRMedicationDispenseModel(String medicationName, String dispenseDate, String daysSupply, String prescriberName, String classification, String subClassification)
	{
        setBrandName(medicationName);
        setDispenseDate(dispenseDate);
        setDaysSupply(daysSupply);
        setPrescriberName(prescriberName);
        setClassification(classification);
        setSubClassification(subClassification);
    }

    /**
     * Map result resources to class variables with MedicationDispense and OperationOutcome objects
     */
    public DHDRMedicationDispenseModel(MedicationDispense medicationDispenseResult, OperationOutcome operationOutcomeResult)
	{
        /* *******************************************************************************************
         *  Operation Outcome
         * *******************************************************************************************/
        mapOperationOutcome(operationOutcomeResult);

        /* *******************************************************************************************
         *  The Medication Dispense resource
         * *******************************************************************************************/
        mapMedicationDispense(medicationDispenseResult);

		// if we have a contained array, then its probably the array of contained resources under medication dispense,
        // so lets loop through it and get the containing data
        if (medicationDispenseResult.getContained() != null && medicationDispenseResult.getContained().getContainedResources() != null && !medicationDispenseResult.getContained().getContainedResources().isEmpty())
		{// loop through contained resources
            for (IResource containedResource : medicationDispenseResult.getContained().getContainedResources())
			{
                /* *******************************************************************************************
                 * Medication Order resource
                 * *******************************************************************************************/
                if (containedResource instanceof MedicationOrder)
                {
                    mapMedicationOrder((MedicationOrder) containedResource);
                }
                /* *******************************************************************************************
                 * Medication resource
                 * *******************************************************************************************/
                else if (containedResource instanceof Medication)
				{
                    mapMedication((Medication) containedResource);
                }
                /* *******************************************************************************************
                 * Organization resource
                 * *******************************************************************************************/
                else if (containedResource instanceof Organization)
                {
                    mapOrganization((Organization) containedResource);
                }
                /* *******************************************************************************************
                 * Practitioner resource
                 * *******************************************************************************************/
                else if (containedResource instanceof Practitioner)
				{
                    mapPractitioner((Practitioner) containedResource);
                }
            }
        }

        setDaysSupplyLeft(calculateDaysSupplyLeft(getDispenseDate(), getDaysSupply()));
    }

    /* *******************************************************************************************
     * Resource mappings
     * *******************************************************************************************/

    private void mapOperationOutcome(OperationOutcome operationOutcomeResult)
    {
        if (operationOutcomeResult != null)
        {
            for(OperationOutcome.Issue outcome : operationOutcomeResult.getIssue())
            {
                // "suppressed" and "warning" usually means there is a consent block
                if (outcome.getCode().equals("Suppressed") && outcome.getSeverity().equals("warning"))
                {
                    setConsentGiven(false);
                }
            }
        }
    }

    private void mapMedicationDispense(MedicationDispense medicationDispenseResult)
    {
        // Extract quantity
        // V1 DHDR FHIR Specification: medicationDispense.quantity
        if (medicationDispenseResult.getQuantity() != null && medicationDispenseResult.getQuantity().getValue() != null)
        {
            setQuantity(medicationDispenseResult.getQuantity().getValue().toString());
        }

        // Extract days supply
        // V1 DHDR FHIR Specification: medicationDispense.daysSupply.value
        if (medicationDispenseResult.getDaysSupply() != null)
        {
            setDaysSupply(medicationDispenseResult.getDaysSupply().getValue().toString());
        }

        // Extract when handed over (dispense date)
        // V1 DHDR FHIR Specification: medicationDispense.whenHandedOver
        if (medicationDispenseResult.getWhenHandedOver() != null)
        {
            // here we try to parse out a date object to convert to a new format: dd-mm-yyy
            try
            {
                // format pattern that is returned by DHDR: Sat Feb 24 00:00:00 EST 2018
                SimpleDateFormat formatReceived = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.CANADA);

                // parse out the Date object from the dispense date using the format object
                Date dateDispensed = formatReceived.parse(medicationDispenseResult.getWhenHandedOver().toString());

                // apply a format like: 05-Feb-1987 to the string
                formatReceived.applyPattern("dd-MMM-YYYY");

                // finally, we can save the date with new format applied
                setDispenseDate(formatReceived.format(dateDispensed));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        // Extract dispense status
        // V1 DHDR FHIR Specification: medicationDispense.status
        if(medicationDispenseResult.getStatus() != null)
        {
            setDispenseStatus(medicationDispenseResult.getStatus());
        }

        // Extract DURs
        // currently it is just extension.value
        if (!medicationDispenseResult.getUndeclaredExtensions().isEmpty())
        {
            // for each response code
            for(ExtensionDt extensionObject : medicationDispenseResult.getUndeclaredExtensionsByUrl("http://lite.innovation-lab.ca:9443/dispense-service/StructureDefinition/ca-on-drug-extension-durResponseCode"))
            {
                // we are expecting the object to be of this type, but check to make sure
                if (extensionObject.getValue() instanceof CodeableConceptDt)
                {
                    CodeableConceptDt codeableConceptDt = (CodeableConceptDt) extensionObject.getValue();


                    if (codeableConceptDt.getCoding() != null && !codeableConceptDt.getCoding().isEmpty() && codeableConceptDt.getCoding().get(0).getCode() != null)
                    {
                        addResponseCode(codeableConceptDt.getCoding().get(0).getCode());
                    }
                }
            }

            // for each intervention code
            for(ExtensionDt extensionObject : medicationDispenseResult.getUndeclaredExtensionsByUrl("http://lite.innovation-lab.ca:9443/dispense-service/StructureDefinition/ca-on-drug-extension-durInterventionCode"))
            {
                // we are expecting the object to be of this type, but check to make sure
                if (extensionObject.getValue() instanceof CodeableConceptDt)
                {
                    CodeableConceptDt codeableConceptDt = (CodeableConceptDt) extensionObject.getValue();


                    if (codeableConceptDt.getCoding() != null && !codeableConceptDt.getCoding().isEmpty() && codeableConceptDt.getCoding().get(0).getCode() != null)
                    {
                        addInterventionCode(codeableConceptDt.getCoding().get(0).getCode());
                    }
                }

            }

            // for each durTextMessage
            for(ExtensionDt extensionObject : medicationDispenseResult.getUndeclaredExtensionsByUrl("http://lite.innovation-lab.ca:9443/StructureDefinition/ca-on-drug-extension-durTextMessage"))
            {
                // we're expecting this object to be a simple url and value object
                if (extensionObject.getValue() != null)
                {
                    addDurTextMessage(extensionObject.getValue().toString());
                }
            }

            setDurTotal(getResponseCodes().size());
        }
    }

    private void mapMedicationOrder(MedicationOrder medicationOrder)
    {
        // Extract prescription number
        // V1 DHDR FHIR Specification: medicationOrder.identifier[x].value
        if (medicationOrder.getIdentifier() != null && !medicationOrder.getIdentifier().isEmpty() && medicationOrder.getIdentifier().get(0) != null && medicationOrder.getIdentifier().get(0).getValue() != null)
        {
            setPrescriptionNumber(medicationOrder.getIdentifier().get(0).getValue());
        }

        // Extract reason for use
        // V1 DHDR FHIR Specification: medicationOrder.reasonCode.coding.display
        //but Hapi fhir only seems to provide getReason which returns an IDataType.
        if (medicationOrder.getReason() != null)
        {
            setReasonForUse(medicationOrder.getReason().toString());
        }
    }

    private void mapMedication(Medication medication)
    {
        // Extract brand name, generic name, DIN, class and sub class of the medication
        // V1 DHDR FHIR Specification: medication.code.coding[x].display, medication.code.coding[x].code
        // the coding array has 1...4 cardinality, so we will need to look at the coding system of each object
        if (medication.getCode() != null && medication.getCode().getCoding() != null && !medication.getCode().getCoding().isEmpty())
        {
            for (CodingDt coding : medication.getCode().getCoding())
            {
                switch (coding.getSystem())
                {
                    // here we have our brand name and DIN
                    case "http://hl7.org/fhir/NamingSystem/ca-hc-din":
                        setBrandName(coding.getDisplay());
                        setDrugIdentificationNumber(coding.getCode());

                        // here we have our generic name
                    case "https://ehealthontario.ca/API/FHIR/NamingSystem/ca-drug-gen-name":
                        setGenericName(coding.getDisplay());
                        break;

                    // here we have our drug classification
                    case "https://ehealthontario.ca/API/FHIR/NamingSystem/ca-on-drug-class-ahfs":
                        setClassification(coding.getDisplay());
                        break;

                    // here we have our drug sub-classification
                    case "https://ehealthontario.ca/API/FHIR/NamingSystem/ca-on-drug-subclass-ahfs":
                        setSubClassification(coding.getDisplay());
                        break;
                }
            }
        }

        // Extract medication form
        // V1 DHDR FHIR Specification: practitioner.product.form.text
        if (medication.getProduct() != null && medication.getProduct().getForm() != null && medication.getProduct().getForm().getText() != null)
        {
            setForm(medication.getProduct().getForm().getText());
        }

        // Extract get medication strength
        // V1 DHDR FHIR Specification: practitioner.product.extension[x].value
        if (!medication.getProduct().getUndeclaredExtensions().isEmpty() && medication.getProduct().getUndeclaredExtensions().get(0) != null && medication.getProduct().getUndeclaredExtensions().get(0).getValue() != null)
        {
            // if we have the drug strength extension
            if (medication.getProduct().getUndeclaredExtensions().get(0).getUrlAsString().equals("https://ehealthontario.ca/API/FHIR/NamingSystem/StructureDefinition/ca-on-drug-extension-strength"))
            {
                ExtensionDt extension = medication.getProduct().getUndeclaredExtensions().get(0);
                setStrength(extension.getValue().toString());
            }
        }
    }

    private void mapOrganization(Organization organization)
    {
        // check system code to ensure this belongs to medication dispense
        if (!organization.getIdentifier().isEmpty() && organization.getIdentifier().get(0).getSystem().equals("https://ehealthontario.ca/API/FHIR/NamingSystem/StructureDefinition/ca-on-pharmacy-id-ocp"))
        {
            // Extract pharmacy name
            // V1 DHDR FHIR Specification: organization.name[x]
            if (organization.getName() != null)
            {
                setPharmacyName(organization.getName());
            }

            // Extract  pharmacy phone
            // V1 DHDR FHIR Specification: organization.telecom[x}
            if (organization.getTelecom() != null && !organization.getTelecom().isEmpty() && organization.getTelecom().get(0) != null && organization.getTelecom().get(0).getValue() != null)
            {
                setPharmacyPhone(organization.getTelecom().get(0).getValue()); ;
            }
        }

    }

    private void mapPractitioner(Practitioner practitioner)
    {
        /**
         *  First we save the available data in temp variables, then set them based on identifier.system
         *  which indicates whether we're dealing with a pharmacist or a practitioner
         */
        String firstName = "";
        String lastName = "";
        String phone = "";
        String license = "";

        // Extract get practitioner name
        // V1 DHDR FHIR Specification: practitioner.name.given, practitioner.name.family
        if (practitioner.getName() != null && practitioner.getName().getGivenAsSingleString() != null && practitioner.getName().getFamilyAsSingleString() != null)
        {
            firstName = practitioner.getName().getGivenAsSingleString();
            lastName = practitioner.getName().getFamilyAsSingleString();
        }

        // Extract practitiner phone
        // V1 DHDR FHIR Specification: practitioner.telecom
        if (practitioner.getTelecom() != null)
        {
            phone = practitioner.getTelecom().toString();
        }

        // Extract practitioner ID
        // V1 DHDR FHIR Specification: practitioner.identifier[x].value
        if (practitioner.getIdentifier() != null && !practitioner.getIdentifier().isEmpty() && practitioner.getIdentifier().get(0) != null && practitioner.getIdentifier().get(0).getValue() != null)
        {
            license = practitioner.getIdentifier().get(0).getValue();
        }

        // Extract practitioner ID
        // V1 DHDR FHIR Specification: practitioner.identifier[x].value
        if (practitioner.getIdentifier() != null && !practitioner.getIdentifier().isEmpty() && practitioner.getIdentifier().get(0) != null && practitioner.getIdentifier().get(0).getSystem() != null)
        {
            // The identifier system code is either:
            // [id-system-global-base]/ca-on-license-physician for practitioner OR
            // [id-system-global-base]/ca-on-license-pharmacist for pharmacist
            if (practitioner.getIdentifier().get(0).getSystem().equals("https://ehealthontario.ca/API/FHIR/NamingSystem/ca-on-license-physician"))
            {
                setPrescriberName(firstName + " " + lastName);
                setPrescriberPhone(phone);
                setPrescriberId(license);
            }
            else if(practitioner.getIdentifier().get(0).getSystem().equals("https://ehealthontario.ca/API/FHIR/NamingSystem/ca-on-license-pharmacist"))
            {
                setPharmacistName(firstName + " " + lastName);
            }
        }
    }

    /**********************
     * Getter Functions
     *********************/
    public String getBrandName()
	{
        return brandName;
    }
    public String getGenericName()
    {
        return genericName;
    }
    public String getDispenseDate()
	{
        return dispenseDate;
    }
    public String getDaysSupply()
	{
        return daysSupply;
    }
    public String getPrescriberName()
	{
        return prescriberName;
    }
    public String getClassification()
    {
        return classification;
    }
    public String getSubClassification()
    {
        return subClassification;
    }
    public String getDaysSupplyLeft()
    {
        return daysSupplyLeft;
    }
    public String getStrength()
    {
        return strength;
    }
    public String getForm()
    {
        return form;
    }
    public String getQuantity()
    {
        return quantity;
    }
    public String getDispenseStatus()
    {
        return dispenseStatus;
    }
    public String getRefillsLeft()
    {
        return refillsLeft;
    }
    public String getExpirationDate()
    {
        return expirationDate;
    }
    public String getRequestStatus()
    {
        return requestStatus;
    }
    public String getDrugIdentificationNumber()
    {
        return drugIdentificationNumber;
    }
    public String getPrescriptionNumber()
    {
        return prescriptionNumber;
    }
    public String getReasonForUse()
    {
        return reasonForUse;
    }
    public String getDosageInstruction()
    {
        return dosageInstruction;
    }
    public String getPrescriberPhone()
    {
        return prescriberPhone;
    }
    public String getPrescriberId()
    {
        return prescriberId;
    }
    public String getPharmacyName()
    {
        return pharmacyName;
    }
    public String getPharmacyPhone()
    {
        return pharmacyPhone;
    }
    public String getPharmacistName()
    {
        return pharmacistName;
    }
    public ArrayList<String> getResponseCodes()
    {
        return responseCodes;
    }
    public ArrayList<String> getInterventionCodes()
    {
        return interventionCodes;
    }
    public ArrayList<String> getDurTextMessages()
    {
        return durTexts;
    }
    public int getDurTotal()
    {
        return durTotal;
    }
    public boolean isConsentGiven()
    {
        return isConsentGiven;
    }

    /**********************
     * Setter Functions
     *********************/
    public void setBrandName(String display)
	{
        brandName = display;
    }
    public void setGenericName(String genericName)
    {
        this.genericName = genericName;
    }
    public void setDispenseDate(String date)
	{
        dispenseDate = date;
    }
    public void setDaysSupply(String supply)
	{
        daysSupply = supply;
    }
    public void setPrescriberName(String prescriberName)
	{
        this.prescriberName = prescriberName;
    }
    public void setClassification(String classification)
    {
        this.classification = classification;
    }
    public void setSubClassification(String subClassification) { this.subClassification = subClassification; }
    public void setDaysSupplyLeft(String daysSupplyLeft)
    {
        this.daysSupplyLeft = daysSupplyLeft;
    }
    public void setStrength(String strength)
    {
        this.strength = strength;
    }
    public void setForm(String form)
    {
        this.form = form;
    }
    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }
    public void setDispenseStatus(String dispenseStatus)
    {
        this.dispenseStatus = dispenseStatus;
    }
    public void setRefillsLeft(String refillsLeft)
    {
        this.refillsLeft = refillsLeft;
    }
    public void setExpirationDate(String expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    public void setRequestStatus(String requestStatus)
    {
        this.requestStatus = requestStatus;
    }
    public void setDrugIdentificationNumber(String drugIdentificationNumber) { this.drugIdentificationNumber = drugIdentificationNumber; }
    public void setPrescriptionNumber(String prescriptionNumber) { this.prescriptionNumber = prescriptionNumber; }
    public void setReasonForUse(String reasonForUse)
    {
        this.reasonForUse = reasonForUse;
    }
    public void setDosageInstruction(String dosageInstruction) { this.dosageInstruction = dosageInstruction; }
    public void setPrescriberPhone(String prescriberPhone) { this.prescriberPhone = prescriberPhone; }
    public void setPrescriberId(String prescriberId)
    {
        this.prescriberId = prescriberId;
    }
    public void setPharmacyName(String pharmacyName)
    {
        this.pharmacyName = pharmacyName;
    }
    public void setPharmacyPhone(String pharmacyPhone)
    {
        this.pharmacyPhone = pharmacyPhone;
    }
    public void setPharmacistName(String pharmacistName)
    {
        this.pharmacistName = pharmacistName;
    }
    public void setResponseCodes(ArrayList<String> responseCodes) { this.responseCodes = responseCodes; }
    public void addResponseCode(String responseCode){this.responseCodes.add(responseCode);}
    public void setInterventionCodes(ArrayList<String> interventionCodes) { this.interventionCodes = interventionCodes; }
    public void addInterventionCode(String interventionCode){this.interventionCodes.add(interventionCode);}
    public void setDurTextMessages(ArrayList<String> durTexts)
    {
        this.durTexts = durTexts;
    }
    public void addDurTextMessage(String durText){this.durTexts.add(durText);}
    public void setDurTotal(int durTotal)
    {
        this.durTotal = durTotal;
    }
    public void setConsentGiven(boolean consentGiven)
    {
        isConsentGiven = consentGiven;
    }

    public String getConcatedDURs()
    {
        StringBuilder concatedString = new StringBuilder();

        for (String durResponseCode : getResponseCodes())
        {
            concatedString.append(durResponseCode).append(" | ");
        }

        return concatedString.toString();
    }

    /***************************************************************************************************************************
     * Parcelable implementation code goes under here
     **************************************************************************************************************************/

    /**
     * Constructor when a parcel object is being given for the Parcelable implementation.
     * the in.XXXX() statements must occur in the same order as the writeXXXX() statements in writeToParcel().
     * @param in the parcel coming in
     *
     */
    private DHDRMedicationDispenseModel(Parcel in)
    {
        setBrandName(in.readString());
        setGenericName(in.readString());
        setDispenseDate(in.readString());
        setDaysSupply(in.readString());
        setPrescriberName(in.readString());
        setClassification(in.readString());
        setSubClassification(in.readString());
        setStrength(in.readString());
        setForm(in.readString());
        setQuantity(in.readString());
        setDaysSupplyLeft(in.readString());
        setDispenseStatus(in.readString());
        setRefillsLeft(in.readString());
        setExpirationDate(in.readString());
        setRequestStatus(in.readString());
        setDrugIdentificationNumber(in.readString());
        setPrescriptionNumber(in.readString());
        setReasonForUse(in.readString());
        setDosageInstruction(in.readString());
        setPrescriberPhone(in.readString());
        setPrescriberId(in.readString());
        setPharmacyName(in.readString());
        setPharmacyPhone(in.readString());
        setPharmacistName(in.readString());
        setResponseCodes((ArrayList<String>) in.readSerializable());
        setInterventionCodes((ArrayList<String>) in.readSerializable());
        setDurTextMessages((ArrayList<String>) in.readSerializable());
        setDurTotal(in.readInt());
        setConsentGiven(in.readByte() != 0);
    }

    /**
     * Auto generated method stub required by parcelable
     */
    public static final Creator<DHDRMedicationDispenseModel> CREATOR = new Creator<DHDRMedicationDispenseModel>()
    {
        @Override
        public DHDRMedicationDispenseModel createFromParcel(Parcel in)
        {
            return new DHDRMedicationDispenseModel(in);
        }

        @Override
        public DHDRMedicationDispenseModel[] newArray(int size)
        {
            return new DHDRMedicationDispenseModel[size];
        }
    };

    /**
     * Auto generated method stub
     * @return
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Auto generated method stub (with added getters)
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(getBrandName());
        dest.writeString(getGenericName());
        dest.writeString(getDispenseDate());
        dest.writeString(getDaysSupply());
        dest.writeString(getPrescriberName());
        dest.writeString(getClassification());
        dest.writeString(getSubClassification());
        dest.writeString(getStrength());
        dest.writeString(getForm());
        dest.writeString(getQuantity());
        dest.writeString(getDaysSupplyLeft());
        dest.writeString(getDispenseStatus());
        dest.writeString(getRefillsLeft());
        dest.writeString(getExpirationDate());
        dest.writeString(getRequestStatus());
        dest.writeString(getDrugIdentificationNumber());
        dest.writeString(getPrescriptionNumber());
        dest.writeString(getReasonForUse());
        dest.writeString(getDosageInstruction());
        dest.writeString(getPrescriberPhone());
        dest.writeString(getPrescriberId());
        dest.writeString(getPharmacyName());
        dest.writeString(getPharmacyPhone());
        dest.writeString(getPharmacistName());
        dest.writeSerializable(getResponseCodes());
        dest.writeSerializable(getInterventionCodes());
        dest.writeSerializable(getDurTextMessages());
        dest.writeInt(getDurTotal());
        dest.writeByte((byte) (isConsentGiven() ? 1 : 0));
    }

    /**
     * This method will take in a date and subtract X amount of days from it.
     * Returns the resulting days left.
     * Uses the JodaTime library for easy calculating.
     * @return
     */
    private String calculateDaysSupplyLeft(String dispenseDate, String daysSupply)
    {
        int daysPassedSinceDispense;
        int daysSupplyLeft;

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-YYYY");
        DateTime today = new DateTime();
        DateTime dispenseDateTime = dateTimeFormatter.parseDateTime(dispenseDate);

        daysPassedSinceDispense = Days.daysBetween(dispenseDateTime.toLocalDate(), today.toLocalDate()).getDays();
        daysSupplyLeft = Integer.parseInt(daysSupply) - daysPassedSinceDispense;

        // set to 0 if patient has passed the days supply estimate (negative numbers won't make any sense)
        if (daysSupplyLeft < 0)
        {
            daysSupplyLeft = 0;
        }

        return Integer.toString(daysSupplyLeft);
    }
}
