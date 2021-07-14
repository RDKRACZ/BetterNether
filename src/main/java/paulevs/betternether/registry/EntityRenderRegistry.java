package paulevs.betternether.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import paulevs.betternether.BetterNether;
import paulevs.betternether.entity.model.ModelEntityFirefly;
import paulevs.betternether.entity.model.ModelEntityFlyingPig;
import paulevs.betternether.entity.model.ModelEntityHydrogenJellyfish;
import paulevs.betternether.entity.model.ModelJungleSkeleton;
import paulevs.betternether.entity.model.ModelNaga;
import paulevs.betternether.entity.model.ModelSkull;
import paulevs.betternether.entity.render.RenderChair;
import paulevs.betternether.entity.render.RenderFirefly;
import paulevs.betternether.entity.render.RenderFlyingPig;
import paulevs.betternether.entity.render.RenderHydrogenJellyfish;
import paulevs.betternether.entity.render.RenderJungleSkeleton;
import paulevs.betternether.entity.render.RenderNaga;
import paulevs.betternether.entity.render.RenderNagaProjectile;
import paulevs.betternether.entity.render.RenderSkull;

@Environment(EnvType.CLIENT)
public class EntityRenderRegistry {

	public static final ModelLayerLocation FIREFLY_MODEL = registerMain("firefly");
	public static final ModelLayerLocation NAGA_MODEL = registerMain("naga");
	public static final ModelLayerLocation JUNGLE_SKELETON_MODEL = registerMain("jungle_skeleton");
	public static final ModelLayerLocation FLYING_PIG_MODEL = registerMain("flying_pig");
	public static final ModelLayerLocation HYDROGEN_JELLYFISH_MODEL = registerMain("hydrogen_jelly");
	public static final ModelLayerLocation SKULL_MODEL = registerMain("skull");


	public static ModelLayerLocation registerMain(String id){
		//System.out.println("Register Entity: " + id);
		return new ModelLayerLocation(new ResourceLocation(BetterNether.MOD_ID, id), "main");
		//return EntityModelLayersMixin.callRegisterMain(key);
	}

	public static void register() {
		registerRenderMob(EntityRegistry.FIREFLY, RenderFirefly.class);
		registerRenderMob(EntityRegistry.CHAIR, RenderChair.class);
		registerRenderMob(EntityRegistry.HYDROGEN_JELLYFISH, RenderHydrogenJellyfish.class);
		registerRenderMob(EntityRegistry.NAGA, RenderNaga.class);
		registerRenderAny(EntityRegistry.NAGA_PROJECTILE, RenderNagaProjectile.class);
		registerRenderMob(EntityRegistry.FLYING_PIG, RenderFlyingPig.class);
		registerRenderMob(EntityRegistry.JUNGLE_SKELETON, RenderJungleSkeleton.class);
		registerRenderMob(EntityRegistry.SKULL, RenderSkull.class);

		EntityModelLayerRegistry.registerModelLayer(FIREFLY_MODEL, ModelEntityFirefly::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(NAGA_MODEL, ModelNaga::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(JUNGLE_SKELETON_MODEL, ModelJungleSkeleton::createBodyLayer);
		EntityModelLayerRegistry.registerModelLayer(FLYING_PIG_MODEL, ModelEntityFlyingPig::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(HYDROGEN_JELLYFISH_MODEL, ModelEntityHydrogenJellyfish::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(SKULL_MODEL, ModelSkull::getTexturedModelData);
	}

	private static void registerRenderMob(EntityType<?> entity, Class<? extends MobRenderer<?, ?>> renderer) {
		EntityRendererRegistry.INSTANCE.register(entity, (context) -> {
			MobRenderer render = null;
			try {
				render = renderer.getConstructor(context.getClass()).newInstance(context);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return render;
		});
	}

	private static void registerRenderAny(EntityType<?> entity, Class<? extends EntityRenderer<?>> renderer) {
		EntityRendererRegistry.INSTANCE.register(entity, (context) -> {
			EntityRenderer render = null;
			try {
				render = renderer.getConstructor(context.getClass()).newInstance(context);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return render;
		});
	}
}
