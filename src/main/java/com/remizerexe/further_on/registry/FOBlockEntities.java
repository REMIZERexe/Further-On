package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHatchBlockEntity;
import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthBlock;
import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthBlockEntity;
import com.remizerexe.further_on.content.pipes.IndustrialPipeBlockEntity;
import com.remizerexe.further_on.content.pumpjack.PumpjackBaseBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.remizerexe.further_on.FurtherOn.REGISTRATE;

public class FOBlockEntities {

    public static final BlockEntityEntry<BlastFurnaceHearthBlockEntity> BLAST_FURNACE_HEARTH =
            REGISTRATE.blockEntity("blast_furnace_hearth", BlastFurnaceHearthBlockEntity::new)
                    .validBlocks(FOBlocks.BLAST_FURNACE_HEARTH)
                    .register();

    public static final BlockEntityEntry<BlastFurnaceHatchBlockEntity> BLAST_FURNACE_HATCH =
            REGISTRATE.blockEntity("blast_furnace_base", BlastFurnaceHatchBlockEntity::new)
                    .validBlocks(FOBlocks.BLAST_FURNACE_HATCH)
                    .register();

    public static final BlockEntityEntry<IndustrialPipeBlockEntity> INDUSTRIAL_PIPE =
            REGISTRATE.<IndustrialPipeBlockEntity>blockEntity("industrial_pipe",
                            (type, pos, state) -> new IndustrialPipeBlockEntity(type, pos, state))
                    .validBlock(FOBlocks.INDUSTRIAL_PIPE)
                    .register();

    public static final BlockEntityEntry<PumpjackBaseBlockEntity> PUMPJACK_BASE =
            REGISTRATE.<PumpjackBaseBlockEntity>blockEntity("pumpjack_base",
                            (type, pos, state) -> new PumpjackBaseBlockEntity(type, pos, state))
                    .validBlock(FOBlocks.PUMPJACK_BASE)
                    .register();

    public static void register() { }
}