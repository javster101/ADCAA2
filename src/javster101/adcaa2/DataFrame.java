package javster101.adcaa2;

import com.opengg.core.math.FastMath;

public class DataFrame {
    public float time;

    public DataElement cm = new DataElement(DataElement.Part.CM);
    public DataElement las = new DataElement(DataElement.Part.LAS);
    public DataElement booster = new DataElement(DataElement.Part.BOOSTER);

    public int engineFlag;
    public int reserved;

    @Override
    public String toString() {
        return "DataFrame{" +
                "time=" + time +
                ", cm=" + cm +
                ", las=" + las +
                ", booster=" + booster +
                ", engineFlag=" + engineFlag +
                ", reserved=" + reserved +
                '}' + "\n";
    }

    public static DataFrame interpolate(DataFrame f1, DataFrame f2, float amount){
        var newF = new DataFrame();

        newF.time = FastMath.lerp(f1.time, f2.time, amount);
        newF.engineFlag = Math.max(f1.engineFlag, f2.engineFlag);
        newF.reserved = Math.max(f1.reserved, f2.reserved);

        newF.cm = DataElement.interpolate(f1.cm, f2.cm, amount);
        newF.las = DataElement.interpolate(f1.las, f2.las, amount);
        newF.booster = DataElement.interpolate(f1.booster, f2.booster, amount);

        return newF;

    }
}
