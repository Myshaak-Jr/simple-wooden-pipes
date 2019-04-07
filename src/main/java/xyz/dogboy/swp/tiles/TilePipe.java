package xyz.dogboy.swp.tiles;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import xyz.dogboy.swp.blocks.BlockWoodenVariation;
import xyz.dogboy.swp.client.ClientProxy;
import xyz.dogboy.swp.config.SWPConfig;

public class TilePipe extends PersistantSyncableTileEntity implements ITickable, IFluidHandler, WoodenVariationProvider {

    private Fluid fluid;
    private int amount;

    @Override
    public void update() {
        if (this.fluid != null && this.fluid.getTemperature() >= 550) {
            this.getWorld().setBlockState(this.getPos(), Blocks.FIRE.getDefaultState());
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (this.fluid == null || this.amount <= 0) {
                break;
            }

            BlockPos pos = this.getPos().offset(facing);
            TileEntity tileEntity = this.getWorld().getTileEntity(pos);
            if (tileEntity == null) {
                continue;
            }

            IFluidHandler fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if (fluidHandler == null) {
                continue;
            }
            boolean isPipe = tileEntity instanceof TilePipe;
            if (isPipe && ((TilePipe) tileEntity).amount > this.amount) {
                continue;
            }

            this.amount -= fluidHandler.fill(new FluidStack(this.fluid, Math.min(SWPConfig.transferRate, this.amount)), true);
        }
    }

    private FluidStack getFluidStack() {
        if (this.amount <= 0) {
            return null;
        }

        return new FluidStack(this.fluid, this.amount);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this;
        }

        return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { new FluidTankProperties(this.getFluidStack(), SWPConfig.internalVolume) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }

        FluidStack current = this.getFluidStack();

        int maxFill = current == null
                ? Math.min(SWPConfig.internalVolume, resource.amount)
                : current.isFluidEqual(resource) ? Math.min(SWPConfig.internalVolume - current.amount, resource.amount) : 0;

        if (!doFill) {
            return maxFill;
        }

        if (current == null) {
            this.fluid = resource.getFluid();
            this.amount = resource.amount;
            this.triggerUpdate();
            return resource.amount;
        }

        if (current.isFluidEqual(resource)) {
            this.amount += maxFill;
            this.triggerUpdate();
            return maxFill;
        }

        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }

        FluidStack current = this.getFluidStack();
        if (current == null) {
            return null;
        }

        return this.drain(new FluidStack(current.getFluid(), maxDrain), doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }

        FluidStack current = this.getFluidStack();
        if (current == null) {
            return null;
        }

        int maxDrain = Math.min(SWPConfig.transferRate, Math.min(current.amount, resource.amount));

        if (!doDrain) {
            return new FluidStack(current.getFluid(), maxDrain);
        }

        this.amount -= maxDrain;
        this.triggerUpdate();
        return new FluidStack(current.getFluid(), maxDrain);
    }

    @Override
    protected void writeData(NBTTagCompound tagCompound) {
        FluidStack current = this.getFluidStack();
        if (current != null) {
            tagCompound.setString("FluidName", FluidRegistry.getFluidName(current.getFluid()));
            tagCompound.setInteger("FluidAmount", current.amount);
        }
    }

    @Override
    protected void readData(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("FluidName") && tagCompound.hasKey("FluidAmount")) {
            String fluidName = tagCompound.getString("FluidName");
            int fluidAmount = tagCompound.getInteger("FluidAmount");

            Fluid fluid = FluidRegistry.getFluid(fluidName);
            if (fluid == null) {
                return;
            }

            this.fluid = fluid;
            this.amount = fluidAmount;
        }
    }

    public IExtendedBlockState writeExtendedState(IExtendedBlockState state) {
        String texture = this.getTileData().getString("Texture");

        if (texture.isEmpty()) {
            NBTTagCompound baseBlock = this.getTileData().getCompoundTag("BaseBlock");
            ItemStack stack = new ItemStack(baseBlock);
            if (!stack.isEmpty()) {
                Block block = Block.getBlockFromItem(stack.getItem());
                if (block != Blocks.AIR) {
                    texture = ClientProxy.getTextureFromBlock(block, stack.getItemDamage());
                    this.getTileData().setString("Texture", texture);
                }
            }
        }

        return state.withProperty(BlockWoodenVariation.TEXTURE, texture.isEmpty() ? "minecraft:blocks/planks_oak" : texture);
    }

}
