package javster101.adcaa2;

import com.opengg.core.math.Quaternionf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataParser {
    public static List<DataFrame> parse(String file){
        try {
            return Files.lines(Paths.get(file)).skip(1).map(DataParser::parseLine).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<DataFrame> parse(List<String> lines){
        return lines.stream().map(DataParser::parseLine).collect(Collectors.toList());
    }

    public static DataFrame parseLine(String line){
        //time (sec), cm alt, lat, long, las alt, lat, long, booster alt, lat, long, cm quat(wxyz) las quat, booster quat, engine flag, reserved
        var sections = line.split(",");

        DataFrame frame = new DataFrame();

        frame.time = Float.parseFloat(sections[0]);

        frame.cm.altitude = Float.parseFloat(sections[1]);
        frame.cm.latitude = Float.parseFloat(sections[2]);
        frame.cm.longitude = Float.parseFloat(sections[3]);

        frame.las.altitude = Float.parseFloat(sections[4]);
        frame.las.latitude = Float.parseFloat(sections[5]);
        frame.las.longitude = Float.parseFloat(sections[6]);

        frame.booster.altitude = Float.parseFloat(sections[7]);
        frame.booster.latitude = Float.parseFloat(sections[8]);
        frame.booster.longitude = Float.parseFloat(sections[9]);

        frame.cm.rotation = new Quaternionf(Float.parseFloat(sections[10]),
                Float.parseFloat(sections[11]),
                Float.parseFloat(sections[12]),
                Float.parseFloat(sections[13]));

        frame.las.rotation = new Quaternionf(Float.parseFloat(sections[14]),
                Float.parseFloat(sections[15]),
                Float.parseFloat(sections[16]),
                Float.parseFloat(sections[17]));

        frame.booster.rotation = new Quaternionf(Float.parseFloat(sections[18]),
                Float.parseFloat(sections[19]),
                Float.parseFloat(sections[20]),
                Float.parseFloat(sections[21]));

        frame.engineFlag = Integer.parseInt(sections[22]);
        frame.reserved = Integer.parseInt(sections[23]);
        return frame;
    }
}
