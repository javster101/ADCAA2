package javster101.adcaa2;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3big;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import javster101.adcaa2.components.FlightManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

public class DataParser {
    public static List<DataFrame> parse(String file){
        try {
            var data = Files.lines(Paths.get(file))
                    .skip(1)
                    .filter(not(String::isEmpty))
                    .map(DataParser::parseLine)
                    .collect(Collectors.toList());
            return data;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<DataFrame> parse(List<String> lines){
        return lines.stream().map(DataParser::parseLine).collect(Collectors.toList());
    }

    public static DataFrame parseFromPacket(GGInputStream in, float time) throws IOException {
        var frame = new DataFrame();
        frame.time = time;
        frame.cm.altitude = in.readDouble();
        frame.cm.latitude = in.readDouble();
        frame.cm.longitude = in.readDouble();

        frame.las.altitude = in.readDouble();
        frame.las.latitude = in.readDouble();
        frame.las.longitude = in.readDouble();

        frame.booster.altitude = in.readDouble();
        frame.booster.latitude = in.readDouble();
        frame.booster.longitude = in.readDouble();

        frame.cm.rotation = new Quaternionf(
                (float)in.readDouble(), (float)in.readDouble(), (float)in.readDouble(), (float)in.readDouble()
        );

        frame.las.rotation = new Quaternionf(
                (float)in.readDouble(), (float)in.readDouble(), (float)in.readDouble(), (float)in.readDouble()
        );

        frame.booster.rotation = new Quaternionf(
                (float)in.readDouble(), (float)in.readDouble(), (float)in.readDouble(), (float)in.readDouble()
        );

        frame.engineFlag = in.readInt();
        return frame;
    }

    public static DataFrame parseLine(String line){
        //time (sec), cm alt, lat, long, las alt, lat, long, booster alt, lat, long, cm quat(wxyz) las quat, booster quat, engine flag, reserved
        var sections = line.split(",");
        DataFrame frame = new DataFrame();

        frame.time =  Float.parseFloat(sections[0]);

        frame.cm.altitude = Double.parseDouble(sections[1]);
        frame.cm.latitude = Double.parseDouble(sections[2]);
        frame.cm.longitude = Double.parseDouble(sections[3]);

        frame.las.altitude = Double.parseDouble(sections[4]);
        frame.las.latitude = Double.parseDouble(sections[5]);
        frame.las.longitude = Double.parseDouble(sections[6]);

        frame.booster.altitude = Double.parseDouble(sections[7]);
        frame.booster.latitude = Double.parseDouble(sections[8]);
        frame.booster.longitude = Double.parseDouble(sections[9]);

        frame.cm.rotation = new Quaternionf( Float.parseFloat(sections[10]),
                    Float.parseFloat(sections[11]),
                    Float.parseFloat(sections[12]),
                    Float.parseFloat(sections[13])).multiply(new Quaternionf(new Vector3f(0,0,0))).normalize();

        frame.las.rotation = new Quaternionf(Float.parseFloat(sections[14]),
                    Float.parseFloat(sections[15]),
                    Float.parseFloat(sections[16]),
                    Float.parseFloat(sections[17])).multiply(new Quaternionf(new Vector3f(0,0,0))).normalize();

        frame.booster.rotation = new Quaternionf(Float.parseFloat(sections[18]),
                    Float.parseFloat(sections[19]),
                    Float.parseFloat(sections[20]),
                    Float.parseFloat(sections[21])).multiply(new Quaternionf(new Vector3f(0,0,0))).normalize();

        frame.engineFlag = Integer.parseInt(sections[22]);
        frame.reserved = Integer.parseInt(sections[23]);
        return frame;
    }

    public static void processEvents(DataFrame before, DataFrame ahead, float length){
        if(before.engineFlag == 1 && ahead.engineFlag == 3){
            ADCAA2.boosterDetatchPercent = before.time/length;
        }

        if(before.engineFlag == 3 && ahead.engineFlag == 2){
            ADCAA2.boosterOffPercent = before.time/length;
        }

        if(before.engineFlag == 2 && ahead.engineFlag == 0){
            ADCAA2.LASOffPercent = before.time/length;
        }

        if(before.engineFlag == 0 && ahead.engineFlag == 1){
            ADCAA2.takeoffPercent = before.time/length;
        }

        if(before.cm.toPosition().distanceTo(before.las.toPosition()).floatValue() > 15f && ADCAA2.LASDetatchPercent == 0){
            ADCAA2.LASDetatchPercent = before.time/length;
        }
    }
}
