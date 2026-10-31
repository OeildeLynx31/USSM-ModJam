package com.ussm.examplemod.client;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class MimicAnimations {
    // export blockbench, un peu nettoyé
    public static final AnimationDefinition EMERGE = AnimationDefinition.Builder.withLength(6.8F)
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.52F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.92F, KeyframeAnimations.degreeVec(0.74F, 0.0F, -40.38F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.16F, KeyframeAnimations.degreeVec(-67.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.24F, KeyframeAnimations.degreeVec(-67.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.32F, KeyframeAnimations.degreeVec(-47.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(-67.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.68F, KeyframeAnimations.degreeVec(-67.5F, 0.0F, 15.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.76F, KeyframeAnimations.degreeVec(-67.5F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.84F, KeyframeAnimations.degreeVec(-52.5F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.92F, KeyframeAnimations.degreeVec(-67.5F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.64F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(3.76F, KeyframeAnimations.degreeVec(70.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(4.04F, KeyframeAnimations.degreeVec(70.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(4.12F, KeyframeAnimations.degreeVec(80.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(4.24F, KeyframeAnimations.degreeVec(70.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(5.0F, KeyframeAnimations.degreeVec(77.5F, 0.0F, -2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(6.64F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(6.8F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.52F, KeyframeAnimations.posVec(-8.0F, -11.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.92F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.24F, KeyframeAnimations.posVec(0.0F, 0.47F, -0.95F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.32F, KeyframeAnimations.posVec(0.0F, 0.47F, -0.95F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, 0.47F, -0.95F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.68F, KeyframeAnimations.posVec(0.0F, 1.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.76F, KeyframeAnimations.posVec(0.0F, 1.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.84F, KeyframeAnimations.posVec(0.0F, 1.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, 1.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.64F, KeyframeAnimations.posVec(0.0F, -2.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(3.76F, KeyframeAnimations.posVec(0.0F, -4.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(4.04F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(4.12F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(4.24F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(6.64F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(6.8F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.6F, KeyframeAnimations.degreeVec(25.0F, 0.0F, -7.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.76F, KeyframeAnimations.degreeVec(35.0F, 0.0F, -7.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.92F, KeyframeAnimations.degreeVec(25.0F, 0.0F, -7.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.28F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.52F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.84F, KeyframeAnimations.degreeVec(70.0F, 0.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.64F, KeyframeAnimations.degreeVec(70.0F, 0.0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(3.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(6.64F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(6.8F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -27.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, -14.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.76F, KeyframeAnimations.posVec(0.0F, -11.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.92F, KeyframeAnimations.posVec(0.0F, -14.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.28F, KeyframeAnimations.posVec(0.0F, -6.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.4F, KeyframeAnimations.posVec(0.0F, -4.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.52F, KeyframeAnimations.posVec(0.0F, -6.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(1.84F, KeyframeAnimations.posVec(0.0F, -3.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(2.64F, KeyframeAnimations.posVec(0.0F, -3.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(3.48F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(5.44F, KeyframeAnimations.degreeVec(5.2413F, 17.4313F, 1.5741F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(5.8F, KeyframeAnimations.degreeVec(5.0429F, 7.4713F, 0.6574F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(6.04F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(6.8F, KeyframeAnimations.degreeVec(5.1754F, 14.9416F, 1.3378F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.84F, KeyframeAnimations.degreeVec(-14.2746F, 9.4678F, -23.0435F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.6F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    public static final AnimationDefinition RUN_START = AnimationDefinition.Builder.withLength(1.1667F)
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083F, KeyframeAnimations.degreeVec(138.3643F, -16.7655F, 11.0655F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.1667F, KeyframeAnimations.degreeVec(113.3643F, -16.7655F, 11.0655F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083F, KeyframeAnimations.degreeVec(120.4668F, 10.4287F, -18.0489F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.1667F, KeyframeAnimations.degreeVec(105.4668F, 10.4287F, -18.0489F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.1667F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(5.1754F, 14.9416F, 1.3378F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.1667F, KeyframeAnimations.degreeVec(5.019F, -4.9808F, -0.437F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(1.0F)
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625F, KeyframeAnimations.degreeVec(-141.6018F, 21.2558F, 13.5456F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75F, KeyframeAnimations.degreeVec(28.806F, -27.7126F, 16.5095F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7917F, KeyframeAnimations.degreeVec(22.1482F, -6.9729F, 20.8373F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.0F, KeyframeAnimations.degreeVec(5.1754F, 14.9416F, 1.3378F), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0F, KeyframeAnimations.degreeVec(5.1754F, 14.9416F, 1.3378F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    // run fait a la main, a refaire dans blockbench un jour
    public static final AnimationDefinition RUN = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, -4f), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(0f, 0f, 4f), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, -4f), AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0, KeyframeAnimations.posVec(0, 0, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.125f, KeyframeAnimations.posVec(0, 1, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0, 0, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0, 1, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0, 0, 0), AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(10f, 6f, 0f), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-10f, -6f, 0f), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(10f, 6f, 0f), AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-10f, -6f, 0f), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(10f, 6f, 0f), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-10f, -6f, 0f), AnimationChannel.Interpolations.CATMULLROM)))
        //la tete qui hoche, ca fait bien flipper
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(3, 0, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-3, 0, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(3, 0, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(-3, 0, 0), AnimationChannel.Interpolations.CATMULLROM),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(3, 0, 0), AnimationChannel.Interpolations.CATMULLROM)))
        .build();
}
