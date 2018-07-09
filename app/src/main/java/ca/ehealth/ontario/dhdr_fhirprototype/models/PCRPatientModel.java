package ca.ehealth.ontario.dhdr_fhirprototype.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PCRPatientModel implements Parcelable
{
    private String name = "n/a";
    private String gender = "n/a";
    private String dateOfBirth = "n/a";
    private String healthCardNumber = "n/a";
    private int dispenseTotal = 0;
    private int durTotal = 0;
    private boolean isConsentGiven = true;
    
    public PCRPatientModel()
	{
        // use default values
    }

    // constructor only for basic data (name, gender, date of birth)
    public PCRPatientModel(String name, String gender, String dateOfBirth, String healthCardNumber)
	{
        setName(name);
        setGender(gender);
        setDateOfBirth(dateOfBirth);
        setHealthCardNumber(healthCardNumber);
    }

    // constructor for more complete data (for the practitioner view) includes everything, including dispenseTotal, durTotal, and consent directive
    public PCRPatientModel(String name, String gender, String dateOfBirth, int dispenseTotal, int durTotal, boolean isConsentGiven, String healthCardNumber)
    {
        setName(name);
        setGender(gender);
        setDateOfBirth(dateOfBirth);
        setDispenseTotal(dispenseTotal);
        setDurTotal(durTotal);
        setConsentGiven(isConsentGiven);
        setHealthCardNumber(healthCardNumber);
    }

    @Override
    public String toString()
    {
        return name;
    }

    //getters
    public String getName()
	{
        return name;
    }
    public String getGender()
	{
        return gender;
    }
    public String getDateOfBirth()
	{
        return dateOfBirth;
    }
    public int getDispenseTotal()
    {
        return dispenseTotal;
    }
    public int getDurTotal()
    {
        return durTotal;
    }
    public boolean getConsentGiven()
    {
        return isConsentGiven;
    }
    public String getHealthCardNumber()
    {
        return healthCardNumber;
    }

    // setters
    public void setName(String firstName)
	{
        this.name = firstName;
    }
    public void setGender(String gender)
	{
        this.gender = gender;
    }
    public void setDateOfBirth(String dateOfBirth)
	{
        this.dateOfBirth = dateOfBirth;
    }
    public void setDispenseTotal(int dispenseTotal)
    {
        this.dispenseTotal = dispenseTotal;
    }
    public void setDurTotal(int durTotal)
    {
        this.durTotal = durTotal;
    }
    public void setConsentGiven(boolean isConsentGiven)
    {
        this.isConsentGiven = isConsentGiven;
    }
    public void setHealthCardNumber(String healthCardNumber) { this.healthCardNumber = healthCardNumber; }

    /***************************************************************************************************************************
     * Parcelable stuff goes under here
     **************************************************************************************************************************/

    /**
     * Constructor when a parcel object is being given for the Parcelable implementation.
     * @param in the parcel coming in
     */
    public PCRPatientModel(Parcel in)
    {
        setName(in.readString());
        setGender(in.readString());
        setDateOfBirth(in.readString());
        setDispenseTotal(in.readInt());
        setDurTotal(in.readInt());
        setConsentGiven(in.readByte() != 0);
        setHealthCardNumber(in.readString());
    }

    /**
     * Required by parcelable
     */
    public static final Creator<PCRPatientModel> CREATOR = new Creator<PCRPatientModel>()
    {
        @Override
        public PCRPatientModel createFromParcel(Parcel in)
        {
            return new PCRPatientModel(in);
        }

        @Override
        public PCRPatientModel[] newArray(int size)
        {
            return new PCRPatientModel[size];
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
        dest.writeString(getName());
        dest.writeString(getGender());
        dest.writeString(getDateOfBirth());
        dest.writeInt(getDispenseTotal());
        dest.writeInt(getDurTotal());
        dest.writeByte((byte) (getConsentGiven() ? 1 : 0));
        dest.writeString(getHealthCardNumber());
    }
}