package me.libraryaddict.halloween;

public class CordPair {
    private int x;
    private int z;

    public CordPair(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CordPair other = (CordPair) obj;
        if (x != other.x) {
            return false;
        }
        if (z != other.z) {
            return false;
        }
        return true;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }
}
