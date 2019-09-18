package net.briac.omegat;

import java.util.List;

import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.core.events.IGlossaryEventListener;
import org.omegat.gui.glossary.GlossaryEntry;
import org.omegat.gui.glossary.GlossaryTextArea;

public class CustomGlossary implements IGlossaryEventListener {

    // Plugin setup
    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                CoreEvents.registerGlossaryEventListener(new CustomGlossary());
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
        StringBuffer buf = new StringBuffer();
        buf.append("** CUSTOM GLOSSARY **");
        for (GlossaryEntry entry : entries) {
            buf.append(entry.getSrcText());
            buf.append("=>");
            buf.append(entry.getLocText());
            buf.append("\n\n");
        }
        buf.append("** /CUSTOM GLOSSARY **");
        glossaryTextArea.setText(buf.toString());
    }
}
