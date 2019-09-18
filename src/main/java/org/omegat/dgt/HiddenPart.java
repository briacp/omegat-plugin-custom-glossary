package org.omegat.dgt;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class HiddenPart extends Part {
    String prefix, suffix;

    public HiddenPart(int start, int end, String prefix, String suffix) {
        super(start, end);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    static final SimpleAttributeSet LINK_ATTRIBUTE = new SimpleAttributeSet(),
            ZERO_ATTRIBUTE = new SimpleAttributeSet();
    static {
        StyleConstants.setUnderline(LINK_ATTRIBUTE, true);
        StyleConstants.setFontSize(ZERO_ATTRIBUTE, 0);
    }

    public HiddenPart decale(int start) {
        return new HiddenPart(this.start + start, len, prefix, suffix);
    }

    public void apply(StyledDocument doc) {
        doc.setCharacterAttributes(start, len, LINK_ATTRIBUTE, false);
        changeState(doc);
    }

    boolean state = true;

    public void changeState(StyledDocument doc) {
        if (state) {
            doc.setCharacterAttributes(start + prefix.length(), len - suffix.length() - prefix.length(),
                    ZERO_ATTRIBUTE, true);
        } else {
            doc.setCharacterAttributes(start + prefix.length(), len - suffix.length() - prefix.length(),
                    DGTGlossary.NO_ATTRIBUTES, true);
        }
        state = !state;
    }
}