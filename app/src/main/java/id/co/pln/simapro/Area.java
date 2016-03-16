package id.co.pln.simapro;

/**
 * Created by Rezpa Aditya on 3/16/2016.
 */
public class Area {
    String ID_BURSARE_UNITP;
    String NM_UNITP;

    public String getID_BURSARE_UNITP() {
        return ID_BURSARE_UNITP;
    }

    public void setID_BURSARE_UNITP(String ID_BURSARE_UNITP) {
        this.ID_BURSARE_UNITP = ID_BURSARE_UNITP;
    }

    public String getNM_UNITP() {
        return NM_UNITP;
    }

    public void setNM_UNITP(String NM_UNITP) {
        this.NM_UNITP = NM_UNITP;
    }

    @Override
    public String toString()
    {
        return(NM_UNITP);
    }
}
