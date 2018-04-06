package edu.asu.cidse.mc.group2;

import java.util.List;

/**
 * Created by student on 4/5/18.
 */

public class Sample {
    List<AccSample> accSampleList;
    int label;

    public Sample(List<AccSample> accSampleList, int label) {
        this.accSampleList = accSampleList;
        this.label = label;
    }
}

