package jdwp.model;

public class Location {

    private ReferenceType refType;
    private Method.MethodInfo method;
    private int start, end, srcLine;

    public Location(ReferenceType refType, Method.MethodInfo method, int start, int end, int srcLine) {
        this.refType = refType;
        this.method = method;
        this.start = start;
        this.end = end;
        this.srcLine = srcLine;
    }

    public ReferenceType getRefType() {
        return refType;
    }

    public void setRefType(ReferenceType refType) {
        this.refType = refType;
    }

    public Method.MethodInfo getMethod() {
        return method;
    }

    public void setMethod(Method.MethodInfo method) {
        this.method = method;
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
