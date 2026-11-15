package com.ussm.the_undervoid.client.model;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;


/**
 * Made with Blockbench 5.2.2
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class EneraAnimations {
	public static final AnimationDefinition animationattack1 = AnimationDefinition.Builder.withLength(1.0F).looping()
		.addAnimation("Waist", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(7.6145F, 9.9136F, 1.3184F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(7.5284F, -4.9571F, -0.6542F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(7.6145F, 9.9136F, 1.3184F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0417F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(0.0F, -2.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 177.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-21.5478F, 54.9248F, 94.3492F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-118.9213F, 76.0468F, -7.3952F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(-106.902F, -66.3535F, 36.3599F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -17.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("katana", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-92.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(-90.4346F, -2.4618F, -79.9908F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition animationattack2 = AnimationDefinition.Builder.withLength(1.625F).looping()
		.addAnimation("Waist", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(7.6145F, 9.9136F, 1.3184F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(11.5751F, -49.4193F, -8.8419F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(11.5751F, -49.4193F, -8.8419F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.625F, KeyframeAnimations.degreeVec(7.7615F, -14.8686F, -2.0031F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.8333F, KeyframeAnimations.degreeVec(0.0F, 52.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(-35.9561F, 12.1203F, -78.1506F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0417F, KeyframeAnimations.degreeVec(-0.1513F, 33.5444F, 3.5809F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.375F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-59.4655F, -14.5557F, 35.8671F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-38.6773F, 70.2615F, 75.2614F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(-72.0603F, -68.4802F, 1.7507F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.25F, KeyframeAnimations.degreeVec(-56.6796F, -3.8977F, 18.6931F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.4583F, KeyframeAnimations.degreeVec(-112.5766F, 29.3065F, -40.7119F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.5F, KeyframeAnimations.degreeVec(-109.5036F, 45.0536F, -38.6106F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.5417F, KeyframeAnimations.degreeVec(-113.1621F, 53.8665F, -51.4376F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.5833F, KeyframeAnimations.degreeVec(-112.0332F, 64.5159F, -62.1422F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.625F, KeyframeAnimations.degreeVec(-172.6827F, 43.2992F, -145.4719F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.125F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -17.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-55.2852F, 0.0F, -17.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -17.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.5F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -17.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("katana", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-92.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition animationattack3 = AnimationDefinition.Builder.withLength(1.0F)
		.addAnimation("Waist", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.degreeVec(0.0F, 18.26F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(0.0F, -30.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.degreeVec(-2.9555F, -2.9324F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4167F, KeyframeAnimations.degreeVec(3.8952F, -2.4951F, -102.6782F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-2.9555F, -2.9324F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(-6.8958F, -6.8423F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Body", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(0.0F, -12.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.degreeVec(-137.1408F, -50.7397F, 82.7872F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.degreeVec(-4.5955F, -10.9237F, 78.1194F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-106.1453F, 39.0354F, -38.8245F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.8333F, KeyframeAnimations.degreeVec(-36.0028F, -0.9108F, -52.6453F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(-37.1393F, -5.4937F, -22.5992F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(9.6658F, 2.576F, -14.7822F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0833F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Leg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0833F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Leg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();

	public static final AnimationDefinition animationmarcheboss = AnimationDefinition.Builder.withLength(1.0F).looping()
		.addAnimation("Waist", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(13.0177F, -19.3815F, -1.662F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(12.5176F, 7.995F, 4.5111F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.degreeVec(13.9545F, 25.7091F, 8.8804F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(12.8459F, -15.115F, -0.6448F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(13.0177F, -19.3815F, -1.662F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Waist", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 2.0F, -3.15F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.posVec(0.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 2.0F, -3.15F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Head", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0417F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0833F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.125F, KeyframeAnimations.degreeVec(-158.8302F, -46.0745F, 164.4097F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.1667F, KeyframeAnimations.degreeVec(-158.8302F, -46.0745F, 164.4097F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2083F, KeyframeAnimations.degreeVec(-158.8302F, -46.0745F, 164.4097F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.degreeVec(-53.2469F, 17.6896F, -25.399F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-20.0988F, -10.5523F, 40.8435F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.3333F, KeyframeAnimations.degreeVec(-20.0988F, -10.5523F, 40.8435F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.375F, KeyframeAnimations.degreeVec(-13.855F, 1.2884F, -0.3453F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-4.3124F, -1.1068F, 13.0771F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-4.3124F, -1.1068F, 13.0771F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.degreeVec(-45.7394F, -65.2416F, 54.478F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-45.7394F, -65.2416F, 54.478F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-13.5002F, -17.7435F, 4.149F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.625F, KeyframeAnimations.degreeVec(-13.5002F, -17.7435F, 4.149F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-13.5002F, -17.7435F, 4.149F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(-12.5447F, -2.4404F, 0.5428F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.degreeVec(-13.2049F, 56.0F, -2.0521F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-13.5002F, -17.7435F, 4.149F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.8333F, KeyframeAnimations.degreeVec(-13.5002F, -17.7435F, 4.149F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.875F, KeyframeAnimations.degreeVec(-12.5447F, -2.4404F, 0.5428F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(-13.2049F, 56.0F, -2.0521F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9583F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(-15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Body", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -7.5F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.degreeVec(0.0F, 20.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -7.5F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-17.6686F, 27.7306F, 55.6061F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(-17.6686F, 27.7306F, 55.6061F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Arm", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(-5.0F, -2.0F, -4.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.posVec(-5.0F, 1.0F, -4.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(-5.0F, -2.0F, -4.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-5.83F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2083F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.9167F, KeyframeAnimations.degreeVec(-5.83F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Right Leg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, -7.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.0833F, KeyframeAnimations.posVec(0.0F, -0.6F, -6.8F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.2083F, KeyframeAnimations.posVec(0.0F, -0.14F, -5.57F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 1.0F, -3.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, -7.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-5.83F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.degreeVec(-5.83F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Left Leg", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 1.0F, -3.5F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, -7.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.5833F, KeyframeAnimations.posVec(0.0F, -0.6F, -6.8F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.7083F, KeyframeAnimations.posVec(0.0F, -0.14F, -5.57F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("katana", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(177.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.degreeVec(177.5F, 0.0F, 5.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("katana", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();
}