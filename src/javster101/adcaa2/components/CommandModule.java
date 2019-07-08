package javster101.adcaa2.components;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;

public class CommandModule extends FlightElement {

    public CommandModule(){
        super(Resource.getModel("cm"));
        holder.setPositionOffset(new Vector3f(0,15,0));

    }
}
