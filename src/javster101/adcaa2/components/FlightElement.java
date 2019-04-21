package javster101.adcaa2.components;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelComponent;
import javster101.adcaa2.DataElement;

public class FlightElement extends Component {
    public ModelComponent model;

    public FlightElement(Model model){
        this.model = new ModelComponent(model);
        this.attach(this.model);
    }

    public void useElement(DataElement element){
        this.setRotationOffset(element.rotation);

        this.setPositionOffset(element.toPosition());
    }
}
