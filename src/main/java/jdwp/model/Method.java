package jdwp.model;

import jdwp.Translator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Method {
    public static class MethodInfo {

        private int startLine;
        private int endLine;
        private String className;
        private String methodName;

        private String gdbName;
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

        public String getMethodName() {
            return methodName;
        }

        public String getClassName() {
            return className;
        }
        public String getGdbName() {
            return gdbName;
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
                builder.append(Translator.gdb2JNIType(argType));
            }
            builder.append(')');
            builder.append(Translator.gdb2JNIType(returnType));
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

        public void setGDBName(String name) {
            this.gdbName = name;
        }

        /**
         * @return the filename to be used for -break-insert
         */
        public String getGDBLocationName() {
            return className + ".java";
        }

        public void setStartLine(int startLine) {
            this.startLine = startLine;
        }

        public void setEndLine(int endLine) {
            this.endLine = endLine;
        }

        public int getStartLine() {
            return startLine;
        }

        public int getEndLine() {
            return endLine;
        }
    }
}
