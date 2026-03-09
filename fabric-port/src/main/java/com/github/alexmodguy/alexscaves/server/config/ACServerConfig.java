package com.github.alexmodguy.alexscaves.server.config;

public final class ACServerConfig {
    public static final ACServerConfig INSTANCE = new ACServerConfig();
    public final DoubleValue caveBiomeMeanWidth = new DoubleValue(300.0D);
    public final IntValue caveBiomeMeanSeparation = new IntValue(900);
    public final DoubleValue caveBiomeWidthRandomness = new DoubleValue(0.15D);
    public final DoubleValue caveBiomeSpacingRandomness = new DoubleValue(0.45D);
    public final BooleanValue sugarRushSlowsTime = new BooleanValue(true);

    private ACServerConfig() {
    }

    public static final class BooleanValue {
        private final boolean value;

        private BooleanValue(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }
    }

    public static final class DoubleValue {
        private final double value;

        private DoubleValue(double value) {
            this.value = value;
        }

        public double get() {
            return value;
        }
    }

    public static final class IntValue {
        private final int value;

        private IntValue(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }
    }
}
