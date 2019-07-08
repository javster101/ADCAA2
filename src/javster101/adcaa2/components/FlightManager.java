package javster101.adcaa2.components;

import com.opengg.core.gui.GUIGroup;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3big;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.text.Text;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import javster101.adcaa2.ADCAA2;
import javster101.adcaa2.DataFrame;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class FlightManager extends Component implements KeyboardListener {
    public List<DataFrame> data;
    public DataFrame currentFrame;

    public static float scaleMultiplier = 1f;
    public static FlightManager currentManager;

    public Vector3big centerPoint;

    public CommandModule cm;
    public Booster booster;
    public LAS lem;

    public float current = 2.5f;

    public static Quaternionf naturalRotation;

    public int currentCam = 0;

    private boolean isReleased = false;

    private boolean fileMode = false;

    private FlightManager(){
        currentManager = this;

        cm = new CommandModule();
        booster = new Booster();
        lem = new LAS();
        this.attach(cm);
        this.attach(booster);
        this.attach(lem);
        WorldEngine.getCurrent().attach(this);
        KeyboardController.addKeyboardListener(this);

    }

    public FlightManager(List<DataFrame> data) {
        this();
        naturalRotation = new Quaternionf(new Vector3f(0, (float) (-data.get(0).cm.longitude) * FastMath.radiansToDegrees, (float) data.get(0).cm.latitude * FastMath.radiansToDegrees));
        this.data = data;
        this.centerPoint = data.get(0).cm.toPosition();
        fileMode = true;

    }

    public FlightManager(DataFrame initial) {
        this();
        naturalRotation = new Quaternionf(new Vector3f(0, (float) (-initial.cm.longitude) * FastMath.radiansToDegrees, (float) initial.cm.latitude * FastMath.radiansToDegrees));
        this.currentFrame = initial;
        this.centerPoint = initial.cm.toPosition();
        fileMode = false;

    }

    public void setCurrentFrame(DataFrame frame){
        this.currentFrame = frame;
    }

    public void update(float delta){
        DataFrame dataFrame;
        if(fileMode){
            if(ADCAA2.run)
                current += delta;
            var frameBehind = data.stream()
                    .filter(c -> c.time < current)
                    .min(Comparator.comparingDouble(c -> Math.abs(c.time - current))).orElse(data.get(0));

            var frameAhead = data.stream()
                    .filter(c -> c.time >= current)
                    .min(Comparator.comparingDouble(c -> Math.abs(c.time - current))).orElse(data.get(data.size()-1));

            dataFrame = DataFrame.interpolate(frameBehind, frameAhead, (current-frameBehind.time)/(frameAhead.time-frameBehind.time));
            System.out.println(dataFrame);
        }else {
            dataFrame = currentFrame;
        }

        if(isReleased){
            if(dataFrame.cm.toPosition().distanceTo(dataFrame.las.toPosition()).floatValue() > 12){
                cm.useElement(dataFrame.cm);
                lem.useElement(dataFrame.las);
            }else{
                cm.useElement(dataFrame.cm);
                lem.useElement(dataFrame.cm);
            }
            booster.useElement(dataFrame.booster);
        }else {
            cm.useElement(dataFrame.cm);
            lem.useElement(dataFrame.cm);
            booster.useElement(dataFrame.cm);
        }

        if(dataFrame.engineFlag == 1 || dataFrame.engineFlag == 3){
            if(dataFrame.engineFlag == 1) isReleased = false;
            booster.setBoosterState(true);
        }else {
            booster.setBoosterState(false);
        }

        if(dataFrame.engineFlag == 2 || dataFrame.engineFlag == 3){
            lem.setThrusterState(true);
            isReleased = true;
        }else {
            lem.setThrusterState(false);
        }
    }

    public void setCamera(int currentCam){
        if(currentCam == 0){
            cm.camera.setEnabled(true);
            lem.camera.setEnabled(false);
            booster.camera.setEnabled(false);
        }else if(currentCam == 1){
            cm.camera.setEnabled(false);
            lem.camera.setEnabled(true);
            booster.camera.setEnabled(false);
        }else{
            cm.camera.setEnabled(false);
            lem.camera.setEnabled(false);
            booster.camera.setEnabled(true);
        }

        String name = switch (currentCam) {
            case 0 -> "Command Module";
            case 1 -> "LAS";
            case 2 -> "Booster";
            default -> "";
        };

        if(!ADCAA2.guiEnabled) return;
        ((GUIText)((GUIGroup) ADCAA2.gui.getRoot().getItem("currentText")).getItem("text")).setText(
                Text.from(name)
                        .size(0.12f)
        );
    }

    @Override
    public void keyPressed(int key) {
        if(key == Key.KEY_LEFT){
            currentCam++;
            if(currentCam > 2) currentCam = 0;
            setCamera(currentCam);
        }

        if(key == Key.KEY_RIGHT){
            currentCam--;
            if(currentCam < 0) currentCam = 2;
            setCamera(currentCam);
        }

        if(key == Key.KEY_SPACE){
            ADCAA2.run = !ADCAA2.run;
        }
    }

    @Override
    public void keyReleased(int key) {

    }
}
