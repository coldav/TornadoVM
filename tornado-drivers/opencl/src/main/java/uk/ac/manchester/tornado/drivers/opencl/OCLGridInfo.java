/*
 * This file is part of Tornado: A heterogeneous programming framework:
 * https://github.com/beehive-lab/tornadovm
 *
 * Copyright (c) 2021, APT Group, Department of Computer Science,
 * The University of Manchester. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package uk.ac.manchester.tornado.drivers.opencl;

import uk.ac.manchester.tornado.drivers.common.GridInfo;

import java.util.Arrays;

import static uk.ac.manchester.tornado.drivers.opencl.OCLFPGAScheduler.DEFAULT_LOCAL_WORK_SIZE;

public class OCLGridInfo implements GridInfo {
    OCLDeviceContext deviceContext;
    public final long[] localWork;

    public OCLGridInfo(OCLDeviceContext deviceContext, long[] localWork) {
        this.deviceContext = deviceContext;
        this.localWork = localWork;
    }

    private boolean checkFPGADefaultLocalWorkGroup() {
        if (Arrays.equals(localWork, DEFAULT_LOCAL_WORK_SIZE)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkGridDimensions() {
        if (deviceContext.isPlatformFPGA()) {
            return checkFPGADefaultLocalWorkGroup();
        } else {
            long[] blockMaxWorkGroupSize = deviceContext.getDevice().getDeviceMaxWorkGroupSize();
            long maxWorkGroupSize = Arrays.stream(blockMaxWorkGroupSize).sum();
            long totalThreads = Arrays.stream(localWork).reduce(1, (a, b) -> a * b);

            return totalThreads <= maxWorkGroupSize;
        }
    }
}
