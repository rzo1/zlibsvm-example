package de.hsheilbronn.mi.controller;

import de.hsheilbronn.mi.configuration.KernelType;
import de.hsheilbronn.mi.configuration.SvmConfigurationImpl;
import de.hsheilbronn.mi.configuration.SvmType;
import de.hsheilbronn.mi.domain.*;
import de.hsheilbronn.mi.process.SvmClassifier;
import de.hsheilbronn.mi.process.SvmClassifierImpl;
import de.hsheilbronn.mi.process.SvmTrainer;
import de.hsheilbronn.mi.process.SvmTrainerImpl;
import de.hsheilbronn.mi.ui.Svm2DPoint;

import java.util.ArrayList;
import java.util.List;

public class SvmController {

    private SvmModel svmModel;

    public void train(List<Svm2DPoint> pointList) {

        SvmTrainer trainer = new SvmTrainerImpl(new SvmConfigurationImpl.Builder()
                .setGamma(0.5).setSvmType(SvmType.C_SVC).setKernelType(KernelType.RBF).setCost(100).build(),"my-custom-trained-model");

        List<SvmDocument> documents = new ArrayList<>();

        for(Svm2DPoint svmPoint : pointList) {
            SvmFeature x = new SvmFeatureImpl(1,svmPoint.getX());
            SvmFeature y = new SvmFeatureImpl(2,svmPoint.getY());
            SvmClassLabel classLabel = new SvmClassLabelImpl(svmPoint.getCategory());

            List<SvmFeature> features = new ArrayList<>();

            features.add(x);
            features.add(y);

            SvmDocument svmDocument = new SvmDocumentImpl(features);
            svmDocument.addClassLabel(classLabel);

            documents.add(svmDocument);

        }

        svmModel = trainer.train(documents);
    }

    public byte predict(Svm2DPoint point) {

        SvmFeature x = new SvmFeatureImpl(1,point.getX());
        SvmFeature y = new SvmFeatureImpl(2,point.getY());
        List<SvmFeature> features = new ArrayList<>();

        features.add(x);
        features.add(y);

        SvmDocument svmDocument = new SvmDocumentImpl(features);

        SvmClassifier svmClassifier = new SvmClassifierImpl(svmModel);

        List<SvmDocument> documents = new ArrayList<>();

        documents.add(svmDocument);

        List<SvmDocument> result = svmClassifier.classify(documents,false);

        return (byte) result.iterator().next().getClassLabelWithHighestProbability().getNumeric();

    }

}
