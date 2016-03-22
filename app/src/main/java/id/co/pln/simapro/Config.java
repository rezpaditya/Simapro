package id.co.pln.simapro;

/**
 * Created by Rezpa Aditya on 3/20/2016.
 */
public class Config {
    public static final String HOSTNAME = "http://10.1.36.239:8008/simapro2/";

    //method get
    public static final String GET_ALL_UNIT = HOSTNAME+ "get_all_unit.php";
    public static final String GET_AREA_BY_UNIT = HOSTNAME+ "get_area_by_unit.php/?id_unit=";
    public static final String GET_ALL_LOKASI = HOSTNAME + "get_all_lokasi.php";
    public static final String GET_ALL_PROVINSI = HOSTNAME + "get_all_propinsi.php";
    public static final String GET_ALL_AREA = HOSTNAME + "get_all_area.php";

    //method post
    public static final String CEKLOGIN = HOSTNAME+ "cek_login.php";
    public static final String INPUT_LOKASI = HOSTNAME + "input_lokasi.php";
    public static final String INPUT_TANAH = HOSTNAME + "input_tanah.php";

}
