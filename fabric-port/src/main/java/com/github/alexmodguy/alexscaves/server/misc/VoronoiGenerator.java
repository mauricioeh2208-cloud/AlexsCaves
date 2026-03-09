package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraft.world.phys.Vec3;

public class VoronoiGenerator {
    private long seed;
    private double offsetAmount = 1.0D;

    public VoronoiGenerator(long seed) {
        this.seed = seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setOffsetAmount(double offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    public VoronoiInfo get2(double x, double z) {
        int cellX = (int) Math.floor(x);
        int cellZ = (int) Math.floor(z);
        double nearestDistance = Double.MAX_VALUE;
        double secondDistance = Double.MAX_VALUE;
        double closestHash = 0.0D;
        Vec3 closestCellPos = Vec3.ZERO;
        Vec3 closestLocalPos = Vec3.ZERO;

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                int testX = cellX + xOffset;
                int testZ = cellZ + zOffset;
                long hash = mix(seed, testX, testZ);
                double jitterX = unpackSigned(hash) * 0.43701595D * offsetAmount;
                double jitterZ = unpackSigned(hash >>> 24) * 0.43701595D * offsetAmount;
                double pointX = testX + 0.5D + jitterX;
                double pointZ = testZ + 0.5D + jitterZ;
                double dx = pointX - x;
                double dz = pointZ - z;
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance < nearestDistance) {
                    secondDistance = nearestDistance;
                    nearestDistance = distance;
                    closestHash = normalizeHash(hash);
                    closestCellPos = new Vec3(pointX, 0.0D, pointZ);
                    closestLocalPos = new Vec3(dx, 0.0D, dz);
                } else if (distance < secondDistance) {
                    secondDistance = distance;
                }
            }
        }
        return new VoronoiInfo(nearestDistance, secondDistance, closestHash, closestCellPos, closestLocalPos);
    }

    private static long mix(long seed, int x, int z) {
        long hash = seed;
        hash ^= x * 341873128712L;
        hash ^= z * 132897987541L;
        hash ^= hash >>> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >>> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >>> 33;
        return hash;
    }

    private static double unpackSigned(long value) {
        return ((value & 0xFFFFFFL) / (double) 0x7FFFFF) - 1.0D;
    }

    private static double normalizeHash(long hash) {
        return ((hash & 0x7FFFFFFFL) / (double) Integer.MAX_VALUE) * 2.0D - 1.0D;
    }

    public record VoronoiInfo(double distance, double distance1, double hash, Vec3 cellPos, Vec3 localPos) {
    }
}
