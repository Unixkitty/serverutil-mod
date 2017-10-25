package com.unixkitty.serverutil.command.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class CustomDimensionTeleporter extends Teleporter
{
    private final WorldServer worldServer;
    private double x;
    private double y;
    private double z;

    public CustomDimensionTeleporter(WorldServer world, double x, double y, double z)
    {
        super(world);
        this.worldServer = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeInPortal(@Nonnull Entity entity, float rotationYaw)
    {
        // Avoid creating a nether portal as per vanilla mechanics
        this.worldServer.getBlockState(new BlockPos((int) this.x, (int) this.y, (int) this.z));

        entity.setPosition(this.x, this.y, this.z);
        entity.motionX = 0.0f;
        entity.motionY = 0.0f;
        entity.motionZ = 0.0f;
    }

    public static void teleport(Entity entity, int dimensionId)
    {
        teleport(entity, dimensionId, entity.posX, entity.posY, entity.posZ);
    }

    public static void teleport(Entity entity, Entity entityTarget)
    {
        teleport(entity, entityTarget.dimension, entityTarget.posX, entityTarget.posY, entityTarget.posZ);
    }

    public static void teleport(Entity entity, int dimensionId, double x, double y, double z)
    {
        int previousDimension = entity.getEntityWorld().provider.getDimension();
        MinecraftServer server = entity.getEntityWorld().getMinecraftServer();
        WorldServer worldServer = server.getWorld(dimensionId);

        if (worldServer == null || worldServer.getMinecraftServer() == null)
        {
            //Dimension does not exist
            throw new IllegalArgumentException("Dimension: " + dimensionId + "does not exist!");
        }

        PlayerList playerList = worldServer.getMinecraftServer().getPlayerList();

        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;

            player.addExperienceLevel(0);

            playerList.transferPlayerToDimension(player, dimensionId, new CustomDimensionTeleporter(worldServer, x, y, z));
            player.setPositionAndUpdate(x, y, z);
            // Teleporting out of the end does strange things apparently
            if (previousDimension == 1)
            {
                player.setPositionAndUpdate(x, y, z);
                worldServer.spawnEntity(player);
                worldServer.updateEntityWithOptionalForce(player, false);
            }
        }
        else
        {
            playerList.transferEntityToWorld(entity, previousDimension, server.getWorld(previousDimension), server.getWorld(dimensionId), new CustomDimensionTeleporter(worldServer, x, y, z));
        }

    }
}
