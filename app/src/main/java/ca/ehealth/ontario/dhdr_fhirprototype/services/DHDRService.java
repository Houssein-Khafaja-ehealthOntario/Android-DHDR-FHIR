/*
    *** Authorship Notice ***
    This file was copy/pasted from the FHIR North 2018 Java project which was posted on Innovation Lab's Code Sharing repository.

    I have made changes to this file to satisfy the particular requirements of the project.

    Link to the Innovation Lab Repo: https://www.innovation-lab.ca/repository/ViewRepository?id=1911d245-5c6c-4156-bd7d-2f0701d6ce28
    Link to the GitHub: https://github.com/EHO-Innovation-Lab/FHIRNorthJava

    Compliments to the author, Dawne Pierce from Ideaworks MEDIC (from Mohawk College)
 */

package ca.ehealth.ontario.dhdr_fhirprototype.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;

public class DHDRService
{
    private final String endPointBase = "http://lite.innovation-lab.ca:9443/dispense-service";
    private final String medicationDispenseSearchURL = "MedicationDispense?patient:patient.identifier=https://ehealthontario.ca/API/FHIR/NamingSystem/ca-on-patient-hcn|";
    private final String senderId = "your unique identifier";
    static private final FhirContext fhirContext = FhirContext.forDstu2();
    private IGenericClient client;

    /**
     * Initializes FHIR client,
     * Generates pin for Immunization_Context, sets required HTTP headers and registers headers to client
     * ****SENDER ID MUST BE REPLACED WITH YOUR UNIQUE SENDER ID, FOUND AT https://www.innovation-lab.ca/Test-Portal****
     */
    DHDRService()
    {
        client = fhirContext.newRestfulGenericClient(endPointBase);

        // The HAPI FHIR library sends an initial metadata query for validation any time a client preforms a query.
        // In order to query DHIR/DHDR, this must be disabled on the client:
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        AdditionalRequestHeadersInterceptor headers = new AdditionalRequestHeadersInterceptor();
        headers.addHeaderValue("X-Sender-Id", senderId);
        headers.addHeaderValue("X-License-Text", "I hereby accept the service agreement here: https://innovation-lab.ca/media/1147/innovation-lab-terms-of-use.pdf");
        headers.addHeaderValue("ClientTxID", UUID.randomUUID().toString());

        client.registerInterceptor(headers);
    }

    /**
     * Performs a basic GET operation, querying the DHDR server by a single HCN.
     * This version of the method will query the last 120 days
     * @param healthCardNumber Ontario Immunization ID
     * @return returns the query results
     */
    public Bundle executeQuery(String healthCardNumber)
    {
        //default range should be last 120 days by default if no handedOver param is given, but we're supplying it anyway just to make sure
        String defaultDateRange = getDate(-120);

        return client.search()
                .byUrl(buildQueryUrl(healthCardNumber, defaultDateRange, null, false))
                .returnBundle(Bundle.class)
                .execute();
    }

    /**
     * Performs a basic GET operation, querying the DHDR server by a single HCN.
     * This version of the method will query with the specified date range (inclusive). So it will be between >= start date and <= end date.
     * @param healthCardNumber of the patient to be queried
     * @param startDate is the value after: whenHandedOver=ge in our query uri
     * @param endDate is the value after: whenHandedOver=le in our query uri
     * @return returns the query results
     */
    public Bundle executeQuery(String healthCardNumber, String startDate, String endDate)
    {
        return client.search()
                .byUrl(buildQueryUrl(healthCardNumber, startDate, endDate, false))
                .returnBundle(Bundle.class)
                .execute();
    }

    /**
     * Performs a basic SUMMARY query, querying with a single HCN.
     * This version of the method will query the last 120 days, and only receives a count of the medication dispenses.
     *
     * This method is not used by this application because it currently does not return all the data we want to show,
     * such as total DURS and consent info. Only useful if you only need dispense total.
     * @param healthCardNumber Ontario Immunization ID
     * @return returns the query results
     */
    public Bundle executeSummaryQuery(String healthCardNumber)
    {
        String defaultDateRange = getDate(-120);

        return client.search()
                .byUrl(buildQueryUrl(healthCardNumber, defaultDateRange, null, true))
                .returnBundle(Bundle.class)
                .execute();
    }

    /**
     * This method will take in query parameters and build a query string for the .search().byUrl() method.
     */
    private String buildQueryUrl(String healthCardNumber, String startDate, String endDate, boolean useSummary)
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(medicationDispenseSearchURL).append(healthCardNumber);

        if (startDate != null && !"".equals(startDate))
        {
            stringBuilder.append("&whenHandedOver=ge").append(startDate);
        }

        if (endDate != null && !"".equals(endDate))
        {
            stringBuilder.append("&whenHandedOver=le").append(endDate);
        }

        if (useSummary)
        {
            stringBuilder.append("&_summary=count");
        }

        stringBuilder.append("&_format=application/fhir+json");

        return stringBuilder.toString();
    }

    /**
     * This method returns today's date minus the number of days supplied as a parameter.
     * The date is generated using the Calendar and SimpleDateFormat objects.
     * @param dayCount the number of days to count backwards from today.
     * @return a date string in the format: 2018-02-24(yyyy-MM-dd)
     */
    private String getDate(int dayCount)
    {
        //date to return
        String myDate;

        // get today's date as a calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // get today's date minus 120 days
        calendar.add(Calendar.DAY_OF_MONTH, dayCount);

        // get the date format: e.g. 2018-02-24
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

        // apply date format with new calendar and set as the start query date
        myDate = dateFormat.format(calendar.getTime());

        return myDate;
    }

    /**
     * Example of how to convert a FHIR resource into a raw JSON string
     * @param resource resource to parse
     * @return raw JSON representation of Immunization object
     */
    public String parse(MedicationDispense resource)
    {
        return fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    /**
     * Example of how to convert a raw JSON string into a FHIR object
     * @param jsonResource resource to parse
     * @return FHIR object parsed from raw JSON
     */
    public MedicationDispense parse(String jsonResource)
    {
        return fhirContext.newJsonParser().parseResource(MedicationDispense.class, jsonResource);
    }

    /**
     * Example of how to convert a fhir.model.dstu2.resource.Bundle to a String
     * @param dataBundle a FHIR bundle representing a patient with dispensed medication
     * @return the dataBundle encoded as a String
     */
    static public String BundleToString (Bundle dataBundle)
    {
        return fhirContext.newJsonParser().encodeResourceToString(dataBundle);
    }

    /**
     * Example of how to convert a String to a fhir.model.dstu2.resource.Bundle
     * @param dataString the dataBundle encoded as a String
     * @return a FHIR bundle representing a patient with dispensed medication
     */
    static public Bundle StringToBundle (String dataString)
    {
        return (Bundle) fhirContext.newJsonParser().parseResource(dataString);
    }
}