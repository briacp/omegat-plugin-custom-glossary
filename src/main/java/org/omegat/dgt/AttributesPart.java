package org.omegat.dgt;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

class AttributesPart extends Part {
    private AttributeSet attr;

    public AttributesPart(int start, int len, AttributeSet attr) {
        super(start, len);
        this.attr = attr;
    }

    public void apply(StyledDocument doc) {
        doc.setCharacterAttributes(start, len, attr, false);
    }

    public AttributesPart decale(int start) {
        return new AttributesPart(this.start + start, len, attr);
    }
}