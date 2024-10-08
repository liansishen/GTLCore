package org.gtlcore.gtlcore.common.machine.multiblock.part;

import com.google.common.collect.ImmutableSet;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ICleanroomProvider;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.DummyCleanroom;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.gtlcore.gtlcore.api.machine.multiblock.GTLCleanroomType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CleaningConfigurationMaintenanceHatchPartMachine extends AutoConfigurationMaintenanceHatchPartMachine {

    protected static final Set<CleanroomType> CLEANROOM = new ObjectOpenHashSet<>();
    protected static final Set<CleanroomType> STERILE_CLEANROOM = new ObjectOpenHashSet<>();
    protected static final Set<CleanroomType> LAW_CLEANROOM = new ObjectOpenHashSet<>();

    static {
        CLEANROOM.add(CleanroomType.CLEANROOM);
        STERILE_CLEANROOM.addAll(CLEANROOM);
        STERILE_CLEANROOM.add(CleanroomType.STERILE_CLEANROOM);
        LAW_CLEANROOM.addAll(STERILE_CLEANROOM);
        LAW_CLEANROOM.add(GTLCleanroomType.LAW_CLEANROOM);
    }

    // must come after the static block
    public static final ICleanroomProvider DUMMY_CLEANROOM = DummyCleanroom.createForTypes(CLEANROOM);
    public static final ICleanroomProvider STERILE_DUMMY_CLEANROOM = DummyCleanroom.createForTypes(STERILE_CLEANROOM);
    public static final ICleanroomProvider LAW_DUMMY_CLEANROOM = DummyCleanroom.createForTypes(LAW_CLEANROOM);

    ICleanroomProvider cleanroomTypes;

    public CleaningConfigurationMaintenanceHatchPartMachine(IMachineBlockEntity metaTileEntityId,
                                                            ICleanroomProvider cleanroomTypes) {
        super(metaTileEntityId);
        this.cleanroomTypes = cleanroomTypes;
    }

    public static CleaningConfigurationMaintenanceHatchPartMachine Cleaning(IMachineBlockEntity metaTileEntityId) {
        return new CleaningConfigurationMaintenanceHatchPartMachine(metaTileEntityId, DUMMY_CLEANROOM);
    }

    public static CleaningConfigurationMaintenanceHatchPartMachine SterileCleaning(IMachineBlockEntity metaTileEntityId) {
        return new CleaningConfigurationMaintenanceHatchPartMachine(metaTileEntityId, STERILE_DUMMY_CLEANROOM);
    }

    public static CleaningConfigurationMaintenanceHatchPartMachine LawCleaning(IMachineBlockEntity metaTileEntityId) {
        return new CleaningConfigurationMaintenanceHatchPartMachine(metaTileEntityId, LAW_DUMMY_CLEANROOM);
    }

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof ICleanroomReceiver receiver) {
            receiver.setCleanroom(cleanroomTypes);
        }
    }

    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        if (controller instanceof ICleanroomReceiver receiver && receiver.getCleanroom() == cleanroomTypes) {
            receiver.setCleanroom(null);
        }
    }

    @Override
    public int getTier() {
        if (this.cleanroomTypes == STERILE_DUMMY_CLEANROOM) return GTValues.UHV;
        if (this.cleanroomTypes == LAW_DUMMY_CLEANROOM) return GTValues.UXV;
        return GTValues.IV;
    }

    /**
     * Add an {@link CleanroomType} that is provided to multiblocks with this hatch
     *
     * @param type the type to add
     */
    @SuppressWarnings("unused")
    public static void addCleanroomType(@NotNull CleanroomType type) {
        CLEANROOM.add(type);
    }

    /**
     * @return the {@link CleanroomType}s this hatch provides to multiblocks
     */
    @SuppressWarnings("unused")
    public static ImmutableSet<CleanroomType> getCleanroomTypes(ICleanroomProvider p) {
        return ImmutableSet.copyOf(p.getTypes());
    }
}
