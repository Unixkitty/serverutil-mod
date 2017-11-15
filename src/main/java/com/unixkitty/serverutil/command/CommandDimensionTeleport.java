package com.unixkitty.serverutil.command;

import com.google.common.collect.Lists;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.CustomDimensionTeleporter;
import com.unixkitty.serverutil.util.CoordinateSet;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandDimensionTeleport extends CommandBase
{
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
        return TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.tpx.usage");
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
                    notifyTeleportSuccess(1, sender, teleportingEntity, targetEntity);
                }
                //Teleporting entity to dimensionId
                else
                {
                    //Specified dimension different from current
                    if (teleportingEntity.dimension != targetDimension)
                    {
                        try
                        {
                            CustomDimensionTeleporter.teleport(teleportingEntity, targetDimension);
                            notifyTeleportSuccess(0, sender, teleportingEntity, targetDimension);
                        }
                        catch (NullPointerException e)
                        {
                            noSuchDimension(sender, targetDimension);
                        }
                    }
                    else
                    {
                        throw new CommandException(TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.tpx.sameDimension"));
                    }
                }
            }
            else if (args.length == 5)
            {
                CoordinateSet coords = CoordinateSet.parseCoordinates(teleportingEntity.posX, teleportingEntity.posY, teleportingEntity.chunkCoordZ, args[2], args[3], args[4]);

                targetX = coords.getX();
                targetY = coords.getY();
                targetZ = coords.getZ();

                if (teleportingEntity.dimension != targetDimension)
                {
                    try
                    {
                        CustomDimensionTeleporter.teleport(teleportingEntity, targetDimension, targetX, targetY, targetZ);
                        notifyTeleportSuccess(3, teleportingEntity, targetDimension, targetX, targetY, targetZ);
                    }
                    catch (NullPointerException e)
                    {
                        noSuchDimension(sender, targetDimension);
                    }
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
                    notifyTeleportSuccess(2, teleportingEntity, targetX, targetY, targetZ);
                }
            }
            else
            {
                throw new WrongUsageException(getUsage(sender));
            }
        }
    }

    private void notifyTeleportSuccess(int kind, ICommandSender sender, Object... args)
    {
        Entity teleportingEntity = (Entity) args[0];
        switch (kind)
        {
            //Entity to dimension
            case 0:
                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.tpx.success", teleportingEntity.getDisplayName().getFormattedText(), args[1], getDimensionName(teleportingEntity, (int) args[1]));
                break;
            //Entity to entity
            case 1:
                sender.sendMessage(new TextComponentTranslation("commands.tp.success", teleportingEntity.getDisplayName().getFormattedText(), ((Entity) args[1]).getDisplayName().getFormattedText()));
                break;
            //Entity to coordinates
            case 2:
                sender.sendMessage(new TextComponentTranslation("commands.tp.success.coordinates", teleportingEntity.getDisplayName().getFormattedText(), args[1], args[2], args[3]));
                break;
            //Entity to dimension coordinates
            case 3:
                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.tpx.success.coordinates", teleportingEntity.getDisplayName().getFormattedText(), args[1], getDimensionName(teleportingEntity, (int) args[1]), args[2], args[3], args[4]);
                break;
        }
    }

    private String getDimensionName(Entity entity, int dim)
    {
        try
        {
            return entity.getEntityWorld().getMinecraftServer().getWorld(dim).provider.getDimensionType().getName();
        }
        catch (NullPointerException e)
        {
            return "";
        }
    }

    private void noSuchDimension(ICommandSender sender, int dim) throws CommandException
    {
        throw new CommandException(TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.tpx.noSuchDimension", dim));
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
