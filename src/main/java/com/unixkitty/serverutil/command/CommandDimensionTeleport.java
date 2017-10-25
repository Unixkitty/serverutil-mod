package com.unixkitty.serverutil.command;

import com.google.common.collect.Lists;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.CustomDimensionTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandDimensionTeleport extends CommandBase
{
    final String stringUsage = ServerUtilMod.MODID + ".commands.tpx.usage";

    private final List<String> aliases;

    public CommandDimensionTeleport()
    {
        aliases = Lists.newArrayList(ServerUtilMod.MODID, "TPX", "tpx");
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "tpx";
    }

    @Nonnull
    @Override
    public List<String> getAliases()
    {
        return aliases;
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return ServerUtilMod.proxy.translateString(stringUsage);
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length < 2 || args.length > 5)
        {
            throw new WrongUsageException(getUsage(sender));
        }
        else
        {

            Entity teleportingEntity = getEntity(server, sender, args[0]);

            int targetDimension = teleportingEntity.dimension;
            double targetX;
            double targetY;
            double targetZ;

            String s = args[1];
            Entity targetEntity = null;
            try
            {
                targetDimension = Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {
                targetEntity = getEntity(server, sender, s);
            }

            if (args.length == 2)
            {

                teleportingEntity.dismountRidingEntity();

                //Teleporting entity to entity
                if (targetEntity != null)
                {
                    targetX = targetEntity.posX;
                    targetY = targetEntity.posY;
                    targetZ = targetEntity.posZ;

                    //Different dimensions
                    if (teleportingEntity.world != targetEntity.world)
                    {
                        CustomDimensionTeleporter.teleport(teleportingEntity, targetEntity);
                    }
                    else
                    {
                        if (teleportingEntity instanceof EntityPlayerMP)
                        {
                            ((EntityPlayerMP) teleportingEntity).connection.setPlayerLocation(targetX, targetY, targetZ, targetEntity.rotationYaw, targetEntity.rotationPitch);
                        }
                        else
                        {
                            teleportingEntity.setLocationAndAngles(targetX, targetY, targetZ, targetEntity.rotationYaw, targetEntity.rotationPitch);
                        }
                    }
                }
                //Teleporting entity to dimensionId
                else
                {
                    //Specified dimension different from current
                    if (teleportingEntity.dimension != targetDimension)
                    {
                        CustomDimensionTeleporter.teleport(teleportingEntity, targetDimension);
                    }
                    else
                    {
                        throw new CommandException(ServerUtilMod.proxy.translateString(ServerUtilMod.MODID + ".commands.tpx.sameDimension"));
                    }
                }
            }
            else if (args.length == 5)
            {
                int i = 4096;
                CommandBase.CoordinateArg parsedX = parseCoordinate(teleportingEntity.posX, args[2], true);
                CommandBase.CoordinateArg parsedY = parseCoordinate(teleportingEntity.posY, args[3], -i, i, false);
                CommandBase.CoordinateArg parsedZ = parseCoordinate(teleportingEntity.posZ, args[4], true);

                targetX = parsedX.getResult();
                targetY = parsedY.getResult();
                targetZ = parsedZ.getResult();

                if (teleportingEntity.dimension != targetDimension)
                {
                    CustomDimensionTeleporter.teleport(teleportingEntity, targetDimension, targetX, targetY, targetZ);
                }
                else
                {
                    if (teleportingEntity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP) teleportingEntity).connection.setPlayerLocation(targetX, targetY, targetZ, teleportingEntity.rotationYaw, teleportingEntity.rotationPitch);
                    }
                    else
                    {
                        teleportingEntity.setLocationAndAngles(targetX, targetY, targetZ, teleportingEntity.rotationYaw, teleportingEntity.rotationPitch);
                    }
                }
            }
            else
            {
                throw new WrongUsageException(getUsage(sender));
            }

        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length <= 2)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else
        {
            //return args.length > 2 && args.length <= 5 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0 || index == 1;
    }
}
