package de.hsheilbronn.mi.controller;


import de.hhn.mi.domain.SvmClassLabel;
import de.hhn.mi.domain.SvmDocument;
import de.hhn.mi.domain.SvmFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SvmDocumentImpl implements SvmDocument {

    private final List<SvmFeature> features;
    private final List<SvmClassLabel> classLabels = new ArrayList<>();

    public SvmDocumentImpl(List<SvmFeature> features) {
        this.features = features;
    }

    public List<SvmFeature> getSvmFeatures() {
        return features;
    }

    public SvmClassLabel getClassLabelWithHighestProbability() {
        if (classLabels.isEmpty()) {
            return null;
        }

        return Collections.max(classLabels);
    }

    @Override
    public List<SvmClassLabel> getAllClassLabels() {
        return Collections.unmodifiableList(classLabels);
    }

    @Override
    public void addClassLabel(SvmClassLabel classLabel) {
        assert (classLabel != null);
        this.classLabels.add(classLabel);
    }

}