package com.maxwell.cyber_ware_port.common.block.robosurgeon;

import com.maxwell.cyber_ware_port.api.event.CyberwareSurgeryEvent;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon.SurgeryManager;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon.SurgerySyncHelper;
import com.maxwell.cyber_ware_port.common.block.surgerychamber.SurgeryChamberBlock;
import com.maxwell.cyber_ware_port.common.block.surgerychamber.SurgeryChamberBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.container.RobosurgeonMenu;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareSlotType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.common.network.SyncSurgeryProgressPacket;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RobosurgeonBlockEntity extends BlockEntity implements MenuProvider {
    public static final int TOTAL_SLOTS = BodyRegionEnum.getTotalSlots();
    public static final int SLOTS_PER_PART = BodyRegionEnum.SLOTS_PER_PART;
    public static final int SLOT_EYES = BodyRegionEnum.EYES.getStartSlot();
    public static final int SLOT_BRAIN = BodyRegionEnum.BRAIN.getStartSlot();
    public static final int SLOT_HEART = BodyRegionEnum.HEART.getStartSlot();
    public static final int SLOT_LUNGS = BodyRegionEnum.LUNGS.getStartSlot();
    public static final int SLOT_STOMACH = BodyRegionEnum.STOMACH.getStartSlot();
    public static final int SLOT_SKIN = BodyRegionEnum.SKIN.getStartSlot();
    public static final int SLOT_MUSCLE = BodyRegionEnum.MUSCLE.getStartSlot();
    public static final int SLOT_BONES = BodyRegionEnum.BONES.getStartSlot();
    public static final int SLOT_ARMS = BodyRegionEnum.ARMS.getStartSlot();
    public static final int SLOT_HANDS = BodyRegionEnum.HANDS.getStartSlot();
    public static final int SLOT_LEGS = BodyRegionEnum.LEGS.getStartSlot();
    public static final int SLOT_BOOTS = BodyRegionEnum.BOOTS.getStartSlot();
    private final ItemStackHandler itemHandler = createItemHandler();
    private final ContainerData data;
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int progress = 0;
    private int maxProgress = 100;

    public RobosurgeonBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ROBO_SURGEON.get(), pPos, pBlockState);
        this.data = createContainerData();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RobosurgeonBlockEntity entity) {
        if (level.isClientSide)
            return;
        BlockPos chamberPos = entity.findChamberPos();
        if (chamberPos == null) {
            entity.resetProgress();
            return;
        }
        BlockEntity be = level.getBlockEntity(chamberPos);
        if (!(be instanceof SurgeryChamberBlockEntity chamber)) {
            entity.resetProgress();
            return;
        }
        LivingEntity patient = entity.findPatient(chamberPos);
        if (chamber.isOpen() || !(patient instanceof ServerPlayer serverPlayer)) {
            if (entity.progress > 0) {
                entity.resetProgress();
                syncProgress(entity, patient instanceof ServerPlayer sp ? sp : null);
            }
            return;
        }
        if (entity.needsSurgery(serverPlayer) && entity.checkRequirements(serverPlayer)) {
            entity.progress++;
            setChanged(level, pos, state);
            syncProgress(entity, serverPlayer);
            if (entity.progress % 20 == 0) {
                serverPlayer.hurt(level.damageSources().magic(), 1.0f);
                level.playSound(null, chamberPos, SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.5f, 1.0f);
                if (entity.progress % 40 == 0) {
                    level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.3F, 1.5F);
                    if (entity.progress % 80 == 0) {
                        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.BLOCKS, 0.2F, 0.8F);
                    }
                }
            }
            if (entity.progress >= entity.maxProgress) {
                entity.performSurgery(serverPlayer);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 2.0F);
                level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.5F, 1.0F);
                entity.resetProgress();
                syncProgress(entity, serverPlayer);
                chamber.setDoorState(true);
            }
        } else if (entity.progress > 0) {
            entity.resetProgress();
            syncProgress(entity, serverPlayer);
            chamber.setDoorState(true);
        }
    }

    private static void syncProgress(RobosurgeonBlockEntity entity, @Nullable ServerPlayer player) {
        if (player != null) {
            A_PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncSurgeryProgressPacket(entity.progress, entity.maxProgress));
        }
    }

    private boolean isGhost(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost");
    }

    private ICyberware getCyber(ItemStack stack) {
        return CyberwareAPI.getCyberware(stack);
    }

    public void performSurgery(ServerPlayer player) {
        if (!checkRequirements(player))
            return;
        if (MinecraftForge.EVENT_BUS.post(new CyberwareSurgeryEvent.Pre(player, this)))
            return;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
            SurgeryManager.execute(player, this.itemHandler, userData.getInstalledCyberware());
            userData.recalculateCapacity(player);
            userData.syncToClient(player);
            this.populateGhostItems(player);
            player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS,
                    1.0f, 1.0f);
            MinecraftForge.EVENT_BUS.post(new CyberwareSurgeryEvent.Post(player, this));
        });
    }

    public void populateGhostItems(ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
            if (SurgerySyncHelper.updateGhosts(userData.getInstalledCyberware(), this.itemHandler)) {
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        });
    }

    private boolean checkRequirements(ServerPlayer player) {
        var cap = player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
        if (!cap.isPresent())
            return false;
        ItemStackHandler playerBody = cap.resolve().get().getInstalledCyberware();
        java.util.Map<net.minecraft.world.item.Item, Integer> futureCounts = new java.util.HashMap<>();
        List<ItemStack> futureBody = new ArrayList<>();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack table = itemHandler.getStackInSlot(i);
            ItemStack finalStack = isGhost(table) ? playerBody.getStackInSlot(i) : table;
            if (!finalStack.isEmpty()) {
                futureBody.add(finalStack);
                futureCounts.put(finalStack.getItem(),
                        futureCounts.getOrDefault(finalStack.getItem(), 0) + finalStack.getCount());
            }
        }
        for (ItemStack stack : futureBody) {
            ICyberware cw = getCyber(stack);
            if (cw == null)
                continue;
            if (futureCounts.get(stack.getItem()) > cw.getMaxInstallAmount(stack))
                return false;
            for (net.minecraft.world.item.Item req : cw.getPrerequisites(stack)) {
                if (futureBody.stream().noneMatch(s -> s.is(req)))
                    return false;
            }
        }
        return true;
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(TOTAL_SLOTS) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw == null)
                    return false;
                return CyberwareSlotType.fromId(cw.getSlot(stack)) == CyberwareSlotType.fromId(slot);
            }
        };
    }

    private boolean needsSurgery(ServerPlayer player) {
        return player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).map(data -> {
            ItemStackHandler playerBody = data.getInstalledCyberware();
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                ItemStack table = itemHandler.getStackInSlot(i);
                if (isGhost(table))
                    continue;
                if (!ItemStack.matches(table, playerBody.getStackInSlot(i)))
                    return true;
            }
            return false;
        }).orElse(false);
    }

    private BlockPos findChamberPos() {
        BlockPos below = worldPosition.below();
        BlockState state = level.getBlockState(below);
        if (state.getBlock() instanceof SurgeryChamberBlock) {
            return state.getValue(SurgeryChamberBlock.HALF) == DoubleBlockHalf.UPPER ? below.below() : below;
        }
        return null;
    }

    private LivingEntity findPatient(BlockPos chamberPos) {
        AABB box = new AABB(chamberPos).deflate(0.3, 0.1, 0.3).inflate(0, 0.9, 0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
        return entities.isEmpty() ? null : entities.get(0);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.cyber_ware_port.robosurgeon");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        if (p instanceof ServerPlayer sp)
            populateGhostItems(sp);
        return new RobosurgeonMenu(id, inv, this, data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? lazyItemHandler.cast() : super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net,
                             net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("progress");
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(TOTAL_SLOTS);
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && !isGhost(stack))
                inv.setItem(i, stack);
        }
        Containers.dropContents(level, worldPosition, inv);
    }

    private ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int i) {
                return i == 0 ? progress : maxProgress;
            }

            @Override
            public void set(int i, int v) {
                if (i == 0)
                    progress = v;
                else
                    maxProgress = v;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public IItemHandlerModifiable getItemHandler() {
        return itemHandler;
    }
}