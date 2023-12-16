package net.nootovich.craftorio.blocks.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import net.nootovich.craftorio.BeltPath;
import net.nootovich.craftorio.OptimizedBeltPath;
import net.nootovich.craftorio.blocks.entity.BeltBlockEntity;
import org.joml.Matrix4f;

public class DebugBeltBlockEntityRenderer implements BlockEntityRenderer<BeltBlockEntity> {

    private static final int COLOR = 0x69DEAF;
    private static final int ALPHA = 0xDA;

    public DebugBeltBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(BeltBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Vec3 pos = Vec3.atLowerCornerOf(pBlockEntity.getBlockPos());

        OptimizedBeltPath pathL       = pBlockEntity.leftPath;
        Vec3              drawStartL  = pathL.start.subtract(pos).add(0, 1, 0);
        Vec3              relStartL   = BeltPath.rotateFromDir(drawStartL, pathL.dir);
        Vec3              relStartL2  = relStartL.subtract(new Vec3(.1, 0, 0));
        Vec3              relEndL     = relStartL.add(new Vec3(0, 0, -pathL.len));
        Vec3              drawStartL2 = BeltPath.rotateToDir(relStartL2, pathL.dir);
        Vec3              drawEndL    = BeltPath.rotateToDir(relEndL, pathL.dir);

        OptimizedBeltPath pathR       = pBlockEntity.rightPath;
        Vec3              drawStartR  = pathR.start.subtract(pos).add(0, 1, 0);
        Vec3              relStartR   = BeltPath.rotateFromDir(drawStartR, pathR.dir);
        Vec3              relStartR2  = relStartR.add(new Vec3(.1, 0, 0));
        Vec3              relEndR     = relStartR.add(new Vec3(0, 0, -pathR.len));
        Vec3              drawStartR2 = BeltPath.rotateToDir(relStartR2, pathR.dir);
        Vec3              drawEndR    = BeltPath.rotateToDir(relEndR, pathR.dir);

        renderTri(pPoseStack, pBuffer, drawStartL, drawStartL2, drawEndL, COLOR, ALPHA);
        renderTri(pPoseStack, pBuffer, drawStartR, drawStartR2, drawEndR, -COLOR, ALPHA);
    }

    private void renderTri(PoseStack pPoseStack, MultiBufferSource pBuffer, Vec3 pos1, Vec3 pos2, Vec3 pos3, int color, int alpha) {
        Matrix4f       matrix4f = pPoseStack.last().pose();
        VertexConsumer vc       = pBuffer.getBuffer(RenderType.debugQuads());

        color = (alpha<<24)|color;
        vc.vertex(matrix4f, (float) pos1.x(), (float) pos1.y(), (float) pos1.z()).color(color).endVertex();
        vc.vertex(matrix4f, (float) pos2.x(), (float) pos2.y(), (float) pos2.z()).color(color).endVertex();

        color = (alpha<<24)|(-color&0xFFFFFF);
        vc.vertex(matrix4f, (float) pos3.x(), (float) pos3.y(), (float) pos3.z()).color(color).endVertex();
        vc.vertex(matrix4f, (float) pos3.x(), (float) pos3.y(), (float) pos3.z()).color(color).endVertex();
    }
}
