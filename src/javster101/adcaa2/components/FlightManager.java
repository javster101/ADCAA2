package javster101.adcaa2.components;

import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import javster101.adcaa2.DataFrame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FlightManager extends Component {
    public List<DataFrame> data;

    public CommandModule cm;
    public Booster booster;
    public LEM lem;

    public float current = 0f;

    public FlightManager(List<DataFrame> data) {
        this.data = data;
        cm = new CommandModule();
        booster = new Booster();
        lem = new LEM();
        WorldEngine.getCurrent().attach(cm);
        WorldEngine.getCurrent().attach(booster);
        WorldEngine.getCurrent().attach(lem);
    }

    public void update(float delta){
        current += delta;

        var frameBehind = data.stream()
                .filter(c -> c.time < current)
                .min(Comparator.comparingDouble(c -> Math.abs(c.time - current))).orElse(data.get(0));

        var frameAhead = data.stream()
                .filter(c -> c.time >= current)
                .min(Comparator.comparingDouble(c -> Math.abs(c.time - current))).orElse(data.get(data.size()-1));

        var dataFrame = DataFrame.interpolate(frameBehind, frameAhead, (current-frameBehind.time)/(frameAhead.time-frameBehind.time));

        cm.useElement(dataFrame.cm);
        lem.useElement(dataFrame.las);
        booster.useElement(dataFrame.booster);
    }
}
