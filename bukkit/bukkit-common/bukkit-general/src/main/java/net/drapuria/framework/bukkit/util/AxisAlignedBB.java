package net.drapuria.framework.bukkit.util;

import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class AxisAlignedBB
{
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;
    private static final String __OBFID = "CL_00000607";

    /**
     * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public static AxisAlignedBB getBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public static AxisAlignedBB getBoundingBox(final Location min, final Location max)
    {
        return new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Sets the bounds of the bounding box. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public AxisAlignedBB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return this;
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    public AxisAlignedBB addCoord(double x, double y, double z)
    {
        double var7 = this.minX;
        double var9 = this.minY;
        double var11 = this.minZ;
        double var13 = this.maxX;
        double var15 = this.maxY;
        double var17 = this.maxZ;

        if (x < 0.0D)
        {
            var7 += x;
        }

        if (x > 0.0D)
        {
            var13 += x;
        }

        if (y < 0.0D)
        {
            var9 += y;
        }

        if (y > 0.0D)
        {
            var15 += y;
        }

        if (z < 0.0D)
        {
            var11 += z;
        }

        if (z > 0.0D)
        {
            var17 += z;
        }

        return getBoundingBox(var7, var9, var11, var13, var15, var17);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB expand(double x, double y, double z)
    {
        double var7 = this.minX - x;
        double var9 = this.minY - y;
        double var11 = this.minZ - z;
        double var13 = this.maxX + x;
        double var15 = this.maxY + y;
        double var17 = this.maxZ + z;
        return getBoundingBox(var7, var9, var11, var13, var15, var17);
    }


    public AxisAlignedBB func_111270_a(AxisAlignedBB p_111270_1_)
    {
        double var2 = Math.min(this.minX, p_111270_1_.minX);
        double var4 = Math.min(this.minY, p_111270_1_.minY);
        double var6 = Math.min(this.minZ, p_111270_1_.minZ);
        double var8 = Math.max(this.maxX, p_111270_1_.maxX);
        double var10 = Math.max(this.maxY, p_111270_1_.maxY);
        double var12 = Math.max(this.maxZ, p_111270_1_.maxZ);
        return getBoundingBox(var2, var4, var6, var8, var10, var12);
    }

    /**
     * Returns a bounding box offseted by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB getOffsetBoundingBox(double x, double y, double z)
    {
        return getBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(AxisAlignedBB boundingBox, double offset)
    {
        if (boundingBox.maxY > this.minY && boundingBox.minY < this.maxY)
        {
            if (boundingBox.maxZ > this.minZ && boundingBox.minZ < this.maxZ)
            {
                double var4;

                if (offset > 0.0D && boundingBox.maxX <= this.minX)
                {
                    var4 = this.minX - boundingBox.maxX;

                    if (var4 < offset)
                    {
                        offset = var4;
                    }
                }

                if (offset < 0.0D && boundingBox.minX >= this.maxX)
                {
                    var4 = this.maxX - boundingBox.minX;

                    if (var4 > offset)
                    {
                        offset = var4;
                    }
                }

                return offset;
            }
            else
            {
                return offset;
            }
        }
        else
        {
            return offset;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(AxisAlignedBB boundingBox, double yOffset)
    {
        if (boundingBox.maxX > this.minX && boundingBox.minX < this.maxX)
        {
            if (boundingBox.maxZ > this.minZ && boundingBox.minZ < this.maxZ)
            {
                double var4;

                if (yOffset > 0.0D && boundingBox.maxY <= this.minY)
                {
                    var4 = this.minY - boundingBox.maxY;

                    if (var4 < yOffset)
                    {
                        yOffset = var4;
                    }
                }

                if (yOffset < 0.0D && boundingBox.minY >= this.maxY)
                {
                    var4 = this.maxY - boundingBox.minY;

                    if (var4 > yOffset)
                    {
                        yOffset = var4;
                    }
                }

                return yOffset;
            }
            else
            {
                return yOffset;
            }
        }
        else
        {
            return yOffset;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(AxisAlignedBB boundingBox, double zOffset)
    {
        if (boundingBox.maxX > this.minX && boundingBox.minX < this.maxX)
        {
            if (boundingBox.maxY > this.minY && boundingBox.minY < this.maxY)
            {
                double var4;

                if (zOffset > 0.0D && boundingBox.maxZ <= this.minZ)
                {
                    var4 = this.minZ - boundingBox.maxZ;

                    if (var4 < zOffset)
                    {
                        zOffset = var4;
                    }
                }

                if (zOffset < 0.0D && boundingBox.minZ >= this.maxZ)
                {
                    var4 = this.maxZ - boundingBox.minZ;

                    if (var4 > zOffset)
                    {
                        zOffset = var4;
                    }
                }

                return zOffset;
            }
            else
            {
                return zOffset;
            }
        }
        else
        {
            return zOffset;
        }
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(AxisAlignedBB boundingBox)
    {
        return boundingBox.maxX > this.minX && boundingBox.minX < this.maxX ? (boundingBox.maxY > this.minY && boundingBox.minY < this.maxY ? boundingBox.maxZ > this.minZ && boundingBox.minZ < this.maxZ : false) : false;
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    public AxisAlignedBB offset(double x, double y, double z)
    {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        return this;
    }

    /**
     * Returns if the supplied location is completely inside the bounding box
     */
    public boolean isLocationInside(Location location)
    {
        return location.getX() > this.minX
                && location.getY() < this.maxX
                && (location.getY() > this.minY && location.getY() < this.maxY
                && location.getZ() > this.minZ && location.getZ() < this.maxZ);
    }

    public boolean isPositionInside(Location testPos) {
        return (minX <= testPos.getX() && maxX >= testPos.getX())
                && (minY <= testPos.getY() && maxY >= testPos.getY()) && (minZ <= testPos.getZ()
                && maxZ >= testPos.getZ());
    }
    
    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getAverageEdgeLength()
    {
        double var1 = this.maxX - this.minX;
        double var3 = this.maxY - this.minY;
        double var5 = this.maxZ - this.minZ;
        return (var1 + var3 + var5) / 3.0D;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    public AxisAlignedBB contract(double x, double y, double z)
    {
        double var7 = this.minX + x;
        double var9 = this.minY + y;
        double var11 = this.minZ + z;
        double var13 = this.maxX - x;
        double var15 = this.maxY - y;
        double var17 = this.maxZ - z;
        return getBoundingBox(var7, var9, var11, var13, var15, var17);
    }

    /**
     * Returns a copy of the bounding box.
     */
    public AxisAlignedBB copy()
    {
        return getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    /**
     * Checks if the specified location is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    public boolean isLocationInYZ(Location location)
    {
        return location == null ? false : location.getY() >= this.minY && location.getY() <= this.maxY && location.getZ() >= this.minZ && location.getZ() <= this.maxZ;
    }

    /**
     * Checks if the specified location is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    public boolean isLocationInXZ(Location location)
    {
        return location == null ? false : location.getX() >= this.minX && location.getX() <= this.maxX && location.getZ() >= this.minZ && location.getZ() <= this.maxZ;
    }

    /**
     * Checks if the specified location is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    public boolean isLocationInBlockXZ(Location location)
    {
        return location == null ? false : location.getBlockX() >= this.minX && location.getBlockX() <= this.maxX && location.getBlockZ() >= this.minZ && location.getBlockZ() <= this.maxZ;
    }

    /**
     * Checks if the specified location is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    public boolean isLocationInBlock(Location location)
    {
        return location != null && location.getBlockX() >= this.minX && location.getBlockX() <= this.maxX && location.getBlockZ() >= this.minZ && location.getBlockZ() <= this.maxZ && location.getBlockY() >= this.minY && location.getBlockY() <= this.maxY;
    }

    /**
     * Sets the bounding box to the same bounds as the bounding box passed in. Args: axisAlignedBB
     */
    public void setBB(AxisAlignedBB boundingBox)
    {
        this.minX = boundingBox.minX;
        this.minY = boundingBox.minY;
        this.minZ = boundingBox.minZ;
        this.maxX = boundingBox.maxX;
        this.maxY = boundingBox.maxY;
        this.maxZ = boundingBox.maxZ;
    }

    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AxisAlignedBB that = (AxisAlignedBB) o;
        return Double.compare(that.minX, minX) == 0 && Double.compare(that.minY, minY) == 0 && Double.compare(that.minZ, minZ) == 0 && Double.compare(that.maxX, maxX) == 0 && Double.compare(that.maxY, maxY) == 0 && Double.compare(that.maxZ, maxZ) == 0;
    }
}