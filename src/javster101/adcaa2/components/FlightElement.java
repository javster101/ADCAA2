package javster101.adcaa2.components;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3big;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelComponent;
import com.opengg.core.world.components.WorldObject;
import javster101.adcaa2.DataElement;

import java.util.Random;

public class FlightElement extends Component {
    public ModelComponent model;
    public ElementCamera camera;
    public WorldObject holder;

    public FlightElement(Model model){
        this.model = (ModelComponent) new ModelComponent(model).setScaleOffset(new Vector3f(FlightManager.scaleMultiplier).multiply(0.1f * (26f/50f)));
        this.attach(this.model);
        this.attach(holder = new WorldObject());
        holder.attach(camera = new ElementCamera());
    }

    public void useElement(DataElement element){
        this.setRotationOffset(element.rotation.multiply(new Quaternionf(new Vector3f(0,90,90))));
        this.setPositionOffset(element.toPosition().subtract(FlightManager.currentManager.centerPoint).toVector3f());
    }
}
