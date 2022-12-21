/*
 * Copyright (c) 2007, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 */


package org.graalvm.visualvm.lib.profiler.snaptracer.impl.export;

import java.io.IOException;
import java.io.Writer;
import javax.swing.table.TableModel;
import org.graalvm.visualvm.lib.profiler.snaptracer.TracerProgressObject;

/**
 *
 * @author Jiri Sedlacek
 */
final class XMLExporter extends Exporter {

    private float step = 1;
    private int lastStep = 0;


    protected int getSteps(TableModel model) {
        int steps = model.getRowCount();
        if (steps > MAX_STEPS) {
            step = MAX_STEPS / (float)steps;
            steps = MAX_STEPS;
        }
        return steps;
    }

    protected void writeHeader(TableModel model, String title, Writer writer,
                               TracerProgressObject progress) throws IOException {
        writeLine(writer, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); // NOI18N
        writeLine(writer, "<ExportedView Name=\"" + title + "\">"); // NOI18N
    }

    protected void writeData(TableModel model, String title, Writer writer,
                             TracerProgressObject progress) throws IOException {
        int columnsCount = model.getColumnCount();
        int rowsCount = model.getRowCount();

        writeLine(writer, "  <TableData NumRows=\"" + rowsCount + // NOI18N
                             "\" NumColumns=\"" + columnsCount + "\">"); // NOI18N

        writeLine(writer, "    <TableHeader>"); // NOI18N
        for (int c = 0; c < columnsCount; c++)
            writeLine(writer, "      <TableColumn>" + model.getColumnName(c) + "</TableColumn>"); // NOI18N
        writeLine(writer, "    </TableHeader>"); // NOI18N

        writeLine(writer, "    <TableBody>"); // NOI18N
        for (int r = 0; r < rowsCount; r++) {
            writeLine(writer, "      <TableRow>"); // NOI18N
            for (int c = 0; c < columnsCount; c++)
                writeLine(writer, "        <TableColumn>" + model.getValueAt(r, c) + "</TableColumn>"); // NOI18N
            writeLine(writer, "      </TableRow>"); // NOI18N
            
            if (progress.isFinished()) break;

            if (step == 1) {
                progress.addStep();
            } else {
                int currentStep = (int)(r * step);
                if (currentStep > lastStep) {
                    progress.addStep();
                    lastStep = currentStep;
                }
            }
        }
        writeLine(writer, "    </TableBody>"); // NOI18N

        writeLine(writer, "  </TableData>"); // NOI18N
    }

    protected void writeFooter(TableModel model, String title, Writer writer,
                               TracerProgressObject progress) throws IOException {
        writeLine(writer, "</ExportedView>"); // NOI18N
    }

}
