/* Copyright (C) 2022 IBM Corporation
*
* This program is free software; you can redistribute and/or modify it under
* the terms of the GNU General Public License v2 with Classpath Exception.
* The text of the license is available in the file LICENSE.TXT.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See LICENSE.TXT for more details.
*/

package jdwp;

import gdb.mi.service.command.events.*;
import gdb.mi.service.command.output.*;
import gdb.mi.service.command.output.MiSymbolInfoFunctionsInfo.SymbolFileInfo;
import gdb.mi.service.command.output.MiSymbolInfoFunctionsInfo.Symbols;
import jdwp.jdi.LocationImpl;
import jdwp.jdi.MethodImpl;
import jdwp.jdi.ConcreteMethodImpl;
import jdwp.model.ReferenceType;

import javax.lang.model.SourceVersion;
import java.lang.reflect.Modifier;
import java.util.*;

public class Translator {

	static final String JAVA_BOOLEAN = "boolean";
	static final String JAVA_BYTE = "byte";
	static final String JAVA_CHAR = "char";
	static final String JAVA_SHORT = "short";
	static final String JAVA_INT = "int";
	static final String JAVA_LONG = "long";
	static final String JAVA_FLOAT = "float";
	static final String JAVA_DOUBLE = "double";
	static final String JAVA_VOID = "void";

	static final Map<String, String> typeSignature;	// primitive type signature mapping from C/C++ to JNI
	static {
		typeSignature = new HashMap<>();

		typeSignature.put(JAVA_BOOLEAN, "Z");
		typeSignature.put(JAVA_BYTE, "B");
		typeSignature.put(JAVA_CHAR, "C");
		typeSignature.put(JAVA_SHORT, "S");
		typeSignature.put(JAVA_INT, "I");
		typeSignature.put(JAVA_LONG, "J");
		typeSignature.put(JAVA_FLOAT, "F");
		typeSignature.put(JAVA_DOUBLE, "D");
		typeSignature.put(JAVA_VOID, "V");
	}

	public static PacketStream getVMStartedPacket(GDBControl gc) {
		PacketStream packetStream = new PacketStream(gc);
		byte suspendPolicy = JDWP.SuspendPolicy.ALL;
		byte eventKind = JDWP.EventKind.VM_START;
		packetStream.writeByte(suspendPolicy);
		packetStream.writeInt(1);
		packetStream.writeByte(eventKind);
		packetStream.writeInt(0); // requestId is 0 since it's automatically generated
		packetStream.writeObjectRef(getMainThreadId(gc)); // Todo ThreadId -- change this!!!!
		return packetStream;
	}


	public static PacketStream translate(GDBControl gc, MIEvent event) {
		if (event instanceof MIBreakpointHitEvent) {
			return translateBreakpointHit(gc, (MIBreakpointHitEvent) event);
		} else if (event instanceof MISteppingRangeEvent) {
			return translateSteppingRange(gc, (MISteppingRangeEvent) event);
		} else if (event instanceof MIInferiorExitEvent) {
			return translateExitEvent(gc, (MIInferiorExitEvent) event);
		} else if (event instanceof ClassPrepareEvent) {
			return translateClassPrepare(gc, (ClassPrepareEvent) event);
		}
		return null;
	}

	private static PacketStream translateClassPrepare(GDBControl gc, ClassPrepareEvent event) {
		PacketStream packetStream = new PacketStream(gc);
		byte eventKind = JDWP.EventKind.CLASS_PREPARE;

		packetStream.writeByte(event.suspendPolicy);
		packetStream.writeInt(1);
		packetStream.writeByte(eventKind);
		packetStream.writeInt(event.requestID);
		packetStream.writeObjectRef(getMainThreadId(gc));
		packetStream.writeByte(event.referenceType.tag());
		packetStream.writeObjectRef(event.referenceType.uniqueID());
		packetStream.writeString(event.referenceType.signature());
		packetStream.writeInt(7);
		return packetStream;
	}

	private static PacketStream translateExitEvent(GDBControl gc, MIInferiorExitEvent event) {
		PacketStream packetStream = new PacketStream(gc);
		byte suspendPolicy = JDWP.SuspendPolicy.NONE;
		byte eventKind = JDWP.EventKind.VM_DEATH;
		packetStream.writeByte(suspendPolicy);
		packetStream.writeInt(1); // Number of events in this response packet
		packetStream.writeByte(eventKind);
		packetStream.writeInt(0);
		return packetStream;
	}

	private static PacketStream translateBreakpointHit(GDBControl gc, MIBreakpointHitEvent event) {
		PacketStream packetStream = new PacketStream(gc);
		Integer eventNumber = Integer.parseInt(event.getNumber());

		if (eventNumber == 1) { // This is the very first breakpoint due the use of start
			gc.initialized();
			return null;
		}

		MIBreakInsertInfo info = JDWP.bkptsByBreakpointNumber.get(eventNumber);
		if (info == null) { // This happens for a synthetic breakpoint (not set by the user)
			return null;
		}
		byte suspendPolicy = info.getMIInfoSuspendPolicy();
		int requestId = info.getMIInfoRequestID();
		byte eventKind = info.getMIInfoEventKind();
		LocationImpl loc = JDWP.bkptsLocation.get(eventNumber);
		long threadID = getThreadId(event);
		System.out.println("THREAD ID FOR HIT: "+ threadID);
		//long threadID = getMainThreadId(gc);

		packetStream.writeByte(suspendPolicy);
		packetStream.writeInt(1); // Number of events in this response packet
		packetStream.writeByte(eventKind);
		packetStream.writeInt(requestId);
		packetStream.writeObjectRef(threadID);
		packetStream.writeLocation(loc);
		return packetStream;
	}

	public static long getMainThreadId(GDBControl gc) {
		return (long) 1;
//		List<ThreadReferenceImpl> list = gc.vm.allThreads();
//		for(ThreadReferenceImpl thread: list){
//			if ("main".equals(thread.name())) {
//				return thread.uniqueID();
//			}
//		}
//		return 0;
	}

	private static long getThreadId(MIStoppedEvent event) {
		long id = 0;
		for (MIResult result: event.getResults()) {
			if ("thread-id".equals(result.getVariable())) {
				MIValue value = result.getMIValue();
				id = Long.parseLong(value.toString());
			}
		}
		return id;
	}

	private static PacketStream  translateSteppingRange(GDBControl gc, MISteppingRangeEvent event) {
		System.out.println("Translating end-stepping-range");
		PacketStream packetStream = new PacketStream(gc);
		Long threadID = getThreadId(event);
		//long threadID = getMainThreadId(gc);
		MIInfo info = JDWP.stepByThreadID.get(threadID);
		if (info == null) {
			System.out.println("Returning null");
			return null;
		}

		packetStream.writeByte(info.getMIInfoSuspendPolicy());
		packetStream.writeInt(1); // Number of events in this response packet
		packetStream.writeByte(info.getMIInfoEventKind());
		packetStream.writeInt(info.getMIInfoRequestID());
		packetStream.writeObjectRef(getMainThreadId(gc));
		LocationImpl loc = locationLookup(event.getFrame().getFunction(), event.getFrame().getLine());
		if (loc != null) {
			packetStream.writeLocation(loc);
			JDWP.stepByThreadID.remove(threadID);
			return packetStream;

		}
		return packetStream;
	}

	private static  boolean isPrimitive(String type) {
		return typeSignature.containsKey(type);
	}

	public static class MethodInfo {
		private String className;
		private String methodName;
		private String returnType;
		private List<String> argumentTypes = new ArrayList<>();

		private int modifier = Modifier.STATIC | Modifier.PUBLIC;
		private final Long uniqueID;
		private static Long counter = 0L;

		public MethodInfo(String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
			this.uniqueID = counter++;
		}

		public void setReturnType(String returnType) {
			this.returnType = returnType;
		}

		public void addArgumentType(String paramType) {
			argumentTypes.add(paramType);
		}

		public String getSignature() {
			StringBuilder builder = new StringBuilder("(");
			for(String argType : argumentTypes) {
				builder.append(gdb2JNIType(argType));
			}
			builder.append(')');
			builder.append(gdb2JNIType(returnType));
			return builder.toString();
		}

		public void addModifier(int modifier) {
			this.modifier |= modifier;
		}

		public void removeModifier(int modifier) {
			this.modifier &= ~modifier;
		}

		public int getModifier() {
			return modifier;
		}

		public Long getUniqueID() {
			return uniqueID;
		}
	}

	public static MethodInfo gdbSymbolToMethodInfo(String name, String type) {
		String[] functionNames = getClassAndFunctionName(name);
		MethodInfo info = new MethodInfo(functionNames[0], functionNames[1]);
		getSignature(type, functionNames[0], info);
		return info;

	}

	public static String normalizeType(String type) {
		if (type.startsWith("class ")) {
			type = type.substring(6);
		} else if (type.startsWith("union ")) {
			type = type.substring(6);
		}
		if (type.charAt(type.length() - 1) == '*') {
			type = type.substring(0, type.length() - 1);
		}
		return type.trim();
	}

	/**
	 * Normalize a type information returned by GDB to the JNI signature. The type field has the following structure:
	 * <code>return_type (parm1_type,...)</code>
	 * where return type can be <code>void, int or union interface_name * or class class_name *</code>
	 *
	 * @param type the type information from GDB
	 * @return the JNI signature
	 */
	public static void getSignature(String type, String className, MethodInfo info) {
		int index = type.indexOf('(');
		if (index == (-1)) {
			info.setReturnType(normalizeType(type));
		} else {
			info.setReturnType(normalizeType(type.substring(0, type.indexOf("("))));
			String paramList = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
			String[] params = paramList.split(", ");
			for (int i=0; i < params.length;++i) {
				if (!params[i].equals("void")) {
					String paramType = normalizeType(params[i]);
					if (i !=0 || !paramType.equals(className)) {
						info.addArgumentType(paramType);
					} else {
						info.removeModifier(Modifier.STATIC);
					}
				}
			}
		}
	}

	/**
	 * Return the function name from the name field. The returned array is a 2 element array whose first value is the
	 * class name and the second value is the function (method) name. If no class is found then the first element is
	 * null.
	 *
	 * @param name the GDB name field (ie <code>java.util.List::of(java.lang.Object[] *)</code>)
	 * @return a 2 element array
	 */
	public static String[] getClassAndFunctionName(String name) {
		String[] names = new String[2];

		int index = name.indexOf("::");
		if (index != (-1)) {
			names[0] = name.substring(0, index);
			name = name.substring(index + 2);

		}
		index = name.indexOf('(');
		if (index != (-1)) {
			names[1] = name.substring(0, index);
		} else {
			names[1] = name;
		}
		return names;
	}

	public static String gdb2JNIType(String param) {
		if (param.endsWith("*")) { // zero or more param
			param = param.substring(0, param.indexOf("*"));
		}
		param = param.replace(" ", "");
		param = param.replace(".", "/");
		String prefix = "";
		while (param.endsWith("[]")) {
			param = param.substring(0, param.length() - 2);
			prefix += "[";
		}
		if (!isPrimitive(param)) {
			param = "L" + param + ";";
		} else {
			// If is primitive, provide JNI signature
			param = getPrimitiveJNI(param);
		}
		return prefix + param;
	}

	public static String getPrimitiveJNI(String param) {
		return typeSignature.get(param);
	}

	public static LocationImpl locationLookup(String func, int line) {
		String name = func; //TODO
		MethodImpl impl = MethodImpl.methods.get(name);
		if (impl != null) {
			List<LocationImpl> list = ((ConcreteMethodImpl) impl).getBaseLocations().lineMapper.get(line);
			if (list != null && list.size() >= 1) {
				return list.get(0);
			}
			return null;
		}
		if (!name.contains("(")) {
			Set<String> keys = MethodImpl.methods.keySet();
			for (String key: keys) {
				if (key.contains(name)) {
					ConcreteMethodImpl impl1 = (ConcreteMethodImpl) MethodImpl.methods.get(key);
					List<LocationImpl> list = ((ConcreteMethodImpl) impl1).getBaseLocations().lineMapper.get(line);
					if (list != null && list.size() >= 1) {
						return list.get(0);
					}
				}
			}
		}
		return null;
	}


	public static void translateReferenceTypes(Map<Long, ReferenceType> referenceTypes, MiSymbolInfoFunctionsInfo response) {
		Map<String, ReferenceType> types = new HashMap<>();
		for(SymbolFileInfo symbolFile : response.getSymbolFiles()) {
			for(Symbols symbol : symbolFile.getSymbols()) {
				var index = symbol.getName().indexOf("::");
				if (index != (-1)) {
					var className = symbol.getName().substring(0, index);
					if (isJavaClassName(className)) {
						var methodInfo = gdbSymbolToMethodInfo(symbol.getName(), symbol.getType());
						var refType = types.computeIfAbsent(className, key -> {
							var type = new ReferenceType(className);
							referenceTypes.put(type.getUniqueID(), type);
							return type;
						});
						refType.addMethod(methodInfo);
					}
				}
			}
		}
	}

	private static boolean isJavaClassName(String className) {
		String[] members = className.split("\\.");
		for(String member : members) {
			if (!SourceVersion.isIdentifier(member)) {
				return false;
			}
		}
		return true;
	}
}


