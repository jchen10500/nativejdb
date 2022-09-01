package jdwp.model;

import gdb.mi.service.command.output.MISymbolInfoFunctionsInfo;
import jdwp.JDWP;
import jdwp.PacketStream;
import jdwp.Translator;

import java.util.*;

public class ReferenceType {

    private MISymbolInfoFunctionsInfo.SymbolFileInfo symbolFileInfo;

    private MISymbolInfoFunctionsInfo.Symbols symbol;

    private String className;

    private String type;
    private String signature;
    private String genericSignature;

    private Map<Long, Translator.MethodInfo> methods = new HashMap<>();
    private Map<Long, Method> methods2 = new HashMap<>();

    private final Long uniqueID;


    private static Long counter = 0L;

    public ReferenceType(String className) {
        this.className = className;
        this.genericSignature = null;
        this.uniqueID = counter++;
    }

    public ReferenceType(MISymbolInfoFunctionsInfo.SymbolFileInfo symbolFileInfo, MISymbolInfoFunctionsInfo.Symbols symbol) {
        this.symbolFileInfo = symbolFileInfo;
        this.symbol = symbol;
        this.className = symbol.getName().substring(0, symbol.getName().indexOf("::"));
        this.type = symbol.getType();
        this.signature = Translator.gdb2JNIType(this.type);
        this.genericSignature = null;
        this.uniqueID = counter++;
    }

    public String getClassName() {
        return className;
    }

    public void addMethod(Translator.MethodInfo methodInfo) {
        methods.put(methodInfo.getUniqueID(), methodInfo);
    }

    public void addMethod2(Method method) {
        methods2.put(method.getUniqueID(), method);
    }

    public Collection<Translator.MethodInfo> getMethods() {
        return methods.values();
    }

    public Collection<Method> getMethods2() {
        return methods2.values();
    }

    public Long getUniqueID() {
        return uniqueID;
    }

    public void write(PacketStream answer, boolean generic) {
        answer.writeByte(JDWP.TypeTag.CLASS); //TODO
        answer.writeObjectRef(uniqueID);
        answer.writeString(getSignature());
        if (generic) {
            answer.writeString("");
        }
        answer.writeInt(JDWP.ClassStatus.INITIALIZED | JDWP.ClassStatus.PREPARED | JDWP.ClassStatus.VERIFIED);

    }

    public String getSignature() {
        return signature;
    }

    public String getGenericSignature() {
        return genericSignature;
    }

    public MISymbolInfoFunctionsInfo.SymbolFileInfo getSymbolFileInfo() {
        return symbolFileInfo;
    }

    public MISymbolInfoFunctionsInfo.Symbols getSymbol() {
        return symbol;
    }
}
