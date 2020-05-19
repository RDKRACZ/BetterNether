package paulevs.betternether.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.betternether.BetterNether;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.biomes.NetherBiome;
import paulevs.betternether.config.Config;
import paulevs.betternether.registry.BiomesRegistry;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.StructureCaves;
import paulevs.betternether.structures.StructureType;
import paulevs.betternether.world.structures.CityFeature;

public class BNWorldGenerator
{
	private static boolean hasCleaningPass;
	private static boolean hasFixPass;

	private static float oreDensity;
	private static float structureDensity;
	
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	
	private static Mutable popPos = new Mutable();
	
	private static final NetherBiome[][][] BIOMES = new NetherBiome[8][64][8];
	
	private static final List<BlockPos> LIST_FLOOR = new ArrayList<BlockPos>(4096);
	private static final List<BlockPos> LIST_WALL = new ArrayList<BlockPos>(4096);
	private static final List<BlockPos> LIST_CEIL = new ArrayList<BlockPos>(4096);
	private static final List<BlockPos> LIST_LAVA = new ArrayList<BlockPos>(1024);
	private static final HashSet<Biome> MC_BIOMES = new HashSet<Biome>();
	
	private static StructureCaves caves;
	private static NetherBiome biome;

	public static final StructureFeature<DefaultFeatureConfig> CITY = Registry.register(
			Registry.STRUCTURE_FEATURE,
			new Identifier(BetterNether.MOD_ID, "nether_city"),
			new CityFeature(DefaultFeatureConfig::deserialize)
			);
	
	public static void onModInit()
	{
		hasCleaningPass = Config.getBoolean("generator_world", "terrain_cleaning_pass", true);
		hasFixPass = Config.getBoolean("generator_world", "world_fixing_pass", true);
		
		oreDensity = Config.getFloat("generator_world", "cincinnasite_ore_density", 1F / 1024F);
		structureDensity = Config.getFloat("generator_world", "structures_density", 1F / 32F);
		
		if (Config.getBoolean("generator_world", "generate_cities", true))
		{
			ConfiguredFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> sFeature = CITY.configure(FeatureConfig.DEFAULT);
			ConfiguredFeature<?, ?> feature = CITY.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
			for (Biome b: Registry.BIOME)
				if (b.getCategory() == Category.NETHER)
				{
					b.addStructureFeature(sFeature);
					b.addFeature(GenerationStep.Feature.RAW_GENERATION, feature);
				}
			Feature.STRUCTURES.put("nether_city", CITY);
		}
	}

	public static void init(long seed)
	{
		//if (caves == null)
		//{
			caves = new StructureCaves(seed);
		//}
	}
	
	private static NetherBiome getBiomeLocal(int x, int y, int z, Random random)
	{
		int px = (int) Math.round(x + random.nextGaussian() * 0.5) >> 1;
		int py = (int) Math.round(y + random.nextGaussian() * 0.5) >> 1;
		int pz = (int) Math.round(z + random.nextGaussian() * 0.5) >> 1;
		return BIOMES[clamp(px, 7)][clamp(py, 63)][clamp(pz, 7)];
	}
	
	private static int clamp(int x, int max)
	{
		return x < 0 ? 0 : x > max ? max : x;
	}

	public static void populate(ChunkRegion world, int sx, int sz, Random random)
	{
		//Structure Generator
		if (random.nextFloat() < structureDensity)
		{
			popPos.set(sx + random.nextInt(16), 32 + random.nextInt(120 - 32), sz + random.nextInt(16));
			StructureType type = StructureType.FLOOR;
			boolean isAir =  world.getBlockState(popPos).isAir();
			boolean airUp = world.getBlockState(popPos.up()).isAir() && world.getBlockState(popPos.up(3)).isAir();
			boolean airDown = world.getBlockState(popPos.down()).isAir() && world.getBlockState(popPos.down(3)).isAir();
			NetherBiome biome = getBiomeLocal(popPos.getX() - sx, popPos.getY(), popPos.getZ() - sz, random);
			if (!isAir && !airUp && !airDown)
				type = StructureType.UNDER;
			else
			{
				if (!biome.hasCeilStructures() || random.nextBoolean()) // Floor
				{
					while (world.getBlockState(popPos.down()).isAir() && popPos.getY() > 1)
					{
						popPos.setY(popPos.getY() - 1);
					}
				}
				else // Ceil
				{
					while (!BlocksHelper.isNetherGroundMagma(world.getBlockState(popPos.up())) && popPos.getY() < 127)
					{
						popPos.setY(popPos.getY() + 1);
					}
					type = StructureType.CEIL;
				}
			}
			biome = getBiomeLocal(popPos.getX() - sx, popPos.getY(), popPos.getZ() - sz, random);
			if (world.isAir(popPos))
			{
				if (type == StructureType.FLOOR)
				{
					BlockState down = world.getBlockState(popPos.down());
					if (BlocksHelper.isLava(down))
					{
						biome.genLavaBuildings(world, popPos, random);
					}
					else if (BlocksHelper.isNetherGroundMagma(down))
						biome.genFloorBuildings(world, popPos, random);
				}
				else if (type == StructureType.CEIL)
				{
					BlockState up = world.getBlockState(popPos.up());
					if (BlocksHelper.isNetherGroundMagma(up))
					{
						biome.genCeilBuildings(world, popPos, random);
					}
				}
			}
			else
				biome.genUnderBuildings(world, popPos, random);
		}
		
		LIST_LAVA.clear();
		LIST_FLOOR.clear();
		LIST_WALL.clear();
		LIST_CEIL.clear();

		int ex = sx + 16;
		int ez = sz + 16;

		for (int x = 0; x < 16; x++)
		{
			int wx = sx + x;
			for (int z = 0; z < 16; z++)
			{
				int wz = sz + z;
				for (int y = 1; y < 126; y++)
				{
					if (caves.isInCave(x, y, z))
						continue;

					biome = getBiomeLocal(x, y, z, random);

					popPos.set(wx, y, wz);
					BlockState state = world.getBlockState(popPos);
					boolean lava = BlocksHelper.isLava(state);
					if (lava || BlocksHelper.isNetherGroundMagma(state) || state.getBlock() == Blocks.GRAVEL)
					{
						if (!lava && world.isAir(popPos.up()))
							biome.genSurfColumn(world, popPos, random);

						if (((x + y + z) & 1) == 0)
						{
							// Ground Generation
							if (world.isAir(popPos.up()))
							{
								if (lava)
									LIST_LAVA.add(popPos.up());
								else
									LIST_FLOOR.add(new BlockPos(popPos.up()));
							}

							// Ceiling Generation
							else if (world.isAir(popPos.down()))
							{
								LIST_CEIL.add(new BlockPos(popPos.down()));
							}

							// Wall Generation
							else
							{
								boolean bNorth = world.isAir(popPos.north());
								boolean bSouth = world.isAir(popPos.south());
								boolean bEast = world.isAir(popPos.east());
								boolean bWest = world.isAir(popPos.west());
								if (bNorth || bSouth || bEast || bWest)
								{
									BlockPos objPos = null;
									if (bNorth)
										objPos = popPos.north();
									else if (bSouth)
										objPos = popPos.south();
									else if (bEast)
										objPos = popPos.east();
									else
										objPos = popPos.west();

									if ((popPos.getX() >= sx) && (popPos.getX() < ex) && (popPos.getZ() >= sz) && (popPos.getZ() < ez))
									{
										boolean bDown = world.isAir(objPos.down());
										boolean bUp = world.isAir(objPos.up());

										if (bDown && bUp)
										{
											LIST_WALL.add(new BlockPos(objPos));
										}
									}
								}
							}
						}
						if (random.nextFloat() < oreDensity)
							spawnOre(BlocksRegistry.CINCINNASITE_ORE.getDefaultState(), world, popPos, random);
					}
				}
			}
		}
		
		for (BlockPos pos: LIST_LAVA)
		{
			if (world.isAir(pos))
			{
				biome = getBiomeLocal(pos.getX() - sx, pos.getY(), pos.getZ() - sz, random);
				if (biome != null)
					biome.genLavaObjects(world, pos, random);
			}
		}
		
		for (BlockPos pos: LIST_FLOOR)
			if (world.isAir(pos))
			{
				biome = getBiomeLocal(pos.getX() - sx, pos.getY(), pos.getZ() - sz, random);
				if (biome != null)
					biome.genFloorObjects(world, pos, random);
			}
		
		for (BlockPos pos: LIST_WALL)
			if (world.isAir(pos))
			{
				biome = getBiomeLocal(pos.getX() - sx, pos.getY(), pos.getZ() - sz, random);
				if (biome != null)
					biome.genWallObjects(world, pos, random);
			}
		
		for (BlockPos pos: LIST_CEIL)
			if (world.isAir(pos))
			{
				biome = getBiomeLocal(pos.getX() - sx, pos.getY(), pos.getZ() - sz, random);
				if (biome != null)
					biome.genCeilObjects(world, pos, random);
			}
	}
	
	private static void makeLocalBiomes(ChunkRegion world, int sx, int sz)
	{
		MC_BIOMES.clear();
		for (int x = 0; x < 8; x++)
		{
			popPos.setX(sx + (x << 1) + 2);
			for (int y = 0; y < 64; y++)
			{
				popPos.setY((y << 1) + 2);
				for (int z = 0; z < 8; z++)
				{
					popPos.setZ(sz + (z << 1) + 2);
					Biome b = world.getBiome(popPos);
					BIOMES[x][y][z] = BiomesRegistry.getFromBiome(b);
					//if (b instanceof NetherBiomeWrapper || !(b instanceof NetherBiome))
					MC_BIOMES.add(b);
				}
			}
		}
		
		/*for (int y = 4; y < 60; y += 2)
		{
			Biome b = BIOMES[4][y][4].getBiome();
			if (b instanceof NetherBiomeWrapper || !(b instanceof NetherBiome))
				MC_BIOMES.add(b);
		}*/
	}

	public static void prePopulate(ChunkRegion world, int sx, int sz, Random random)
	{
		makeLocalBiomes(world, sx, sz);
		
		popPos.set(sx, 0, sz);
		caves.generate(world, popPos, random);
		
		if (hasCleaningPass)
		{
			List<BlockPos> pos = new ArrayList<BlockPos>();
			BlockPos up;
			BlockPos down;
			BlockPos north;
			BlockPos south;
			BlockPos east;
			BlockPos west;
			for (int y = 32; y < 110; y++)
			{
				popPos.setY(y);
				for (int x = 0; x < 16; x++)
				{
					popPos.setX(x | sx);
					for (int z = 0; z < 16; z++)
					{
						popPos.setZ(z | sz);
						if (canReplace(world, popPos))
						{
							up = popPos.up();
							down = popPos.down();
							north = popPos.north();
							south = popPos.south();
							east = popPos.east();
							west = popPos.west();
							if (world.isAir(north) && world.isAir(south))
								pos.add(new BlockPos(popPos));
							else if (world.isAir(east) && world.isAir(west))
								pos.add(new BlockPos(popPos));
							else if (world.isAir(up) && world.isAir(down))
								pos.add(new BlockPos(popPos));
							else if (world.isAir(popPos.north().east().down()) && world.isAir(popPos.south().west().up()))
								pos.add(new BlockPos(popPos));
							else if (world.isAir(popPos.south().east().down()) && world.isAir(popPos.north().west().up()))
								pos.add(new BlockPos(popPos));
							else if (world.isAir(popPos.north().west().down()) && world.isAir(popPos.south().east().up()))
								pos.add(new BlockPos(popPos));
							else if (world.isAir(popPos.south().west().down()) && world.isAir(popPos.north().east().up()))
								pos.add(new BlockPos(popPos));
						}
					}
				}
			}
			for (BlockPos p : pos)
			{
				BlocksHelper.setWithoutUpdate(world, p, AIR);
				up = p.up();
				BlockState state = world.getBlockState(up);
				if (!state.getBlock().canPlaceAt(state, world, up))
					BlocksHelper.setWithoutUpdate(world, up, AIR);
			}
		}
	}
	
	private static boolean canReplace(WorldAccess world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		return BlocksHelper.isNetherGround(state) || state.getBlock() == Blocks.GRAVEL;
	}
	
	private static void spawnOre(BlockState state, WorldAccess world, BlockPos pos, Random random)
	{
		for (int i = 0; i < 6 + random.nextInt(11); i++)
		{
			BlockPos local = pos.add(random.nextInt(3), random.nextInt(3), random.nextInt(3));
			if (BlocksHelper.isNetherrack(world.getBlockState(local)))
			{
				BlocksHelper.setWithoutUpdate(world, local, state);
			}
		}
	}
	
	public static void cleaningPass(WorldAccess world, int sx, int sz)
	{
		if (hasFixPass)
		{
			fixBlocks(world, sx, 30, sz, sx + 15, 110, sz + 15);
		}
	}
	
	private static void fixBlocks(WorldAccess world, int x1, int y1, int z1, int x2, int y2, int z2)
	{
		for (int y = y1; y <= y2; y++)
		{
			popPos.setY(y);
			for (int x = x1; x <= x2; x++)
			{
				popPos.setX(x);
				for (int z = z1; z <= z2; z++)
				{
					popPos.setZ(z);
					
					BlockState state = world.getBlockState(popPos);
					
					if (!state.canPlaceAt(world, popPos))
					{
						BlocksHelper.setWithoutUpdate(world, popPos, AIR);
						continue;
					}
					
					if (!state.isOpaque() && world.getBlockState(popPos.up()).getBlock() == Blocks.NETHER_BRICKS)
					{
						BlocksHelper.setWithoutUpdate(world, popPos, Blocks.NETHER_BRICKS.getDefaultState());
						continue;
					}
					
					if (BlocksHelper.isLava(state) && world.isAir(popPos.up()) && world.isAir(popPos.down()))
					{
						BlocksHelper.setWithoutUpdate(world, popPos, AIR);
						continue;
					}
					
					if (state.getBlock() == Blocks.NETHER_WART_BLOCK || state.getBlock() == Blocks.WARPED_WART_BLOCK)
					{
						if (world.isAir(popPos.down()) && world.isAir(popPos.up()) && world.isAir(popPos.north()) && world.isAir(popPos.south()) && world.isAir(popPos.east()) && world.isAir(popPos.west()))
							BlocksHelper.setWithoutUpdate(world, popPos, AIR);
						continue;
					}
				}
			}
		}
	}
	
	public static HashSet<Biome> getPopulateBiomes()
	{
		return MC_BIOMES;
	}
}
