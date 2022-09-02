package jdwp.model;

import gdb.mi.service.command.output.MISymbolInfoFunctionsInfo;
import jdwp.Translator;

public class Method {

    private MISymbolInfoFunctionsInfo.SymbolFileInfo symbolFileInfo;

    private MISymbolInfoFunctionsInfo.Symbols symbol;

    private ReferenceType referenceType;
    private int startLine;
    private int endLine;
    private String methodName;
    private final long uniqueID;
    private final String signature;
    private final String genericSignature;

    private static Long counter = 0L;

    public Method(MISymbolInfoFunctionsInfo.SymbolFileInfo symbolFileInfo, MISymbolInfoFunctionsInfo.Symbols symbol) {
//        this.referenceType = referenceType;
        this.symbolFileInfo = symbolFileInfo;
        this.symbol = symbol;
        this.startLine = symbol.getLine();
        this.methodName = symbol.getName().substring(0, symbol.getName().indexOf("::"));
        this.uniqueID = counter++;
        this.signature = Translator.gdb2JNIType(symbol.getType());
        this.genericSignature = null;       // for now
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public MISymbolInfoFunctionsInfo.Symbols getSymbol() {
        return symbol;
    }

    public void setSymbol(MISymbolInfoFunctionsInfo.Symbols symbol) {
        this.symbol = symbol;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getUniqueID() {
        return uniqueID;
    }

    public String getSignature() {
        return signature;
    }

    public String getGenericSignature() {
        return genericSignature;
    }

}
