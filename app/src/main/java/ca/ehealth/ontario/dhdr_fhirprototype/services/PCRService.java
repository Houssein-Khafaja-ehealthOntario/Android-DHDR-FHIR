package ca.ehealth.ontario.dhdr_fhirprototype.services;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ca.ehealth.ontario.dhdr_fhirprototype.models.PCRPatientModel;

public class PCRService
{
    private static String pcrEndPoint = "http://lite.innovation-lab.ca:8080/on";
    private static final String senderId = "your unique identifier";

    public PCRService()
    {


    }

    /**
     * This method takes in a health card number to query the PCR repository.
     * Uses the Get Client Demographics Query-IN101101CA Interaction:
     * https://www.innovation-lab.ca/get-client-demographics-query/
     * @param healthCardNumber patient health card number to be used in the query search parameter
     * @return an HttpResponse object in XML format
     */
    public static HttpResponse executeQuery(String healthCardNumber)
    {
        // get creation date with proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyyMMddkkmmssZ", Locale.CANADA);
        String creationDateTime = logDateFormat.format(calendar.getTime());

        String xmlRequest = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                " <S:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\n" +
                "  <wsa:Action>urn:hl7-org:v3:PRPA_IN101101CA.MR200903</wsa:Action>\n" +
                "  <wsa:ReplyTo>\n" +
                "   <wsa:Address>http://schemas.xmlsoap.org/ws/2005/08/addressing/role/anonymous</wsa:Address>\n" +
                "  </wsa:ReplyTo>\n" +
                "  <wsa:MessageID>" + UUID.randomUUID() + "</wsa:MessageID>\n" +
                "  <wsa:To>www.example.com</wsa:To>\n" +
                " </S:Header>\n" +
                " <S:Body>\n" +
                "  <PRPA_IN101101CA ITSVersion=\"XML_1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n" +
                "   <realmCode code=\"CA\" />\n" +
                "   <id specializationType=\"II.TOKEN\" root=\"7ABC2FAA-3C63-4C93-8CCE-E06AE051E579\" />\n" +
                "   <creationTime specializationType=\"TS.FULLDATETIME\" value=\"" + creationDateTime + "\" />\n" +
                "   <responseModeCode code=\"I\" />\n" +
                "   <versionCode code=\"V3-2008N\" />\n" +
                "   <interactionId specializationType=\"II.PUBLIC\" root=\"2.16.840.1.113883.1.6\" extension=\"PRPA_IN101101CA\" />\n" +
                "   <profileId root=\"2.16.840.1.113883.2.20.2\" extension=\"R02.04.03\" />\n" +
                "   <profileId root=\"2.16.840.1.113883.3.239.7\" extension=\"V03.00\" />\n" +
                "   <processingCode code=\"P\" />\n" +
                "   <processingModeCode code=\"T\" />\n" +
                "   <acceptAckCode code=\"NE\" />\n" +
                "   <receiver typeCode=\"RCV\">\n" +
                "    <telecom specializationType=\"TEL.URI\" value=\"http://142.222.17.102:8080/on\" />\n" +
                "    <device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
                "     <id specializationType=\"II.BUS\" root=\"2.16.840.1.113883.3.239.2\" use=\"BUS\" />\n" +
                "     <name mediaType=\"text/plain\" representation=\"TXT\">eHealth Ontario Integration Facility</name>\n" +
                "     <agent classCode=\"AGNT\">\n" +
                "      <agentOrganization classCode=\"ORG\" determinerCode=\"INSTANCE\">\n" +
                "       <id specializationType=\"II.BUS\" root=\"2.16.840.1.113883.3.239\" use=\"BUS\" />\n" +
                "      </agentOrganization>\n" +
                "     </agent>\n" +
                "    </device>\n" +
                "   </receiver>\n" +
                "   <sender typeCode=\"SND\">\n" +
                "    <telecom specializationType=\"TEL.URI\" value=\"http://innovation-lab.ca\" />\n" +
                "    <device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
                "     <id specializationType=\"II.BUS\" root=\"2.16.840.1.113883.3.239.4\" extension=\"" + senderId + "\" use=\"BUS\" />\n" +
                "     <name mediaType=\"text/plain\" representation=\"TXT\">eHealth Ontario Portal Application</name>\n" +
                "     <desc mediaType=\"text/plain\" representation=\"TXT\">I hereby accept the service agreement here: https://innovation-lab.ca/media/1147/innovation-lab-terms-of-use.pdf</desc>\n" +
                "    </device>\n" +
                "   </sender>\n" +
                "   <controlActEvent classCode=\"CACT\" moodCode=\"EVN\">\n" +
                "    <id specializationType=\"II.BUS\" root=\"2.16.840.1.113883.19.3.207.15.1.1\" extension=\"52379653\" use=\"BUS\" />\n" +
                "    <code code=\"PRPA_TE101101CA\" codeSystem=\"2.16.840.1.113883.1.18\" />\n" +
                "    <statusCode code=\"completed\" />\n" +
                "    <effectiveTime>\n" +
                "     <low value=\"" + creationDateTime + "\" />\n" +
                "    </effectiveTime>\n" +
                "    <reasonCode code=\"PATCAR\" codeSystem=\"2.16.840.1.113883.11.14878\" />\n" +
                "    <author typeCode=\"AUT\" contextControlCode=\"AP\">\n" +
                "     <time specializationType=\"TS.FULLDATETIME\" value=\"" + creationDateTime + "\" />\n" +
                "     <assignedEntity1 classCode=\"ASSIGNED\">\n" +
                "      <id root=\"1.1.1.4\" extension=\"5763665838\" displayable=\"true\" use=\"BUS\" />\n" +
                "      <assignedPerson classCode=\"PSN\" determinerCode=\"INSTANCE\">\n" +
                "       <name specializationType=\"PN.BASIC\">\n" +
                "        <family partType=\"FAM\">User</family>\n" +
                "        <given partType=\"GIV\">Test</given>\n" +
                "       </name>\n" +
                "      </assignedPerson>\n" +
                "      <representedOrganization classCode=\"ORG\" determinerCode=\"INSTANCE\">\n" +
                "       <id specializationType=\"II.PUBLIC\" root=\"1.1.1.5\" extension=\"123456789012\" displayable=\"true\" />\n" +
                "       <name mediaType=\"text/plain\" representation=\"TXT\">Mohawk College Hospital</name>\n" +
                "      </representedOrganization>\n" +
                "     </assignedEntity1>\n" +
                "    </author>\n" +
                "    <queryByParameter>\n" +
                "     <queryId specializationType=\"II.TOKEN\" root=\"2AA0C0DE-26F5-4F65-A102-F16A9479CC17\" />\n" +
                "     <initialQuantity specializationType=\"INT.POS\" value=\"10\" />\n" +
                "     <parameterList>\n" +
                "      <clientIDPub>\n" +
                "       <value specializationType=\"II.PUBLIC\" root=\"2.16.840.1.113883.4.59\" extension=\"" + healthCardNumber + "\" />\n" +
                "      </clientIDPub>\n" +
                "     </parameterList>\n" +
                "    </queryByParameter>\n" +
                "   </controlActEvent>\n" +
                "  </PRPA_IN101101CA>\n" +
                " </S:Body>\n" +
                "</S:Envelope>";

        HttpResponse httpResponse = null;

        try
        {
            // Initialize http stuff and set headers
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(pcrEndPoint);
            StringEntity stringEntity = new StringEntity(xmlRequest, HTTP.UTF_8);
            stringEntity.setContentType("text/xml");
            httpPost.addHeader("SOAPAction", pcrEndPoint);
            httpPost.setEntity(stringEntity);

            // make the http request
            httpResponse = httpClient.execute(httpPost);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return httpResponse;
    }

    /**
     * This method takes an HttpResponse object and parses out the xml.
     * Will extract:
     * -givenName
     * -familyName
     * -birthTime
     * -gender
     *
     * @param xmlResponse the HttpResponse object with XML data
     * @return our own PCRPatientModel with extracted data
     */
    public static PCRPatientModel parseHttpResponse(HttpResponse xmlResponse)
    {
        PCRPatientModel newPCRPatient = new PCRPatientModel();
        XmlPullParser xmlPullParser = null;
        int xmlEvent = 0;


        try
        {
            // setup for xml parsing
            HttpEntity httpResponseEntity = xmlResponse.getEntity();
            InputStream inputStream = httpResponseEntity.getContent();
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlPullParser = xmlFactoryObject.newPullParser();
            xmlPullParser.setInput(inputStream, null);
            xmlEvent = xmlPullParser.getEventType();

            String givenName = "";
            String familyName = "";
            String birthTime = "";
            String gender = ""; // administrativeGenderCode will be checked to generate gender

            // loop through xml and parse out data
            while (xmlEvent != XmlPullParser.END_DOCUMENT)
            {
                String tagName = xmlPullParser.getName();

                if (xmlEvent == XmlPullParser.START_TAG)
                {
                    switch (tagName)
                    {
                        case "hl7:given":
                            xmlEvent = xmlPullParser.next(); // move into the value position
                            if (xmlPullParser.getText() != null)
                            {
                                givenName = xmlPullParser.getText();
                            }
                            break;

                        case "hl7:family":
                            xmlEvent = xmlPullParser.next(); // move into the value position
                            if (xmlPullParser.getText() != null)
                            {
                                familyName = xmlPullParser.getText();
                            }
                            break;

                        case "hl7:birthTime":
                            // format pattern that is returned by PCR: 19940115(yyyyMMdd)
                            SimpleDateFormat formatReceived = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
                            // parse out the Date object from the dispense date using the format object
                            Date birthDate = formatReceived.parse(xmlPullParser.getAttributeValue(null, "value"));
                            // apply a format like: 05-Feb-1987 to the string
                            formatReceived.applyPattern("dd-MMM-YYYY");
                            // finally, we can save the date with new format applied
                            birthTime = formatReceived.format(birthDate);
                            break;

                        case "hl7:administrativeGenderCode":
                            String genderCode = xmlPullParser.getAttributeValue(null, "code");
                            if (genderCode.toLowerCase().equals("f"))
                            {
                                gender = "Female";
                            }
                            else if (genderCode.toLowerCase().equals("m"))
                            {
                                gender = "Male";
                            }
                            break;

                        case "hl7:text":
                            xmlEvent = xmlPullParser.next(); // move into the value position
                            if (xmlPullParser.getText() != null && xmlPullParser.getText().equals("no member(s) found."))
                            {
                                return new PCRPatientModel();
                            }
                            break;
                    }
                }

                xmlEvent = xmlPullParser.next();
            }

            newPCRPatient.setName(givenName + " " + familyName);
            newPCRPatient.setGender(gender);
            newPCRPatient.setDateOfBirth(birthTime);
        }
        catch (Exception e)
        {
            //System.out.println(e.toString());
            Log.e("httpRequestException", e.toString());
        }

        return newPCRPatient;
    }
}
