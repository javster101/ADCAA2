package javster101.adcaa2;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3big;
import com.opengg.core.math.Vector3f;
import javster101.adcaa2.components.FlightManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataElement {
    public Part part;

    public double altitude, latitude, longitude;
    public Quaternionf rotation;

    public DataElement(Part part){
        this.part = part;
    }

    enum Part{
        CM, LAS, BOOSTER
    }

    @Override
    public String toString() {
        return "DataElement{" +
                "part=" + part +
                ", altitude=" + altitude +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rotation=" + rotation +
                '}';
    }

    public static DataElement interpolate(DataElement d1, DataElement d2, float amount){
        var newE = new DataElement(d1.part);
        newE.rotation = d1.rotation;
        newE.longitude = d1.longitude + ((d2.longitude-d1.longitude)*amount);
        newE.latitude = d1.latitude + ((d2.latitude-d1.latitude)*amount);
        newE.altitude = d1.altitude + ((d2.altitude-d1.altitude)*amount);


        return newE;
    }
    
    public Vector3big toPosition(){
        var direction = new Quaternionf(new Vector3f(0, (float) (-longitude) * FastMath.radiansToDegrees, (float) latitude * FastMath.radiansToDegrees)).divide(FlightManager.currentManager.naturalRotation).transform(new Vector3f(0,0,1));
        var realAlt = new BigDecimal(6_371_000).add(new BigDecimal(altitude).multiply(new BigDecimal(0.3048)));
        var result = new Vector3big(direction).multiply(realAlt.multiply(new BigDecimal(FlightManager.scaleMultiplier)));
        /*
        System.out.println();
        System.out.println(altitude);
        System.out.println(new BigDecimal(altitude).multiply(new BigDecimal(0.3048)));
        System.out.println(realAlt);
        System.out.println(result.length().divide(new BigDecimal(FlightManager.scaleMultiplier), RoundingMode.HALF_DOWN));
        System.out.println(realAlt.subtract(result.length().divide(new BigDecimal(FlightManager.scaleMultiplier), RoundingMode.HALF_DOWN)));
        */return result;
    }
}
