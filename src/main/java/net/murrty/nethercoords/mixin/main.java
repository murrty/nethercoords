package net.murrty.nethercoords.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.DimensionType;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class main {

	TextRenderer r = MinecraftClient.getInstance().textRenderer;
	ClientPlayerEntity currentPlayer;
	DimensionType currentDimension;

	int windowHeight = 0;
	int CurrentX = 0;
	int CurrentY = 0;
	int CurrentZ = 0;

	String Direction = "?";
	String newGeneralCoordinates = "x: 0, y: 0, z: 0";
	String newOverworld = "o-x: 0, o-y: 0, o-z: 0";
	String newNether = "n-x: 0, n-y: 0, n-z: 0";

	Direction CurrentDirection;

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void onRender (MatrixStack matrices, float tickDelta, CallbackInfo info) {
		currentPlayer = MinecraftClient.getInstance().player;
		currentDimension = currentPlayer.getEntityWorld().getDimension();
		windowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
		CurrentX = currentPlayer.getBlockX();
		CurrentY = currentPlayer.getBlockY();
		CurrentZ = currentPlayer.getBlockZ();
		CurrentDirection = currentPlayer.getHorizontalFacing();

		switch (CurrentDirection.asString()) {
			case "north":
				Direction = "[-Z] North";
				break;

			case "south":
				Direction = "[+Z] South";
				break;

			case "east":
				Direction = "[+X] East";
				break;

			case "west":
				Direction = "[-X] West";
				break;
		}

		if (currentDimension.isBedWorking()) {
			// overworld
			newOverworld = "o-x: " + CurrentX + ", o-y: " + CurrentY + ", o-z: " + CurrentZ + " *";
			newNether = "n-x: " + String.format("%.3f", ConvertCoordinate(CurrentX, 1)) + ", n-y: " + CurrentY + ", n-z: " + String.format("%.3f", ConvertCoordinate(CurrentZ, 1));

			r.drawWithShadow(
				matrices,
				Direction,
				5,
				windowHeight - 35,
				-1
			);

			r.drawWithShadow(
				matrices,
				newOverworld,
				5,
				windowHeight - 25,
				-1
			);

			r.drawWithShadow(
				matrices,
				newNether,
				5,
				windowHeight - 15,
				-1
			);
		}
		else if (currentDimension.isRespawnAnchorWorking()) {
			// nether
			newOverworld = "o-x: " + (CurrentX * 8) + ", o-y: " + CurrentY + ", o-z: " + (CurrentZ * 8);
			newNether = "n-x: " + CurrentX + ", n-y: " + CurrentY + ", n-z: " + CurrentZ + " *";

			r.drawWithShadow(
				matrices,
				Direction,
				5,
				windowHeight - 35,
				-1
			);

			r.drawWithShadow(
				matrices,
				newOverworld,
				5,
				windowHeight - 25,
				-1
			);

			r.drawWithShadow(
				matrices,
				newNether,
				5,
				windowHeight - 15,
				-1
			);
		}
		else {
			// end
			newGeneralCoordinates = "x: " + CurrentX + ", y: " + CurrentY + ", z: " + CurrentZ;
			r.drawWithShadow(
				matrices,
				Direction,
				5,
				windowHeight - 25,
				-1
			);

			r.drawWithShadow(
				matrices,
				newGeneralCoordinates,
				5,
				windowHeight - 15,
				-1
			);
		}
	}

	public double ConvertCoordinate(double coord, int CurrentDimension) {
		switch (CurrentDimension) {
			case 0: // IN THE NETHER
				return (coord * 8);

			case 1: // IN THE OVERWORLD
				BigDecimal n = new BigDecimal(coord / 8);
				n.setScale(2, RoundingMode.HALF_UP);
				return n.doubleValue();

			default: return 0;
		}
	}
}
