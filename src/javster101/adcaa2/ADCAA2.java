package javster101.adcaa2;

import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.io.ControlType;
import com.opengg.core.render.ProjectionData;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.FreeFlyComponent;
import javster101.adcaa2.components.FlightManager;

import static com.opengg.core.io.input.keyboard.Key.*;

public class ADCAA2 extends GGApplication {
    public static void main(String... args){
        OpenGG.initialize(new ADCAA2(),
                new WindowInfo()
                        .setName("ADC AA 2 Challenge")
                        .setWidth(1280)
                        .setHeight(960));
    }

    @Override
    public void setup() {
        this.applicationName = "ADC AA 2 Challenge";
        var data =  DataParser.parse(Resource.getAbsoluteFromLocal("resources/data/Data.csv"));

        WorldEngine.getCurrent().attach(new FlightManager(data));

        var player = new FreeFlyComponent();
        player.setPositionOffset(data.get(0).cm.toPosition());

        WorldEngine.getCurrent().attach(player);
        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 100000f));

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

        RenderEngine.setProjectionData(ProjectionData.getPerspective(90, 1f, 100000));

        WindowController.getWindow().setCursorLock(true);
    }

    @Override
    public void render() {

    }

    @Override
    public void update(float delta) {

    }
}
