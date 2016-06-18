package com.sproutigy.commons.binary;

public class BinaryBuilderFactory {
    private long expectedSize = BinaryBuilder.DEFAULT_EXPECTED_SIZE;
    private int maxMemorySizeBytes = BinaryBuilder.DEFAULT_MAX_MEMORY_SIZE_BYTES;
    private long maxSizeBytesLimit = BinaryBuilder.DEFAULT_MAX_SIZE_BYTES_LIMIT;

    public BinaryBuilder create() {
        return new BinaryBuilder(expectedSize, maxMemorySizeBytes, maxSizeBytesLimit);
    }

    public long getExpectedSize() {
        return expectedSize;
    }

    public void setExpectedSize(long expectedSize) {
        this.expectedSize = expectedSize;
    }

    public int getMaxMemorySizeBytes() {
        return maxMemorySizeBytes;
    }

    public void setMaxMemorySizeBytes(int maxMemorySizeBytes) {
        this.maxMemorySizeBytes = maxMemorySizeBytes;
    }

    public long getMaxSizeBytesLimit() {
        return maxSizeBytesLimit;
    }

    public void setMaxSizeBytesLimit(long maxSizeBytesLimit) {
        this.maxSizeBytesLimit = maxSizeBytesLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryBuilderFactory that = (BinaryBuilderFactory) o;

        if (expectedSize != that.expectedSize) return false;
        if (maxMemorySizeBytes != that.maxMemorySizeBytes) return false;
        return maxSizeBytesLimit == that.maxSizeBytesLimit;

    }

    @Override
    public int hashCode() {
        int result = (int) (expectedSize ^ (expectedSize >>> 32));
        result = 31 * result + maxMemorySizeBytes;
        result = 31 * result + (int) (maxSizeBytesLimit ^ (maxSizeBytesLimit >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BinaryBuilderFactory{" +
                "expectedSize=" + expectedSize +
                ", maxMemorySizeBytes=" + maxMemorySizeBytes +
                ", maxSizeBytesLimit=" + maxSizeBytesLimit +
                '}';
    }
}
