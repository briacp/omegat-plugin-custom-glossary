package org.omegat.dgt;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.core.events.IGlossaryEventListener;
import org.omegat.gui.glossary.GlossaryEntry;
import org.omegat.gui.glossary.GlossaryTextArea;
import org.omegat.util.gui.Styles;

public class DGTGlossary implements IGlossaryEventListener {

    public static final AttributeSet NO_ATTRIBUTES = Styles.createAttributeSet(null, null, false, null);
    public static final AttributeSet PRIORITY_ATTRIBUTES = Styles.createAttributeSet(null, null, true, null);

    // Styles.EditorColor.COLOR_GLOSSARY_NOTE.getColor()
    public static final AttributeSet SOURCE_ATTRIBUTES = Styles.createAttributeSet(Color.blue, null, null, null);
    public static final AttributeSet TARGET_ATTRIBUTES = Styles.createAttributeSet(Color.green.darker(), null, null, null);
    public static final AttributeSet NOTES_ATTRIBUTES = Styles.createAttributeSet(Color.decode("#cc00cc"), null, null, null);

    public StringBuilder text = new StringBuilder();
    public LinkedList<AttributesPart> attrParts = new LinkedList<>();
    public LinkedList<AttributesPart> attrBolds = new LinkedList<>();
    public LinkedList<TooltipPart> tooltips = new LinkedList<>();
    public LinkedList<HiddenPart> hiddenParts = new LinkedList<>();

    // Plugin setup
    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                CoreEvents.registerGlossaryEventListener(new DGTGlossary());
                
                // TODO Add prefs for color?
                //Core.getMainWindow().pre
            }

            @Override
            public void onApplicationShutdown() {
                /* empty */
            }
        });
    }

    public static void unloadPlugins() {
        /* empty */
    }

    @Override
    public void onGlossaryChanged(List<GlossaryEntry> entries, GlossaryTextArea glossaryTextArea) {
        DGTStyledString buf = new DGTStyledString();
        // FIXME entries.sort(c);
        for (GlossaryEntry entry : entries) {
            DGTEntry dgtEntry = new DGTEntry(entry);
            DGTStyledString str = dgtEntry.toStyledString();
            buf.append(str);
            buf.append("\n");
        }

        System.out.println("onGlossaryChanged:" + buf.text.toString());

        glossaryTextArea.setText(buf.text.toString());

        StyledDocument doc = glossaryTextArea.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), NO_ATTRIBUTES, true); // remove old bold settings first
        for (AttributesPart part : buf.attrParts) {
            part.apply(doc);
        }
        for (AttributesPart part : buf.attrBolds) {
            part.apply(doc);
        }
        for (HiddenPart part : buf.hiddenParts) {
            part.apply(doc);
        }
        tooltips = buf.tooltips;
        hiddenParts = buf.hiddenParts;
    }

}
