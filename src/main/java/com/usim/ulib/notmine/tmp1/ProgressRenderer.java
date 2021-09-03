/**
Copyright (c) 2011-present - Luu Gia Thuy

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package com.usim.ulib.notmine.tmp1;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *  This class renders a JProgressBar in a table cell.
 */
public class ProgressRenderer extends JProgressBar implements TableCellRenderer {
    
	@Serial
    private static final long serialVersionUID = -2002374113358949051L;

	/**
	 *  Constructor for ProgressRenderer.
	 */
    public ProgressRenderer(int min, int max) {
        super(min, max);
    }
    
    /**
     * Returns this JProgressBar as the renderer for the given table cell. 
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            											boolean hasFocus, int row, int column) {
        
    	// set JProgressBar's percent complete value.
        setValue((int) ((Float) value).floatValue());
        return this;
    }
}
