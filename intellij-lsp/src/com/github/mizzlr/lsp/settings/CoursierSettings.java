package com.github.mizzlr.lsp.settings;

import com.github.mizzlr.lsp.settings.gui.CoursierGUI;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class CoursierSettings implements Configurable {

    private static CoursierGUI coursierGUI;

    @Nls
    @Override
    public String getDisplayName() {
        return "Coursier";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "com.github.mizzlr.lsp.settings.CoursierSettings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        coursierGUI = new CoursierGUI();
        return coursierGUI.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return coursierGUI.isModified();
    }

    @Override
    public void apply() {
        coursierGUI.apply();
    }

    @Override
    public void reset() {
        coursierGUI.reset();
    }

    @Override
    public void disposeUIResources() {
        coursierGUI = null;
    }
}
