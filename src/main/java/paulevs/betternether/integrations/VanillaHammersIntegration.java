package paulevs.betternether.integrations;

import java.lang.reflect.Constructor;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import org.apache.logging.log4j.LogManager;
import paulevs.betternether.registry.ItemsRegistry;

public class VanillaHammersIntegration {
	private static boolean hasHammers;
	private static Constructor<?> hammerConstructor;

	public static Item makeHammer(Tier material, int attackDamage, float attackSpeed) {
		if (!hasHammers)
			return Items.AIR;
		try {
			return (Item) hammerConstructor.newInstance(material, attackDamage, attackSpeed, ItemsRegistry.defaultSettings());
		}
		catch (Exception e) {
			e.printStackTrace();
			return Items.AIR;
		}
	}

	static {
		hasHammers = FabricLoader.getInstance().isModLoaded("vanilla-hammers");
		try {
			if (hasHammers) {
				LogManager.getLogger().info("[BetterNether] Enabled Vanilla Hammers Integration");
				Class<?> hammerItemClass = Class.forName("draylar.magna.item.HammerItem");
				if (hammerItemClass != null)
					for (Constructor<?> c : hammerItemClass.getConstructors())
					if (c.getParameterCount() == 4) {
					hammerConstructor = c;
					break;
					}
				hasHammers = (hammerConstructor != null);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasHammers() {
		return hasHammers;
	}
}
