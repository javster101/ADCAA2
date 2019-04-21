package javster101.adcaa2;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

public class DataElement {
    public Part part;

    public float altitude, latitude, longitude;
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

        if(Float.isNaN(amount))
            amount = 0;

        newE.altitude = FastMath.lerp(d1.altitude, d2.altitude, amount);
        newE.latitude = FastMath.lerpAngle(d1.latitude, d2.latitude, amount);
        newE.longitude = FastMath.lerpAngle(d1.longitude, d2.longitude, amount);
        newE.rotation = Quaternionf.slerp(d1.rotation, d2.rotation, amount);

        return newE;
    }
    
    public Vector3f toPosition(){
        var x = FastMath.cos(latitude) * FastMath.cos(longitude);
        var z = FastMath.cos(latitude) * FastMath.sin(longitude);
        var y = FastMath.sin(latitude);
        return new Vector3f(x, y, z).multiply(6_371_000 + (altitude * 0.3048f)).multiply(0.01f);
    }
}
