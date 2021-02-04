/*******************************************************************************
 * Linking Books
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
package thefloydman.linkingbooks.tileentity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.util.Constants.NBT;
import thefloydman.linkingbooks.integration.ImmersivePortalsIntegration;
import thefloydman.linkingbooks.util.Reference;

public class LinkTranslatorTileEntity extends LinkingBookHolderTileEntity {

    protected Set<Entity> immersivePortalsEntities = new HashSet<Entity>();

    public LinkTranslatorTileEntity() {
        super(ModTileEntityTypes.LINK_TRANSLATOR.get());
    }

    public void addImmersivePortalsEntity(Entity entity) {
        this.immersivePortalsEntities.add(entity);
        this.markDirty();
    }

    public void removeImmersivePortalsEntity(Entity entity) {
        this.immersivePortalsEntities.remove(entity);
        this.markDirty();
    }

    protected void clearImmersivePortalsEntities() {
        this.immersivePortalsEntities.clear();
        this.markDirty();
    }

    public void deleteImmersivePortals() {
        for (Entity entity : this.immersivePortalsEntities) {
            entity.remove();
        }
        this.clearImmersivePortalsEntities();
        this.markDirty();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("entities", NBT.TAG_LIST) && Reference.isModLoaded("immersive_portals")) {
            ListNBT list = nbt.getList("entities", NBT.TAG_INT_ARRAY);
            if (this.world != null) {
                List<Entity> worldEntities = ImmersivePortalsIntegration.getNearbyLinkingPortals(this.getPos(),
                        this.getWorld());
                for (INBT tag : list) {
                    UUID uuid = NBTUtil.readUniqueId(tag);
                    for (Entity entity : worldEntities) {
                        if (uuid.equals(entity.getUniqueID())) {
                            this.addImmersivePortalsEntity(entity);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        ListNBT list = new ListNBT();
        for (Entity entity : this.immersivePortalsEntities) {
            list.add(NBTUtil.func_240626_a_(entity.getUniqueID()));
        }
        nbt.put("entities", list);
        return nbt;
    }

}
