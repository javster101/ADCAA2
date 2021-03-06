package javster101.adcaa2.components;

import com.opengg.core.animation.Animation;
import com.opengg.core.animation.AnimationManager;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.particle.FountainParticleEmitter;

public class Booster extends FlightElement {
    FountainParticleEmitter boosterEmitter;
    FountainParticleEmitter boosterFireEmitter;
    Sound rocketSound;
    boolean firing = false;

    public Booster(){
        super(Resource.getModel("booster"));
        boosterEmitter = new FountainParticleEmitter(200, 100, 2, 10, new Vector3f(0,-1,0), Resource.getTexture("smoke.png"));
        boosterEmitter.setBindParticlesToEmitter(false);
        boosterEmitter.setScaleOffset(new Vector3f(20));
        boosterEmitter.setEnabled(false);
        boosterEmitter.setPositionOffset(new Vector3f(0,0,0));

        boosterFireEmitter = new FountainParticleEmitter(500, 100, 0.4f, 10, new Vector3f(0,-1,0), Resource.getTexture("fire.png"));
        boosterFireEmitter.setBindParticlesToEmitter(false);
        boosterFireEmitter.setScaleOffset(new Vector3f(30));
        boosterFireEmitter.setEnabled(false);
        boosterFireEmitter.setPositionOffset(new Vector3f(0,0,0));
        this.attach(boosterEmitter);
        this.attach(boosterFireEmitter);

        rocketSound = new Sound(Resource.getSoundData("rocket.ogg"));
        rocketSound.shouldLoop(true);
        rocketSound.setGain(0);
        rocketSound.play();
//
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        rocketSound.setPosition(this.getPosition());
    }

    public void setBoosterState(boolean state){
        if(state == true && !firing){
            new Animation(3, false).addStaticEvent(
                    Animation.AnimationStage.createStaticStage(0,3,
                            d -> (d * d) / 9,
                            d -> rocketSound.setGain(d.floatValue()))).start();
            firing = true;
        }else{
            if(state == false && firing){
                firing = false;
                new Animation(0.1, false).addStaticEvent(
                        Animation.AnimationStage.createStaticStage(0,0.1,
                                d -> d*10,
                                d -> rocketSound.setGain(1-d.floatValue()))).start();
            }
        }
        boosterEmitter.setEnabled(state);
        boosterFireEmitter.setEnabled(state);

    }
}
