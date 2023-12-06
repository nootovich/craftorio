package net.nootovich.craftorio.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.nootovich.craftorio.entities.custom.CraftorioItemEntity;
import org.jetbrains.annotations.NotNull;

public class CraftorioItemEntityRenderer extends EntityRenderer<CraftorioItemEntity> {

    private final ItemRenderer itemRenderer;

    public CraftorioItemEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(CraftorioItemEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        ItemStack  itemstack  = pEntity.getItem();
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, pEntity.level(), null, pEntity.getId());

        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotation(pEntityYaw));
        pMatrixStack.translate(0, (itemstack.getItem() instanceof BlockItem) ? 0 : 0.25f, 0);

        this.itemRenderer.render(
            itemstack, ItemDisplayContext.GROUND, false, pMatrixStack, pBuffer,
            pPackedLight, OverlayTexture.NO_OVERLAY, bakedmodel);

        pMatrixStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(CraftorioItemEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
