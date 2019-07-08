package javster101.adcaa2.components;

import com.opengg.core.animation.Animation;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.particle.FountainParticleEmitter;

public class LAS extends FlightElement {
    FountainParticleEmitter boosterEmitter;
    FountainParticleEmitter boosterFireEmitter;
    Sound sound;

    boolean firing = false;

    public LAS(){
        super(Resource.getModel("las"));
        boosterEmitter = new FountainParticleEmitter(300, 100, 1, 80, new Vector3f(0,-1,0), Resource.getTexture("smoke.png"));
        boosterEmitter.setBindParticlesToEmitter(false);
        boosterEmitter.setScaleOffset(new Vector3f(20));
        boosterEmitter.setEnabled(false);
        boosterEmitter.setPositionOffset(new Vector3f(0, 22,0));

        boosterFireEmitter = new FountainParticleEmitter(500, 100, 0.4f, 80, new Vector3f(0,-1,0), Resource.getTexture("fire.png"));
        boosterFireEmitter.setBindParticlesToEmitter(false);
        boosterFireEmitter.setScaleOffset(new Vector3f(30));
        boosterFireEmitter.setEnabled(false);
        boosterFireEmitter.setPositionOffset(new Vector3f(0,22,0));
        this.attach(boosterEmitter);
        this.attach(boosterFireEmitter);

        holder.setPositionOffset(new Vector3f(0,22,0));

        sound = new Sound(Resource.getSoundData("rocket.ogg"));
        sound.shouldLoop(true);
        sound.setGain(0);
        sound.setPitch(20);
        sound.play();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        sound.setPosition(this.getPosition());
    }

    public void setThrusterState(boolean state){
        if(state == true && !firing){
            new Animation(0.5, false).addStaticEvent(
                    Animation.AnimationStage.createStaticStage(0,0.5,
                            d -> d * 2,
                            d -> sound.setGain(d.floatValue() * 0.8f))).start();
            firing = true;
        }else{
            if(state == false && firing){
                firing = false;
                new Animation(0.1, false).addStaticEvent(
                        Animation.AnimationStage.createStaticStage(0,0.1,
                                d -> d*10,
                                d -> sound.setGain(1-d.floatValue()))).start();
            }
        }
        boosterEmitter.setEnabled(state);
        boosterFireEmitter.setEnabled(state);

    }
}
