/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 * Copyright (C) 2018 JetBrains s.r.o.
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License v2 with Classpath Exception.
 * The text of the license is available in the file LICENSE.TXT.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See LICENSE.TXT for more details.
 *
 * You may contact JetBrains s.r.o. at Na Hřebenech II 1718/10, 140 00 Prague,
 * Czech Republic or at legal@jetbrains.com.
 */

package jdwp.jdi;

// from JVMTI specification - refer to jvmti.xml
public interface JVMTIThreadState {
    int JVMTI_THREAD_STATE_ALIVE = 0x0001;
    int JVMTI_THREAD_STATE_TERMINATED = 0x0002;
    int JVMTI_THREAD_STATE_RUNNABLE = 0x0004;
    int JVMTI_THREAD_STATE_WAITING = 0x0080;
    int JVMTI_THREAD_STATE_WAITING_INDEFINITELY = 0x0010;
    int JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT = 0x0020;
    int JVMTI_THREAD_STATE_SLEEPING = 0x0040;
    int JVMTI_THREAD_STATE_IN_OBJECT_WAIT = 0x0100;
    int JVMTI_THREAD_STATE_PARKED = 0x0200;
    int JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER = 0x0400;
    int JVMTI_THREAD_STATE_SUSPENDED = 0x100000;
    int JVMTI_THREAD_STATE_INTERRUPTED = 0x200000;
    int JVMTI_THREAD_STATE_IN_NATIVE = 0x400000;
}
