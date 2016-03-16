package id.co.pln.simapro;

/**
 * Created by Rezpa Aditya on 3/15/2016.
 */
public class Unit {
    private String ID_UNIT;
    private String NM_UNIT;

    private String GRP_UNIT;
    private String ID_COCD;
    private String ID_BUSARE;
    private String ID_PARE;
    private String ID_PSUBARE;
    private String NM_COCD;

    public String getID_UNIT() {
        return ID_UNIT;
    }

    public void setID_UNIT(String ID_UNIT) {
        this.ID_UNIT = ID_UNIT;
    }

    public String getNM_UNIT() {
        return NM_UNIT;
    }

    public void setNM_UNIT(String NM_UNIT) {
        this.NM_UNIT = NM_UNIT;
    }

    public String getGRP_UNIT() {
        return GRP_UNIT;
    }

    public void setGRP_UNIT(String GRP_UNIT) {
        this.GRP_UNIT = GRP_UNIT;
    }

    public String getID_COCD() {
        return ID_COCD;
    }

    public void setID_COCD(String ID_COCD) {
        this.ID_COCD = ID_COCD;
    }

    public String getID_BUSARE() {
        return ID_BUSARE;
    }

    public void setID_BUSARE(String ID_BUSARE) {
        this.ID_BUSARE = ID_BUSARE;
    }

    public String getID_PARE() {
        return ID_PARE;
    }

    public void setID_PARE(String ID_PARE) {
        this.ID_PARE = ID_PARE;
    }

    public String getID_PSUBARE() {
        return ID_PSUBARE;
    }

    public void setID_PSUBARE(String ID_PSUBARE) {
        this.ID_PSUBARE = ID_PSUBARE;
    }

    public String getNM_COCD() {
        return NM_COCD;
    }

    public void setNM_COCD(String NM_COCD) {
        this.NM_COCD = NM_COCD;
    }

    @Override
    public String toString()
    {
        return( ID_UNIT + " " + NM_UNIT);
    }
}
