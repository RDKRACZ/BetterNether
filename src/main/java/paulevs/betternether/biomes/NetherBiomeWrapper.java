package paulevs.betternether.biomes;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import paulevs.betternether.structures.StructureType;

public class NetherBiomeWrapper extends NetherBiome {
	final Biome biome;

	public NetherBiomeWrapper(ResourceLocation id) {
		super(new BiomeDefinition(id));
		this.biome = BuiltinRegistries.BIOME.get(id);

		if (id.getPath().equals("basalt_deltas")) {
			addStructure("blackstone_stalactite", STALACTITE_BLACKSTONE, StructureType.FLOOR, 0.2F, true);
			addStructure("stalactite_stalactite", STALACTITE_BASALT, StructureType.FLOOR, 0.2F, true);

			addStructure("blackstone_stalagmite", STALAGMITE_BLACKSTONE, StructureType.CEIL, 0.1F, true);
			addStructure("basalt_stalagmite", STALAGMITE_BASALT, StructureType.CEIL, 0.1F, true);
		}
	}

	public NetherBiomeWrapper(ResourceLocation id, Biome biome) {
		super(new BiomeDefinition(id));
		this.biome = biome;
	}

	@Override
	public void genSurfColumn(LevelAccessor world, BlockPos pos, Random random) {
		//BlocksHelper.setWithoutUpdate(world, pos, biome.getGenerationSettings().getSurfaceConfig().getTopMaterial());
	}
}