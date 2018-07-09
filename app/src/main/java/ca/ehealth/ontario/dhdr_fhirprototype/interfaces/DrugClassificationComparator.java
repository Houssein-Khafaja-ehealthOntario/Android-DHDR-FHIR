package ca.ehealth.ontario.dhdr_fhirprototype.interfaces;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

import ca.ehealth.ontario.dhdr_fhirprototype.models.DHDRMedicationDispenseModel;

public class DrugClassificationComparator implements Comparator<DHDRMedicationDispenseModel>
{
    @Override
    public int compare(DHDRMedicationDispenseModel t1, DHDRMedicationDispenseModel t2)
    {
        int sortResult = t1.getClassification().compareToIgnoreCase(t2.getClassification());

        // if both are of the same class, then compare dispenseDates
        // we want to sort by latest dispense at the top/beginning of a list
        if (sortResult == 0)
        {
            DateTimeFormatter format = DateTimeFormat.forPattern("dd-MMM-yyyy");
            DateTime t1DateTime = format.parseDateTime(t1.getDispenseDate());
            DateTime t2DateTime = format.parseDateTime(t2.getDispenseDate());

            if (t1DateTime.isAfter(t2DateTime))
            {
                sortResult = 1;
            }
            else if(t1DateTime.isBefore(t2DateTime))
            {
                sortResult = -1;
            }
            else
            {
                sortResult = 0;
            }
        }

        return sortResult;
    }
}
