package assemblyline.client.render.tile;


import com.mojang.blaze3d.matrix.MatrixStack;

import assemblyline.client.ClientRegister;
import assemblyline.common.tile.TileConveyorBelt;
import assemblyline.common.tile.TileConveyorBelt.ConveyorType;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class RenderConveyorBelt extends AbstractTileRenderer<TileConveyorBelt> {

	public RenderConveyorBelt(TileEntityRendererDispatcher context) {
		super(context);
	}

	@Override
	public void render(TileConveyorBelt tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		matrixStackIn.pushPose();

		ComponentInventory inv = tile.getComponent(IComponentType.Inventory);

		ItemStack stack = inv.getItem(0);

		Vector3f itemVec = tile.getObjectLocal();

		Vector3f move = tile.getDirectionAsVector();

		Direction direct = tile.getFacing().getOpposite();

		if (ConveyorType.values()[tile.conveyorType.get()] != ConveyorType.Horizontal) {

			move.add(0, ConveyorType.values()[tile.conveyorType.get()] == ConveyorType.SlopedDown ? -1 : 1, 0);

		}

		move.mul(partialTicks / 16.0f);

		if (tile.running.get()) {

			itemVec.add(move);

		}

		matrixStackIn.pushPose();

		ResourceLocation location = ClientRegister.MODEL_CONVEYOR;

		if (tile.running.get()) {

			location = ClientRegister.MODEL_CONVEYORANIMATED;

		}

		switch (ConveyorType.values()[tile.conveyorType.get()]) {

		case Horizontal:

			matrixStackIn.translate(itemVec.x(), itemVec.y() + (stack.getItem() instanceof BlockItem ? 0.167 : 5.0f / 16.0f) + move.y(), itemVec.z());

			matrixStackIn.scale(0.35f, 0.35f, 0.35f);

			matrixStackIn.translate(0, 5.0f / (16.0f * 0.35f), 0);

			if (!(stack.getItem() instanceof BlockItem)) {

				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));

			}

			break;

		case SlopedDown:

			matrixStackIn.translate(itemVec.x(), itemVec.y() + (stack.getItem() instanceof BlockItem ? 0.167 : 2.0f / 16.0f), itemVec.z());

			matrixStackIn.scale(0.35f, 0.35f, 0.35f);

			if (!(stack.getItem() instanceof BlockItem)) {

				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));

			}

			int rotate = -45;

			if (direct == Direction.NORTH) {

				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(rotate));

			} else if (direct == Direction.EAST) {

				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-rotate));

			} else if (direct == Direction.WEST) {

				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90));
				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(rotate));

			} else if (direct == Direction.SOUTH) {

				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-rotate));

			}

			matrixStackIn.translate(0, 2.0f / (16.0f * 0.35f), 0);

			location = tile.running.get() ? ClientRegister.MODEL_SLOPEDCONVEYORDOWNANIMATED : ClientRegister.MODEL_SLOPEDCONVEYORDOWN;

			break;

		case SlopedUp:

			matrixStackIn.translate(itemVec.x(), itemVec.y() + (stack.getItem() instanceof BlockItem ? 0.167 : 7.0f / 16.0f), itemVec.z());

			matrixStackIn.scale(0.35f, 0.35f, 0.35f);

			if (!(stack.getItem() instanceof BlockItem)) {

				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));

			}

			rotate = 45;

			if (direct == Direction.NORTH) {

				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(rotate));

			} else if (direct == Direction.EAST) {

				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-rotate));

			} else if (direct == Direction.WEST) {

				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90));
				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(rotate));

			} else if (direct == Direction.SOUTH) {

				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-rotate));

			}

			matrixStackIn.translate(0, 5.0f / (16.0f * 0.35f), 0);

			location = tile.running.get() ? ClientRegister.MODEL_SLOPEDCONVEYORUPANIMATED : ClientRegister.MODEL_SLOPEDCONVEYORUP;

			break;

		case Vertical:

			TileEntity below = tile.getLevel().getBlockEntity(tile.getBlockPos().below());
			
			if (below instanceof TileConveyorBelt && ConveyorType.values()[((TileConveyorBelt) below).conveyorType.get()] == ConveyorType.Vertical) {

				location = tile.running.get() ? ClientRegister.MODEL_ELEVATORRUNNING : ClientRegister.MODEL_ELEVATOR;

			} else {

				location = tile.running.get() ? ClientRegister.MODEL_ELEVATORBOTTOMRUNNING : ClientRegister.MODEL_ELEVATORBOTTOM;

			}

			matrixStackIn.translate(0.5, itemVec.y() + (stack.getItem() instanceof BlockItem ? 0.167 : 5.0f / 16.0f) + 5.0f / 16.0f, 0.5);

			matrixStackIn.scale(0.35f, 0.35f, 0.35f);

			if (!(stack.getItem() instanceof BlockItem)) {

				matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));

			}

			break;

		default:

			break;

		}

		Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.NONE, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);

		matrixStackIn.popPose();

		IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);

		matrixStackIn.pushPose();

		matrixStackIn.translate(0, 1 / 16.0, 0);

		RenderingUtils.prepareRotationalTileModel(tile, matrixStackIn);

		if (ConveyorType.values()[tile.conveyorType.get()] == ConveyorType.SlopedDown) {

			matrixStackIn.translate(0, -1, 0);

			matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));

		}

		RenderingUtils.renderModel(model, tile, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

		matrixStackIn.popPose();

		if (tile.isPusher.get() || tile.isPuller.get()) {

			model = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_MANIPULATOR);

			move = tile.getDirectionAsVector();

			if (tile.isPusher.get()) {

				BlockPos nextBlockPos = tile.getNextPos().subtract(tile.getBlockPos());

				matrixStackIn.pushPose();

				matrixStackIn.translate(0, 1 / 16.0, 0);

				if (ConveyorType.values()[tile.conveyorType.get()] == ConveyorType.SlopedDown) {

					matrixStackIn.translate(0, 0.4, 0);

				}

				matrixStackIn.translate(nextBlockPos.getX() - move.x(), nextBlockPos.getY() - move.y(), nextBlockPos.getZ() - move.z());

				RenderingUtils.prepareRotationalTileModel(tile, matrixStackIn);

				RenderingUtils.renderModel(model, tile, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

				matrixStackIn.popPose();

			}

			if (tile.isPuller.get()) {

				matrixStackIn.pushPose();

				matrixStackIn.translate(0, 1 / 16.0, 0);

				RenderingUtils.prepareRotationalTileModel(tile, matrixStackIn);

				if (ConveyorType.values()[tile.conveyorType.get()] == ConveyorType.SlopedUp) {

					matrixStackIn.translate(0, 0.4, 0);

				}

				matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));

				RenderingUtils.renderModel(model, tile, RenderType.solid(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

				matrixStackIn.popPose();

			}

		}

		matrixStackIn.popPose();

	}
}