package javster101.adcaa2;

import com.opengg.core.engine.*;
import com.opengg.core.gui.*;
import com.opengg.core.io.ControlType;
import com.opengg.core.math.*;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.FreeFlyComponent;
import com.opengg.core.world.components.LightComponent;
import com.opengg.core.world.components.RenderComponent;
import javster101.adcaa2.components.FlightManager;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.opengg.core.io.input.keyboard.Key.*;

public class ADCAA2 extends GGApplication {
    public static GUI gui;
    private FreeFlyComponent player;
    private int tick;
    private int valueAmount = 30;
    private static final boolean fileMode = true;
    public static boolean guiEnabled = false;
    public static boolean run = true;

    public static float takeoffPercent=0;
    public static float boosterDetatchPercent=0;
    public static float boosterOffPercent=0;
    public static float LASDetatchPercent=0;
    public static float LASOffPercent=0;


    public static void main(String... args) {
        OpenGG.initialize(new ADCAA2(),
                new InitializationOptions()
                        .setApplicationName("ADC AA 2")
                        .setWindowInfo(new WindowInfo()
                                .setName("ADC AA 2 Challenge")
                                .setVsync(true)
                                .setWidth(1920)
                                .setHeight(1080)));
    }

    @Override
    public void setup() {


        if(fileMode){
            enableGUI();
            var data = DataParser.parse(Resource.getAbsoluteFromLocal("resources/data/Data.csv"));

            assert data != null;

            initializeFromFrameList(data);
        }else{
            new PacketParser().run();
        }

        WorldEngine.getCurrent().attach(new LightComponent(Light.createDirectional(Quaternionf.LookAt(new Vector3f(10, 10, 0), new Vector3f()), new Vector3f(1))));
        WorldEngine.getCurrent().attach(new RenderComponent(
                new TexturedDrawnObject(ObjectCreator.createQuadPrism(new Vector3f(-12500, -3, -10000).multiply(0.302f),
                        new Vector3f(12500, 0, 10000).multiply(0.302f)), Resource.getTexture("smallcape.png")))
                .setPositionOffset(new Vector3f(93, 0, -3080))
                .setRotationOffset(new Vector3f(0, 90, 0)));

        WorldEngine.getCurrent().attach(new RenderComponent(
                new TexturedDrawnObject(ObjectCreator.createQuadPrism(new Vector3f(-125000, -0.2f, -125000).multiply(0.302f),
                        new Vector3f(250000, 0.2f, 250000).multiply(0.302f)), Resource.getTexture("water.jpg")))
                .setPositionOffset(new Vector3f(0, -10, 0)));

        /*WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(
                Texture.getSRGBCubemap(
                        Texture.dataOfColor(new Color(48, 144, 255), 1f),
                        Texture.dataOfColor(new Color(48, 144, 255), 1f),
                        Texture.dataOfColor(new Color(48, 144, 255), 1f),
                        Texture.dataOfColor(new Color(48, 144, 255), 1f),
                        Texture.dataOfColor(new Color(48, 144, 255), 1f),
                        Texture.dataOfColor(new Color(48, 144, 255), 1f)), 21000));*/

        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "down", KEY_LEFT_SHIFT);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_RIGHT);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_LEFT);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_UP);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_DOWN);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);

        RenderEngine.setProjectionData(ProjectionData.getPerspective(90, 6f, 42000));
        RenderEngine.setClearColor(new Vector3f(48/255f, 144/255f, 255/255f));

        WindowController.getWindow().setCursorLock(false);
    }

    public static void initializeFromFrameList(List<DataFrame> data) {
        if(FlightManager.currentManager != null)
            WorldEngine.getCurrent().remove(FlightManager.currentManager);

        WorldEngine.getCurrent().attach(new FlightManager(data));
        IntStream.range(0, data.size()-2).forEach(i -> DataParser.processEvents(data.get(i), data.get(i+1), data.get(data.size()-1).time));

        FlightManager.currentManager.setCamera(0);
        enableGUI();
    }

    public static void initializeNetworkFlightManager(DataFrame firstFrame){
        WorldEngine.getCurrent().attach(new FlightManager(firstFrame));
        FlightManager.currentManager.setCamera(0);
    }

    public static void enableGUI(){
        gui = new GUI();
        var altGraph = new GUIGroup(new Vector2f(0.0208f, 0.117f));
        var altLine = new GUILine(Texture.ofColor(Color.WHITE));
        var altText = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0, 0.20f));
        var velGraph = new GUIGroup(new Vector2f(0.0208f, 0.4f));
        var velLine = new GUILine(Texture.ofColor(Color.WHITE));
        var velText = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0, 0.20f));

        var background = new GUITexture(Resource.getTexture("backdrop.png"), new Vector2f(0,0), new Vector2f(1,1));
        background.setLayer(-0.5f);

        var progressGroup = new GUIGroup();
        var progressBar = new GUIProgressBar(new Vector2f(0.21f,0.032f), new Vector2f(0.78f,0.01f), Color.GREEN, Color.BLACK);

        var LASText = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.039f, 0.8f));
        var boosterText = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.039f, 0.735f));

        var takeoffGroup = new GUIGroup(new Vector2f(0.21f + 0.78f * takeoffPercent,0.015f));
        var takeoffLine = new GUITexture(Texture.ofColor(Color.WHITE), new Vector2f(0,0), new Vector2f(0.002f,0.04f));
        takeoffLine.setLayer(0.5f);
        takeoffGroup.addItem("line", takeoffLine);
        var takeoffText = new GUIText(Text.from("Liftoff").size(0.08f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(-0.03f, 0.060f));
        takeoffGroup.addItem("text", takeoffText);

        var boosterDetatchGroup = new GUIGroup(new Vector2f(0.21f + 0.78f * boosterDetatchPercent,0.015f));
        var boosterDetatchLine = new GUITexture(Texture.ofColor(Color.WHITE), new Vector2f(0,0), new Vector2f(0.002f,0.04f));
        boosterDetatchLine.setLayer(0.5f);
        boosterDetatchGroup.addItem("line", boosterDetatchLine);
        var boosterDetatchText = new GUIText(Text.from("LAS Activation").size(0.08f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(-0.05f, 0.060f));
        boosterDetatchGroup.addItem("text", boosterDetatchText);

        var boosterOffGroup = new GUIGroup(new Vector2f(0.21f + 0.78f * boosterOffPercent,0.015f));
        var boosterOffLine = new GUITexture(Texture.ofColor(Color.WHITE), new Vector2f(0,0), new Vector2f(0.002f,0.04f));
        boosterOffLine.setLayer(0.5f);
        boosterOffGroup.addItem("line", boosterOffLine);
        var boosterOffText = new GUIText(Text.from("Main Engine Cutoff").size(0.08f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(-0.08f, 0.002f));
        boosterOffGroup.addItem("text", boosterOffText);

        var LASDetatchGroup = new GUIGroup(new Vector2f(0.21f + 0.78f * LASDetatchPercent,0.015f));
        var LASDetatchLine = new GUITexture(Texture.ofColor(Color.WHITE), new Vector2f(0,0), new Vector2f(0.002f,0.04f));
        LASDetatchLine.setLayer(0.5f);
        LASDetatchGroup.addItem("line", LASDetatchLine);
        var LASDetatchText = new GUIText(Text.from("LAS Detach").size(0.08f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(-0.05f, 0.060f));
        LASDetatchGroup.addItem("text", LASDetatchText);


        var LASOffGroup = new GUIGroup(new Vector2f(0.21f + 0.78f * LASOffPercent,0.015f));
        var LASOffLine = new GUITexture(Texture.ofColor(Color.WHITE), new Vector2f(0,0), new Vector2f(0.002f,0.04f));
        LASOffLine.setLayer(0.5f);
        LASOffGroup.addItem("line", LASOffLine);
        var LASOffText = new GUIText(Text.from("LAS Engine Cutoff").size(0.08f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(-0.05f, 0.002f));
        LASOffGroup.addItem("text", LASOffText);


        progressGroup.addItem("bar", progressBar);
        progressGroup.addItem("takeoff", takeoffGroup);
        progressGroup.addItem("boosterdet", boosterDetatchGroup);
        progressGroup.addItem("boosteroff", boosterOffGroup);
        progressGroup.addItem("lasdetatch", LASDetatchGroup);
        progressGroup.addItem("lasoff", LASOffGroup);

        var currentText = new GUIGroup(new Vector2f(0.02f, 0.98f));
        var currentTextText = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0, 0));
        currentText.addItem("text", currentTextText);

        var MET = new GUIGroup(new Vector2f(0.039f, 0.87f));
        var METText = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0, 0));
        MET.addItem("text", METText);

        GUIController.addAndUse(gui, "main");
        gui.addItem("altGraph", altGraph);
        gui.addItem("velGraph", velGraph);
        gui.addItem("currentText", currentText);
        gui.addItem("METText", MET);
        gui.addItem("bar", progressGroup);
        gui.addItem("background", background);
        gui.addItem("LASState", LASText);
        gui.addItem("boosterState", boosterText);

        altGraph.addItem("graph", altLine);
        altGraph.addItem("text", altText);

        velGraph.addItem("graph", velLine);
        velGraph.addItem("text", velText);
        guiEnabled = true;
    }

    @Override
    public void render() {

    }

    @Override
    public void update(float delta) {
        if(!guiEnabled) return;
        tick++;
        if (tick > 10) {
            tick = 0;
            var tempData = FlightManager.currentManager.data.stream()
                    .filter(c -> c.time < FlightManager.currentManager.current)
                    .collect(Collectors.toList());

            var data = IntStream.range(0, tempData.size() / valueAmount)
                    .mapToObj(i -> tempData.get(i * valueAmount))
                    .collect(Collectors.toList());

            ((GUIText) ((GUIGroup) ADCAA2.gui.getRoot().getItem("METText")).getItem("text")).setText(
                    Text.from("MET: " + String.format("%02d:%02d", (int) FlightManager.currentManager.current / 60, (int) FlightManager.currentManager.current % 60))
                            .size(0.12f)
            );

            ((GUIProgressBar) ((GUIGroup) ADCAA2.gui.getRoot().getItem("bar")).getItem("bar")).setPercent(tempData.get(tempData.size()-1).time/FlightManager.currentManager.data.get(FlightManager.currentManager.data.size()-1).time);

            DecimalFormat numberFormat = new DecimalFormat("#.00");
            List<Vector2f> altNodes;
            List<Vector2f> velNodes;

            if(data.size() < 10) return;
            if (FlightManager.currentManager.currentCam == 0) {
                altNodes = data.stream()
                        .map(c -> new Vector2f(c.time / (90 * 10.5f), (float) c.cm.altitude / 240000))
                        .collect(Collectors.toList());

                velNodes = IntStream.range(1, data.size() / 2)
                        .mapToObj(i -> Tuple.of(data.get(i * 2 - 1), data.get(i * 2)))
                        .map  (c -> new Vector2f(c.x.time / (90 * 10.5f), c.y.cm.toPosition().subtract(c.x.cm.toPosition()).multiply(BigDecimal.valueOf(c.y.time - c.x.time)).length().floatValue() / 1600))
                        .collect(Collectors.toList());
            } else if (FlightManager.currentManager.currentCam == 1) {
                altNodes = data.stream()
                        .map(c -> new Vector2f(c.time / (90 * 10.5f), (float) c.las.altitude / 240000))
                        .collect(Collectors.toList());

                velNodes = IntStream.range(1, data.size() / 2)
                        .mapToObj(i -> Tuple.of(data.get(i * 2 - 1), data.get(i * 2)))
                        .map(c -> new Vector2f(c.x.time / (90 * 10.5f), c.y.las.toPosition().subtract(c.x.las.toPosition()).multiply(BigDecimal.valueOf(c.y.time - c.x.time)).length().floatValue() / 1600))
                        .map(c -> new Vector2f(c.x, FastMath.clamp(c.y,0,0.195f)))
                        .collect(Collectors.toList());
            }else{
                altNodes = data.stream()
                        .map(c -> new Vector2f(c.time/(90*10.5f), (float)c.booster.altitude/240000))
                        .collect(Collectors.toList());

                velNodes = IntStream.range(1, data.size()/2)
                        .mapToObj(i -> Tuple.of(data.get(i*2-1), data.get(i*2)))
                        .map(c -> new Vector2f(c.x.time/(90*10.5f), c.y.booster.toPosition().subtract(c.x.booster.toPosition()).multiply(BigDecimal.valueOf(c.y.time-c.x.time)).length().floatValue() / 1600))
                        .collect(Collectors.toList());
            }


            ((GUILine) ((GUIGroup) gui.getRoot().getItem("altGraph")).getItem("graph")).setPoints(altNodes);
            ((GUIText) ((GUIGroup) gui.getRoot().getItem("altGraph")).getItem("text"))
                    .setText(Text.from("Altitude (m): " + numberFormat.format(altNodes.get(data.size()-1).y * 240000 * 0.302))
                            .size(0.08f));

            ((GUILine) ((GUIGroup) gui.getRoot().getItem("velGraph")).getItem("graph")).setPoints(velNodes);
            ((GUIText) ((GUIGroup) gui.getRoot().getItem("velGraph")).getItem("text"))
                    .setText(Text.from("Velocity (m/s): " + numberFormat.format(velNodes.get(velNodes.size() - 1).y * 1600))
                            .size(0.08f));

            String engine;
            var flag = data.get(data.size()-1).engineFlag;

            if(flag == 1 || flag == 3){
                ((GUIText) gui.getRoot().getItem("boosterState"))
                        .setText(Text.from("Booster On")
                                .size(0.1f));
            }else{
                ((GUIText) gui.getRoot().getItem("boosterState"))
                        .setText(Text.from("Booster Off")
                                .size(0.1f));
            }

            if(flag == 2 || flag == 3){
                ((GUIText) gui.getRoot().getItem("LASState"))
                        .setText(Text.from("LAS Engine On")
                                .size(0.1f));
            }else{
                ((GUIText) gui.getRoot().getItem("LASState"))
                        .setText(Text.from("LAS Engine Off")
                                .size(0.1f));
            }

        }
    }
}
