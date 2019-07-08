package javster101.adcaa2.components;

import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.io.input.mouse.MouseScrollChangeListener;
import com.opengg.core.io.input.mouse.MouseScrollListener;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.Component;

public class ElementCamera extends Component implements MouseScrollChangeListener, KeyboardListener {
    private CameraComponent component;
    private float elementDistance = 100f;
    private float yrotvel;
    private float zrotvel;
    private float curyrot;
    private float curzrot;

    public ElementCamera(){
        this.setAbsoluteRotation(true);
        this.setAbsoluteOffset(true);
        this.setRotationOffset(new Vector3f(0,90,0));
        this.setPositionOffset(new Vector3f(-elementDistance,0, 0));

        component = new CameraComponent();
        this.attach(component);
        MouseController.addScrollChangeListener(this);
        KeyboardController.addKeyboardListener(this);
    }

    public void update(float delta){
        curyrot += yrotvel;
        curzrot += zrotvel;
        curzrot = FastMath.clamp(curzrot, -89,89);
        var direction = new Quaternionf(new Vector3f(0, curyrot, curzrot)).transform(new Vector3f(0,0,1));


    }

    @Override
    public void onScrollUp() {
        elementDistance += 2;
        this.setPositionOffset(new Vector3f(-elementDistance,0, 0));
    }

    @Override
    public void onScrollDown() {
        elementDistance -= 2;
        this.setPositionOffset(new Vector3f(-elementDistance,0, 0));
    }

    @Override
    public void keyPressed(int key) {
        if(key == Key.KEY_W){
            zrotvel++;
        }else if(key == Key.KEY_S){
            zrotvel--;
        }else if(key == Key.KEY_A){
            yrotvel--;
        }else if(key == Key.KEY_D){
            yrotvel++;
        }
    }

    @Override
    public void keyReleased(int key) {
        if(key == Key.KEY_W){
            zrotvel--;
        }else if(key == Key.KEY_S){
            zrotvel++;
        }else if(key == Key.KEY_A){
            yrotvel++;
        }else if(key == Key.KEY_D) {
            yrotvel--;
        }
    }
}
