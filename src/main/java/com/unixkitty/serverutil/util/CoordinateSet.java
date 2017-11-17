package com.unixkitty.serverutil.util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;

public final class CoordinateSet
{
    private double x;
    private double y;
    private double z;

    private CoordinateSet(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public static CoordinateSet parseCoordinates(double base1, double base2, double base3, String arg1, String arg2, String arg3) throws NumberInvalidException
    {
        int i = 4096;
        return new CoordinateSet
                (
                        CommandBase.parseCoordinate(base1, arg1, true).getResult(),
                        CommandBase.parseCoordinate(base2, arg2, -i, i, false).getResult(),
                        CommandBase.parseCoordinate(base3, arg3, true).getResult()
                );
    }
}