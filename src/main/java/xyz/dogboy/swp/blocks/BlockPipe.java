package xyz.dogboy.swp.blocks;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import xyz.dogboy.swp.Registry;
import xyz.dogboy.swp.tiles.TilePipe;
import xyz.dogboy.swp.tiles.TilePump;

public class BlockPipe extends BlockWoodenVariation {


    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    public static final PropertyBool EXTRACTION = PropertyBool.create("extraction");
    public static final PropertyBool EXTRACT_NORTH = PropertyBool.create("extract_north");
    public static final PropertyBool EXTRACT_EAST = PropertyBool.create("extract_east");
    public static final PropertyBool EXTRACT_SOUTH = PropertyBool.create("extract_south");
    public static final PropertyBool EXTRACT_WEST = PropertyBool.create("extract_west");
    public static final PropertyBool EXTRACT_UP = PropertyBool.create("extract_up");
    public static final PropertyBool EXTRACT_DOWN = PropertyBool.create("extract_down");

    public static final AxisAlignedBB MIDDLE_BB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
    public static final AxisAlignedBB NORTH_BB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.25);
    public static final AxisAlignedBB EAST_BB = new AxisAlignedBB(1, 0.3125, 0.3125, 0.75, 0.6875, 0.6875);
    public static final AxisAlignedBB SOUTH_BB = new AxisAlignedBB(0.3125, 0.3125, 1, 0.6875, 0.6875, 0.75);
    public static final AxisAlignedBB WEST_BB = new AxisAlignedBB(0, 0.3125, 0.3125, 0.25, 0.6875, 0.6875);
    public static final AxisAlignedBB UP_BB = new AxisAlignedBB(0.3125, 1, 0.3125, 0.6875, 0.75, 0.6875);
    public static final AxisAlignedBB DOWN_BB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.25, 0.6875);

    public BlockPipe() {
        super("pipe", Material.WOOD, MapColor.WOOD);
        this.setHardness(1.0F);
        this.setResistance(2.0F);
        this.setSoundType(SoundType.WOOD);

        this.setDefaultState(this.getBlockState().getBaseState()
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(UP, false)
                .withProperty(DOWN, false)
                .withProperty(EXTRACTION, false)
                .withProperty(EXTRACT_NORTH, false)
                .withProperty(EXTRACT_EAST, false)
                .withProperty(EXTRACT_SOUTH, false)
                .withProperty(EXTRACT_WEST, false)
                .withProperty(EXTRACT_UP, false)
                .withProperty(EXTRACT_DOWN, false));
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (!isActualState) {
            state = state.getActualState(worldIn, pos);
        }

        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, MIDDLE_BB);

        if (state.getValue(NORTH))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_BB);

        if (state.getValue(EAST))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_BB);

        if (state.getValue(SOUTH))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_BB);

        if (state.getValue(WEST))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_BB);

        if (state.getValue(UP))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_BB);

        if (state.getValue(DOWN))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_BB);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = this.getActualState(state, source, pos);

        AxisAlignedBB boundingBox = MIDDLE_BB;

        if (state.getValue(NORTH))
            boundingBox = boundingBox.union(NORTH_BB);

        if (state.getValue(EAST))
            boundingBox = boundingBox.union(EAST_BB);

        if (state.getValue(SOUTH))
            boundingBox = boundingBox.union(SOUTH_BB);

        if (state.getValue(WEST))
            boundingBox = boundingBox.union(WEST_BB);

        if (state.getValue(UP))
            boundingBox = boundingBox.union(UP_BB);

        if (state.getValue(DOWN))
            boundingBox = boundingBox.union(DOWN_BB);

        return boundingBox;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TilePipe pipe = (TilePipe) worldIn.getTileEntity(pos);
        if (pipe == null) {
            return false;
        }

        IFluidHandlerItem fluidHandler = playerIn.getHeldItem(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidHandler != null) {
            return this.handleFluidHandlerActivate(playerIn, hand, pipe, fluidHandler);
        }

        if (playerIn.getHeldItem(hand).getItem() == Item.getItemFromBlock(Blocks.PISTON) || playerIn.isSneaking()) {
            return this.handlePistonActivate(playerIn, hand, pipe);
        }

        return false;
    }

    private boolean handleFluidHandlerActivate(EntityPlayer playerIn, EnumHand hand, TilePipe pipe, IFluidHandlerItem fluidHandler) {
        IFluidTankProperties tankProperties = pipe.getTankProperties()[0];
        int maxDrain = tankProperties.getCapacity() - (tankProperties.getContents() == null ? 0 : tankProperties.getContents().amount);
        if (maxDrain <= 0) {
            return false;
        }

        FluidStack drained = fluidHandler.drain(maxDrain, false);
        if (drained == null) {
            return false;
        }

        maxDrain = pipe.fill(drained, false);
        if (maxDrain <= 0) {
            return false;
        }

        drained = fluidHandler.drain(maxDrain, true);
        if (drained == null) {
            return false;
        }

        pipe.fill(drained, true);
        playerIn.setHeldItem(hand, fluidHandler.getContainer());

        return true;
    }

    private boolean handlePistonActivate(EntityPlayer playerIn, EnumHand hand, TilePipe pipe) {
        if (playerIn.isSneaking()) {
            if (!pipe.isExtractionEnabled()) {
                return false;
            }

            pipe.setExtractionEnabled(false);

            if (!playerIn.capabilities.isCreativeMode) {
                if (!playerIn.addItemStackToInventory(new ItemStack(Blocks.PISTON))) {
                    playerIn.dropItem(new ItemStack(Blocks.PISTON), false);
                }
            }

            playerIn.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1, 1);
        } else {
            if (pipe.isExtractionEnabled()) {
                return false;
            }

            pipe.setExtractionEnabled(true);

            if (!playerIn.capabilities.isCreativeMode) {
                playerIn.getHeldItem(hand).shrink(1);
            }

            playerIn.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (!stack.hasTagCompound()) {
            return;
        }

        NBTTagCompound baseBlock = stack.getTagCompound().getCompoundTag("BaseBlock");
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TilePipe) {
            tileEntity.getTileData().setTag("BaseBlock", baseBlock);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePipe();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{ NORTH, EAST, SOUTH, WEST, UP, DOWN, EXTRACTION, EXTRACT_NORTH, EXTRACT_EAST, EXTRACT_SOUTH, EXTRACT_WEST, EXTRACT_UP, EXTRACT_DOWN },
                new IUnlistedProperty[]{ BlockWoodenVariation.TEXTURE });
    }

    public boolean canConnectTo(IBlockAccess world, BlockPos pipePos, EnumFacing direction, boolean excludePipe) {
        TileEntity tileEntity = world.getTileEntity(pipePos.offset(direction));
        return tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())
                && (!excludePipe || !(tileEntity instanceof TilePipe || tileEntity instanceof TilePump));
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn instanceof ChunkCache
                ? ((ChunkCache)worldIn).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
                : worldIn.getTileEntity(pos);
        boolean extraction = tileentity instanceof TilePipe && ((TilePipe) tileentity).isExtractionEnabled();

        return state.withProperty(NORTH, this.canConnectTo(worldIn, pos, EnumFacing.NORTH, false))
                .withProperty(EAST, this.canConnectTo(worldIn, pos, EnumFacing.EAST, false))
                .withProperty(SOUTH, this.canConnectTo(worldIn, pos, EnumFacing.SOUTH, false))
                .withProperty(WEST, this.canConnectTo(worldIn, pos, EnumFacing.WEST, false))
                .withProperty(UP, this.canConnectTo(worldIn, pos, EnumFacing.UP, false))
                .withProperty(DOWN, this.canConnectTo(worldIn, pos, EnumFacing.DOWN, false))
                .withProperty(EXTRACTION, extraction)
                .withProperty(EXTRACT_NORTH, extraction && this.canConnectTo(worldIn, pos, EnumFacing.NORTH, true))
                .withProperty(EXTRACT_EAST, extraction && this.canConnectTo(worldIn, pos, EnumFacing.EAST, true))
                .withProperty(EXTRACT_SOUTH, extraction && this.canConnectTo(worldIn, pos, EnumFacing.SOUTH, true))
                .withProperty(EXTRACT_WEST, extraction && this.canConnectTo(worldIn, pos, EnumFacing.WEST, true))
                .withProperty(EXTRACT_UP, extraction && this.canConnectTo(worldIn, pos, EnumFacing.UP, true))
                .withProperty(EXTRACT_DOWN, extraction && this.canConnectTo(worldIn, pos, EnumFacing.DOWN, true));
    }

    public ItemStack getItem(IBlockAccess world, BlockPos pos) {
        ItemStack itemStack = new ItemStack(Registry.PIPE_ITEM);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TilePipe) {
            itemStack.setTagCompound(new NBTTagCompound());
            itemStack.getTagCompound().setTag("BaseBlock", tileEntity.getTileData().getCompoundTag("BaseBlock").copy());
        }
        return itemStack;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return this.getItem(worldIn, pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(this.getItem(world, pos));

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TilePipe && ((TilePipe) tileEntity).isExtractionEnabled()) {
            drops.add(new ItemStack(Blocks.PISTON));
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

}
