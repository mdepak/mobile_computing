package edu.asu.cidse.mc.group2;


import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.*;
import java.util.*;

import libsvm.*;

/**
 * Created by student on 4/5/18.
 */

public class TrainSVM {


    private svm_parameter param;        // set by pae_command_line
    private svm_problem prob;        // set by read_problem
    private svm_model model;
    private String input_file_name;        // set by parse_command_line
    private String model_file_name;        // set by parse_command_line
    private String error_msg;
    private int cross_validation;
    private int nr_fold;
    private Context context;
    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {
        }
    };

    public TrainSVM(Context con){
        context = con;
    }

    private static void exit_with_help() {
        System.out.print(
                "Usage: svm_train [options] training_set_file [model_file]\n"
                        + "options:\n"
                        + "-s svm_type : set type of SVM (default 0)\n"
                        + "	0 -- C-SVC		(multi-class classification)\n"
                        + "	1 -- nu-SVC		(multi-class classification)\n"
                        + "	2 -- one-class SVM\n"
                        + "	3 -- epsilon-SVR	(regression)\n"
                        + "	4 -- nu-SVR		(regression)\n"
                        + "-t kernel_type : set type of kernel function (default 2)\n"
                        + "	0 -- linear: u'*v\n"
                        + "	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
                        + "	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
                        + "	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
                        + "	4 -- precomputed kernel (kernel values in training_set_file)\n"
                        + "-d degree : set degree in kernel function (default 3)\n"
                        + "-g gamma : set gamma in kernel function (default 1/num_features)\n"
                        + "-r coef0 : set coef0 in kernel function (default 0)\n"
                        + "-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
                        + "-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
                        + "-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
                        + "-m cachesize : set cache memory size in MB (default 100)\n"
                        + "-e epsilon : set tolerance of termination criterion (default 0.001)\n"
                        + "-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
                        + "-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
                        + "-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
                        + "-v n : n-fold cross validation mode\n"
                         + "-q : quiet mode (no outputs)\n"
        );
        System.exit(1);
    }

    private void do_cross_validation() {
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[prob.l];

        svm.svm_cross_validation(prob, param, nr_fold, target);
        if (param.svm_type == svm_parameter.EPSILON_SVR ||
                param.svm_type == svm_parameter.NU_SVR) {
            for (i = 0; i < prob.l; i++) {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }
            System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
            System.out.print("Cross Validation Squared correlation coefficient = " +
                    ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy)) /
                            ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy)) + "\n"
            );
        } else {
            for (i = 0; i < prob.l; i++)
                if (target[i] == prob.y[i])
                    ++total_correct;
            Toast.makeText(context, "Cross Validation Accuracy = " + 100.0 * total_correct / prob.l + "%\n",
                    Toast.LENGTH_LONG).show();
            System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / prob.l + "%\n");
        }
    }

    private void initModelParams()
    {
        param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;	// 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        cross_validation = 1;
        nr_fold = 3;

        svm.svm_set_print_string_function(svm_print_null);

        model_file_name = Environment.getExternalStorageDirectory()+File.separator + "test_svm.model";

        Toast.makeText(context, "SVM Parameters : Kernel: RBF,Type: C_SVC, gamma: 0.006, Cross validation : 3- fold, slack epsilon :1e-3 ", Toast.LENGTH_LONG).show();
    }

    public void run(List<Sample> trainSample, String argv[]) throws IOException {
        initModelParams();
        read_acc_problem(trainSample);
        error_msg = svm.svm_check_parameter(prob, param);

        if (error_msg != null) {
            System.err.print("ERROR: " + error_msg + "\n");
            System.exit(1);
        }

        if (cross_validation != 0) {

            do_cross_validation();
            model = svm.svm_train(prob, param);
            svm.svm_save_model(model_file_name, model);
        } else {

        }
    }
/*
    public void main(List<Sample> sampleList) throws IOException {
        TrainSVM t = new TrainSVM();
        String[] arg = new String[0];


        t.run(sampleList, arg);
    }
*/

    private static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return (d);
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    // read in a problem (in svmlight format)


    private void read_acc_problem(List<Sample> sampleList) {

        prob = new svm_problem();
        prob.l = sampleList.size();


        prob.x = new svm_node[prob.l][];
        prob.y = new double[prob.l];

        for (int i = 0; i < prob.l; i++)
        {
            Sample trainSample = sampleList.get(i);

            //Set the label
            prob.y[i] = trainSample.label;

            svm_node[] x = new svm_node[150];

            int index = 0;
            for (AccSample accSample : trainSample.accSampleList) {
                //Acc X
                x[index] = new svm_node();
                x[index].index = index;
                x[index].value = accSample.getAccx();
                index++;

                //Acc Y
                x[index] = new svm_node();
                x[index].index = index;
                x[index].value = accSample.getAccy();
                index++;

                //Acc Z
                x[index] = new svm_node();
                x[index].index = index;
                x[index].value = accSample.getAccz();
                index++;
            }
            prob.x[i] = x;

        }

        if (param.gamma == 0)
            param.gamma = 1.0 / 150;

        if (param.kernel_type == svm_parameter.PRECOMPUTED)
            for (int i = 0; i < prob.l; i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if ((int) prob.x[i][0].value <= 0 || (int) prob.x[i][0].value > 150) {
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }
    }

}
