package com.yech.heavenRTP;

public class nnrandomxoroshiro128plus {

    private long state0;
    private long state1;

    public nnrandomxoroshiro128plus(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        state0 = splitMix64(seed);
        state1 = splitMix64(state0);
    }

    private long splitMix64(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        return (int) (nextLong() % bound);
    }

    public long nextLong() {
        long result = state0 + state1;

        long s1 = state0;
        state1 ^= s1;
        state0 = Long.rotateLeft(s1, 55) ^ state1 ^ (state1 << 14);
        state1 = Long.rotateLeft(state1, 36);

        return result;
    }
}