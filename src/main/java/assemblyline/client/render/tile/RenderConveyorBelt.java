package assemblyline.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import assemblyline.client.ClientRegister;
import assemblyline.common.tile.TileConveyorBelt;
import assemblyline.common.tile.TileConveyorBelt.ConveyorType;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.utilities.UtilitiesRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class RenderConveyorBelt implements BlockEntityRenderer<TileConveyorBelt> {

    public RenderConveyorBelt(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileConveyorBelt tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
	    int combinedOverlayIn) {
	matrixStackIn.pushPose();
	ComponentInventory inv = tile.getComponent(ComponentType.Inventory);
	ItemStack stack = inv.getItem(0);
	Vector3f itemVec = tile.getObjectLocal();
	Vector3f move = tile.getDirectionAsVector();
	Direction direct = tile.<ComponentDirection>getComponent(ComponentType.Direction).getDirection().getOpposite();
	move.mul(partialTicks / 16.0f);
	if (tile.running) {
	    itemVec.add(move);
	}
	matrixStackIn.pushPose();
	ResourceLocation location = ClientRegister.MODEL_CONVEYOR;
	if (tile.running) {
	    location = ClientRegister.MODEL_CONVEYORANIMATED;
	}
	Vector3f move2 = tile.getDirectionAsVector();
	Vector3f dir = new Vector3f(Math.abs(move2.x()), Math.abs(move2.y()), Math.abs(move2.z()));
	switch (tile.conveyorType) {
	case Horizontal:
	    matrixStackIn.translate(itemVec.x(), stack.getItem() instanceof BlockItem ? 0.48 : 0.33, itemVec.z());
	    matrixStackIn.scale(0.35f, 0.35f, 0.35f);
	    if (!(stack.getItem() instanceof BlockItem)) {
		matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
	    }
	    break;
	case SlopedDown:
	    double verticalComponent2 = 0;
	    if (move2.x() + move2.y() + move2.z() < 0) {
		verticalComponent2 = 1 - Mth.clampedLerp(0, 1, itemVec.dot(dir));
	    } else {
		verticalComponent2 = Mth.clampedLerp(0, 1, itemVec.dot(dir));
	    }
	    matrixStackIn.translate(itemVec.x(), (stack.getItem() instanceof BlockItem ? 0.48 : 0.33) - verticalComponent2 + 2 / 16.0, itemVec.z());
	    matrixStackIn.scale(0.35f, 0.35f, 0.35f);
	    if (!(stack.getItem() instanceof BlockItem)) {
		matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
	    }
	    int rotate = -45;
	    if (direct == Direction.NORTH) {
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
	    } else if (direct == Direction.EAST) {
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
	    } else if (direct == Direction.WEST) {
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90));
	    }
	    matrixStackIn.mulPose(direct == Direction.NORTH ? Vector3f.XN.rotationDegrees(rotate)
		    : direct == Direction.SOUTH ? Vector3f.XP.rotationDegrees(-rotate)
			    : direct == Direction.WEST ? Vector3f.XN.rotationDegrees(rotate) : Vector3f.XP.rotationDegrees(-rotate));
	    location = tile.running ? ClientRegister.MODEL_SLOPEDCONVEYORDOWNANIMATED : ClientRegister.MODEL_SLOPEDCONVEYORDOWN;
	    break;
	case SlopedUp:
	    double verticalComponent = 0;
	    if (move2.x() + move2.y() + move2.z() < 0) {
		verticalComponent = 1 - Mth.clampedLerp(0, 1, itemVec.dot(dir));
	    } else {
		verticalComponent = Mth.clampedLerp(0, 1, itemVec.dot(dir));
	    }
	    matrixStackIn.translate(itemVec.x(), (stack.getItem() instanceof BlockItem ? 0.48 : 0.33) + verticalComponent + 2 / 16.0, itemVec.z());
	    matrixStackIn.scale(0.35f, 0.35f, 0.35f);
	    if (!(stack.getItem() instanceof BlockItem)) {
		matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
	    }
	    rotate = 45;
	    if (direct == Direction.NORTH) {
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
	    } else if (direct == Direction.EAST) {
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
	    } else if (direct == Direction.WEST) {
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90));
	    }
	    matrixStackIn.mulPose(direct == Direction.NORTH ? Vector3f.XN.rotationDegrees(rotate)
		    : direct == Direction.SOUTH ? Vector3f.XP.rotationDegrees(-rotate)
			    : direct == Direction.WEST ? Vector3f.XN.rotationDegrees(rotate) : Vector3f.XP.rotationDegrees(-rotate));
	    location = tile.running ? ClientRegister.MODEL_SLOPEDCONVEYORUPANIMATED : ClientRegister.MODEL_SLOPEDCONVEYORUP;
	    break;
	case Vertical:
	    double verticalComponent3 = 0;
	    if (tile.getLevel().getBlockEntity(tile.getBlockPos().below())instanceof TileConveyorBelt belt
		    && belt.conveyorType == ConveyorType.Vertical) {
		location = tile.running ? ClientRegister.MODEL_ELEVATORRUNNING : ClientRegister.MODEL_ELEVATOR;
	    } else {
		location = tile.running ? ClientRegister.MODEL_ELEVATORBOTTOMRUNNING : ClientRegister.MODEL_ELEVATORBOTTOM;
	    }
	    if (move2.x() + move2.y() + move2.z() < 0) {
		verticalComponent3 = 1 - Mth.clampedLerp(0, 1, itemVec.dot(dir));
	    } else {
		verticalComponent3 = Mth.clampedLerp(0, 1, itemVec.dot(dir));
	    }
	    matrixStackIn.translate(0.5, (stack.getItem() instanceof BlockItem ? 0.48 : 0.33) + verticalComponent3, 0.5);
	    matrixStackIn.scale(0.35f, 0.35f, 0.35f);
	    if (!(stack.getItem() instanceof BlockItem)) {
		matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
	    }
	    break;
	default:
	    break;
	}
	Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.NONE, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn,
		0);
	matrixStackIn.popPose();

	BakedModel model = Minecraft.getInstance().getModelManager().getModel(location);

	matrixStackIn.pushPose();
	matrixStackIn.translate(0, 1 / 16.0, 0);
	UtilitiesRendering.prepareRotationalTileModel(tile, matrixStackIn);
	if (tile.conveyorType == ConveyorType.SlopedDown) {
	    matrixStackIn.translate(0, -1, 0);
	    matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));
	}
	UtilitiesRendering.renderModel(model, tile, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
	matrixStackIn.popPose();

	if (tile.isPusher || tile.isPuller) {
	    model = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_MANIPULATOR);
	    move = tile.getDirectionAsVector();
	    if (tile.isPusher) {
		BlockPos nextBlockPos = tile.getNextPos().subtract(tile.getBlockPos());
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, 1 / 16.0, 0);
		matrixStackIn.translate(nextBlockPos.getX() - move.x(), nextBlockPos.getY() - move.y(), nextBlockPos.getZ() - move.z());
		UtilitiesRendering.prepareRotationalTileModel(tile, matrixStackIn);
		UtilitiesRendering.renderModel(model, tile, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
	    }
	    if (tile.isPuller) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, 1 / 16.0, 0);
		UtilitiesRendering.prepareRotationalTileModel(tile, matrixStackIn);
		if (tile.conveyorType == ConveyorType.SlopedUp) {
		    matrixStackIn.translate(0, 0.4, 0);
		}
		matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));
		UtilitiesRendering.renderModel(model, tile, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		matrixStackIn.popPose();
	    }
	}
	matrixStackIn.popPose();

    }
}