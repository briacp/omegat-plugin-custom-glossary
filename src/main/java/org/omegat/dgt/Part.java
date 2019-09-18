package org.omegat.dgt;

class Part {
    protected int start, len;

    public Part(int start, int len) {
        this.start = start;
        this.len = len;
    }

    public void setEnd(int pos) {
        len = pos - start;
    }

    public boolean contains(int pos) {
        return (pos >= start) && (pos <= start + len);
    }
}