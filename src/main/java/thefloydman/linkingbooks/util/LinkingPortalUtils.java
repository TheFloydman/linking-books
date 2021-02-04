/*******************************************************************************
 * Linking Books - Fabric
 * Copyright (C) 2021  TheFloydman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can reach TheFloydman on Discord at Floydman#7171.
 *******************************************************************************/
package thefloydman.linkingbooks.util;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.EntitySize;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import thefloydman.linkingbooks.api.capability.ILinkData;
import thefloydman.linkingbooks.block.LinkingPortalBlock;
import thefloydman.linkingbooks.block.ModBlocks;
import thefloydman.linkingbooks.world.storage.LinkingBooksSavedData;

public class LinkingPortalUtils {

    private static final AbstractBlock.IPositionPredicate IS_VALID_FRAME_BLOCK = (blockState, blockView, blockPos) -> {
        return blockState.isIn(ModBlocks.NARA.get()) || blockState.isIn(ModBlocks.LINK_TRANSLATOR.get());
    };
    private final IWorld world;
    public final Direction.Axis axis;
    private final Direction negativeDir;
    private int foundPortalBlocks;
    @Nullable
    public BlockPos lowerCorner;
    public int height;
    public int width;

    public static Optional<LinkingPortalUtils> canMakePortal(IWorld worldAccess, BlockPos blockPos,
            Direction.Axis axis) {
        return canMakePortal(worldAccess, blockPos, (linkingPortalUtil) -> {
            return linkingPortalUtil.isValid() && linkingPortalUtil.foundPortalBlocks == 0;
        }, axis);
    }

    public static Optional<LinkingPortalUtils> canErasePortal(IWorld worldAccess, BlockPos blockPos,
            Direction.Axis axis) {
        return canMakePortal(worldAccess, blockPos, (linkingPortalUtil) -> {
            return linkingPortalUtil.isValid() && linkingPortalUtil.foundPortalBlocks > 0;
        }, axis);
    }

    public static Optional<LinkingPortalUtils> canMakePortal(IWorld worldAccess, BlockPos blockPos,
            Predicate<LinkingPortalUtils> predicate, Direction.Axis axis) {
        Optional<LinkingPortalUtils> optional = Optional.of(new LinkingPortalUtils(worldAccess, blockPos, axis))
                .filter(predicate);
        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new LinkingPortalUtils(worldAccess, blockPos, axis2)).filter(predicate);
        }
    }

    public LinkingPortalUtils(IWorld worldAccess, BlockPos blockPos, Direction.Axis axis) {
        this.world = worldAccess;
        this.axis = axis;
        this.negativeDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.lowerCorner = this.method_30492(blockPos);
        if (this.lowerCorner == null) {
            this.lowerCorner = blockPos;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.method_30495();
            if (this.width > 0) {
                this.height = this.method_30496();
            }
        }

    }

    @Nullable
    private BlockPos method_30492(BlockPos blockPos) {
        for (int i = Math.max(0, blockPos.getY() - 21); blockPos.getY() > i
                && validStateInsidePortal(this.world.getBlockState(blockPos.down())); blockPos = blockPos.down()) {
        }

        Direction direction = this.negativeDir.getOpposite();
        int j = this.method_30493(blockPos, direction) - 1;
        return j < 0 ? null : blockPos.offset(direction, j);
    }

    private int method_30495() {
        int i = this.method_30493(this.lowerCorner, this.negativeDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int method_30493(BlockPos blockPos, Direction direction) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i <= 21; ++i) {
            mutable.setPos(blockPos).move(direction, i);
            BlockState blockState = this.world.getBlockState(mutable);
            if (!validStateInsidePortal(blockState)) {
                if (IS_VALID_FRAME_BLOCK.test(blockState, this.world, mutable)) {
                    return i;
                }
                break;
            }

            BlockState blockState2 = this.world.getBlockState(mutable.move(Direction.DOWN));
            if (!IS_VALID_FRAME_BLOCK.test(blockState2, this.world, mutable)) {
                break;
            }
        }

        return 0;
    }

    private int method_30496() {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = this.method_30490(mutable);
        return i >= 3 && i <= 21 && this.method_30491(mutable, i) ? i : 0;
    }

    private boolean method_30491(BlockPos.Mutable mutable, int i) {
        for (int j = 0; j < this.width; ++j) {
            BlockPos.Mutable mutable2 = mutable.setPos(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir,
                    j);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable2), this.world, mutable2)) {
                return false;
            }
        }

        return true;
    }

    private int method_30490(BlockPos.Mutable mutable) {
        for (int i = 0; i < 21; ++i) {
            mutable.setPos(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable), this.world, mutable)) {
                return i;
            }

            mutable.setPos(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable), this.world, mutable)) {
                return i;
            }

            for (int j = 0; j < this.width; ++j) {
                mutable.setPos(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
                BlockState blockState = this.world.getBlockState(mutable);
                if (!validStateInsidePortal(blockState)) {
                    return i;
                }

                if (blockState.isIn(ModBlocks.LINKING_PORTAL.get())) {
                    ++this.foundPortalBlocks;
                }
            }
        }

        return 21;
    }

    private static boolean validStateInsidePortal(BlockState blockState) {
        return blockState.isAir() || blockState.isIn(BlockTags.FIRE) || blockState.isIn(ModBlocks.LINKING_PORTAL.get());
    }

    public boolean isValid() {
        return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortal(ILinkData linkData) {
        BlockState blockState = ModBlocks.LINKING_PORTAL.get().getDefaultState().with(LinkingPortalBlock.AXIS,
                this.axis);
        BlockPos.getAllInBoxMutable(this.lowerCorner,
                this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
                .forEach((blockPos) -> {
                    this.world.setBlockState(blockPos, blockState, 18);
                    if (this.world instanceof ServerWorld) {
                        LinkingBooksSavedData persistentState = ((ServerWorld) this.world).getSavedData()
                                .getOrCreate(LinkingBooksSavedData::new, Reference.MOD_ID);
                        persistentState.addLinkingPortalData(blockPos, linkData);
                    }
                });
    }

    public void erasePortal() {
        BlockState blockState = Blocks.AIR.getDefaultState();
        BlockPos.getAllInBoxMutable(this.lowerCorner,
                this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
                .forEach((blockPos) -> {
                    this.world.setBlockState(blockPos, blockState, 18);
                    if (this.world instanceof ServerWorld) {
                        LinkingBooksSavedData persistentState = ((ServerWorld) this.world).getSavedData()
                                .getOrCreate(LinkingBooksSavedData::new, Reference.MOD_ID);
                        persistentState.removeLinkingPortalData(blockPos);
                    }
                });
    }

    public boolean wasAlreadyValid() {
        return this.isValid() && this.foundPortalBlocks == this.width * this.height;
    }

    public static Vector3d method_30494(TeleportationRepositioner.Result arg, Direction.Axis axis, Vector3d vec3d,
            EntitySize entityDimensions) {
        double d = (double) arg.width - (double) entityDimensions.width;
        double e = (double) arg.height - (double) entityDimensions.height;
        BlockPos blockPos = arg.startPos;
        double h;
        if (d > 0.0D) {
            float f = blockPos.func_243648_a(axis) + entityDimensions.width / 2.0F;
            h = MathHelper.clamp(MathHelper.func_233020_c_(vec3d.getCoordinate(axis) - f, 0.0D, d), 0.0D, 1.0D);
        } else {
            h = 0.5D;
        }

        double j;
        Direction.Axis axis3;
        if (e > 0.0D) {
            axis3 = Direction.Axis.Y;
            j = MathHelper.clamp(
                    MathHelper.func_233020_c_(vec3d.getCoordinate(axis3) - blockPos.func_243648_a(axis3), 0.0D, e),
                    0.0D, 1.0D);
        } else {
            j = 0.0D;
        }

        axis3 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double k = vec3d.getCoordinate(axis3) - (blockPos.func_243648_a(axis3) + 0.5D);
        return new Vector3d(h, j, k);
    }

    public static PortalInfo method_30484(ServerWorld serverWorld, TeleportationRepositioner.Result arg,
            Direction.Axis axis, Vector3d vec3d, EntitySize entityDimensions, Vector3d vec3d2, float f, float g) {
        BlockPos blockPos = arg.startPos;
        BlockState blockState = serverWorld.getBlockState(blockPos);
        Direction.Axis axis2 = blockState.get(BlockStateProperties.HORIZONTAL_AXIS);
        double d = arg.width;
        double e = arg.height;
        int i = axis == axis2 ? 0 : 90;
        Vector3d vec3d3 = axis == axis2 ? vec3d2 : new Vector3d(vec3d2.z, vec3d2.y, -vec3d2.x);
        double h = entityDimensions.width / 2.0D + (d - entityDimensions.width) * vec3d.getX();
        double j = (e - entityDimensions.height) * vec3d.getY();
        double k = 0.5D + vec3d.getZ();
        boolean bl = axis2 == Direction.Axis.X;
        Vector3d vec3d4 = new Vector3d(blockPos.getX() + (bl ? h : k), blockPos.getY() + j,
                blockPos.getZ() + (bl ? k : h));
        return new PortalInfo(vec3d4, vec3d3, f + i, g);
    }

}
