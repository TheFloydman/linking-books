package thefloydman.linkingbooks.api.capability;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import thefloydman.linkingbooks.api.linking.LinkEffect;

public interface ILinkData {

    public void setDimension(ResourceLocation dimension);

    public ResourceLocation getDimension();

    public void setPosition(BlockPos position);

    public BlockPos getPosition();

    public void setRotation(float rotation);

    public float getRotation();

    public void setLinkEffects(Set<LinkEffect> effects);

    public Set<LinkEffect> getLinkEffects();

    public boolean addLinkEffect(LinkEffect effect);

    public boolean removeLinkEffect(LinkEffect effect);

}
