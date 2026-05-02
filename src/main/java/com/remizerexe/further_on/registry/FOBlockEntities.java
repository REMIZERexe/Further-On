package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHatchBlockEntity;
import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthBlock;
import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthBlockEntity;
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

    public static void register() { }
}