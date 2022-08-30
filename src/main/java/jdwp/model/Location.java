package jdwp.model;

import jdwp.Translator;

public class Location {

    private int start, end, srcLine;

    public Location(int start, int end, int srcLine) {
        this.start = start;
        this.end = end;
        this.srcLine = srcLine;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getSrcLine() {
        return srcLine;
    }

    public void setSrcLine(int srcLine) {
        this.srcLine = srcLine;
    }

}
