package com.ordana.ngv;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod(NGV.MODID)
public class NGV {
    public static final String MODID = "ngv";

    public static ConfiguredFeature<?, ?> NETHER_GOLD_VEIN_CONFIGURED;
    public static PlacedFeature NETHER_GOLD_VEIN_PLACED;

    public NGV(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::biomeModification);
    }


    public static final RuleTest NETHERRACK = new RandomBlockMatchTest(Blocks.NETHERRACK, 0.3f);
    public static final RuleTest RARE_NETHERRACK = new RandomBlockMatchTest(Blocks.NETHERRACK, 0.01f);
    public static final RuleTest BLACKSTONE = new RandomBlockMatchTest(Blocks.BLACKSTONE, 0.3f);
    public static final RuleTest NETHER_GOLD_ORE = new RandomBlockMatchTest(Blocks.NETHER_GOLD_ORE, 0.25f);
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NETHER_GOLD_VEIN_CONFIGURED = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(NGV.MODID, "nether_gold_vein_configured"),
                    Feature.ORE.configured(new OreConfiguration(
                            List.of(OreConfiguration.target(NETHERRACK, Blocks.NETHER_GOLD_ORE.defaultBlockState()),
                                    OreConfiguration.target(RARE_NETHERRACK, Blocks.RAW_GOLD_BLOCK.defaultBlockState()),
                                    OreConfiguration.target(BLACKSTONE, Blocks.GILDED_BLACKSTONE.defaultBlockState()),
                                    OreConfiguration.target(NETHER_GOLD_ORE, Blocks.RAW_GOLD_BLOCK.defaultBlockState())),
                            64, // The size of the vein. Do not do less than 3 or else it places nothing.
                            0f // % of exposed ore block will not generate if touching air.
                    )));

            NETHER_GOLD_VEIN_PLACED = Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(NGV.MODID, "nether_gold_vein_placed"),
                    NETHER_GOLD_VEIN_CONFIGURED.placed(
                            RarityFilter.onAverageOnceEvery(16),
                            CountOnEveryLayerPlacement.of(2),
                            BiomeFilter.biome()) // Needed to allow the feature to spawn in biomes properly.
            );
        });
    }

    public void biomeModification(final BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        if(category == Biome.BiomeCategory.NETHER) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_GOLD_VEIN_PLACED);
        }
    }
}
