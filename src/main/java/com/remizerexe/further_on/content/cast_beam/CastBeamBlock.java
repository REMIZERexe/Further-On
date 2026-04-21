package com.remizerexe.further_on.content.cast_beam;

import com.remizerexe.further_on.FurtherOn;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public class CastBeamBlock extends RotatedPillarBlock {
    private static final ResourceLocation MODEL = FurtherOn.asResource("block/cast_beam");
    private static final VoxelShape SHAPE = Shapes.or(Shapes.or(
                    Shapes.box(0, 0, 0,           16.0/16, 2.0/16, 16.0/16),
                    Shapes.box(2.0/16, 2.0/16, 0, 14.0/16, 14.0/16, 16.0/16)),
                    Shapes.box(0, 14.0/16, 0,     16.0/16, 16.0/16, 16.0/16)
    );
    private static final VoxelShaper SHAPER = VoxelShaper.forAxis(SHAPE, Direction.Axis.Z);

    public CastBeamBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPER.get(state.getValue(BlockStateProperties.AXIS));
    }

    public static void blockstate(DataGenContext<Block, CastBeamBlock> ctx, RegistrateBlockstateProvider prov) {
        prov.getVariantBuilder(ctx.get()).forAllStates((bs) -> ConfiguredModel.builder()
                .modelFile(prov.models().getExistingFile(MODEL))
                .rotationX(bs.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y ? 90 : 0)
                .rotationY(bs.getValue(BlockStateProperties.AXIS) == Direction.Axis.X ? 90 : 0)
                .build()
        );
    }
}
