package re.adjustme.de.readjustme.Prediction.internal;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class svm_predict {
    private static svm_print_interface svm_print_null = new svm_print_interface() {
        @Override
        public void print(final String s) {
        }
    };

    private static svm_print_interface svm_print_stdout = new svm_print_interface() {
        @Override
        public void print(final String s) {
            System.out.print(s);
        }
    };

    private static svm_print_interface svm_print_string = svm_predict.svm_print_stdout;

    static void info(final String s) {
        svm_predict.svm_print_string.print(s);
    }

    private static double atof(final String s) {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(final String s) {
        return Integer.parseInt(s);
    }

    private static void predict(
            final BufferedReader input,
            final DataOutputStream output,
            final svm_model model,
            final int predict_probability) throws IOException {
        int correct = 0;
        int total = 0;
        double error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

        final int svm_type = svm.svm_get_svm_type(model);
        final int nr_class = svm.svm_get_nr_class(model);
        double[] prob_estimates = null;

        if (predict_probability == 1) {
            if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
                svm_predict.info(
                        "Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
                                + svm.svm_get_svr_probability(model) + "\n");
            } else {
                final int[] labels = new int[nr_class];
                svm.svm_get_labels(model, labels);
                prob_estimates = new double[nr_class];
                output.writeBytes("labels");
                for (int j = 0; j < nr_class; j++) {
                    output.writeBytes(" " + labels[j]);
                }
                output.writeBytes("\n");
            }
        }
        // Prediction
        while (true) {
            final String line = input.readLine();
            if (line == null) {
                break;
            }
            // split line
            final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            final double target = svm_predict.atof(st.nextToken());//class
            final int m = st.countTokens() / 2;
            final svm_node[] x = new svm_node[m];
            for (int j = 0; j < m; j++) {
                x[j] = new svm_node();
                x[j].index = svm_predict.atoi(st.nextToken());// key
                x[j].value = svm_predict.atof(st.nextToken());// value
            }

            double v;
            if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
                // Multi-Classification problems
                v = svm.svm_predict_probability(model, x, prob_estimates); // internal prediction
                output.writeBytes(v + " ");
                for (int j = 0; j < nr_class; j++) {
                    output.writeBytes(prob_estimates[j] + " ");
                }
                output.writeBytes("\n");
            } else {
                // simple one class problem (true or false)
                v = svm.svm_predict(model, x);
                output.writeBytes(v + "\n");
            }

            if (v == target) {
                ++correct;
            }
            error += (v - target) * (v - target);
            sumv += v;
            sumy += target;
            sumvv += v * v;
            sumyy += target * target;
            sumvy += v * target;
            ++total;
        }
        if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
            svm_predict.info("Mean squared error = " + error / total + " (regression)\n");
            svm_predict.info(
                    "Squared correlation coefficient = "
                            + ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy))
                            / ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy))
                            + " (regression)\n");
        } else {
            svm_predict.info(
                    "Accuracy = " + (double) correct / total * 100 + "% (" + correct + "/" + total
                            + ") (classification)\n");
        }
    }

    private static void exit_with_help() {
        System.err.print(
                "usage: svm_predict [options] test_file model_file output_file\n" + "options:\n"
                        + "-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
                        + "-q : quiet mode (no outputs)\n");
        System.exit(1);
    }

    public static void predict(final String pathTest, final svm_model inmodel) throws IOException {
        final int i, predict_probability = 0;
        svm_predict.svm_print_string = svm_predict.svm_print_stdout;

        // parse options
        // for(i=0;i<argv.length;i++)
        // {
        // if(argv[i].charAt(0) != '-') break;
        // ++i;
        // switch(argv[i-1].charAt(1))
        // {
        // case 'b':
        // predict_probability = atoi(argv[i]);
        // break;
        // case 'q':
        // svm_print_string = svm_print_null;
        // i--;
        // break;
        // default:
        // System.err.print("Unknown option: " + argv[i-1] + "\n");
        // exit_with_help();
        // }
        // }
        // if(i>=argv.length-2)
        // exit_with_help();
        try {
            final BufferedReader input = new BufferedReader(new FileReader(pathTest));
            final DataOutputStream output = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(pathTest + "r.esult")));
            final svm_model model = inmodel;
            if (model == null) {
                // System.err.print("can't open model file " + argv[i + 1] + "\n");
                System.exit(1);
            }
            if (predict_probability == 1) {
                if (svm.svm_check_probability_model(model) == 0) {
                    System.err.print("Model does not support probabiliy estimates\n");
                    System.exit(1);
                }
            } else {
                if (svm.svm_check_probability_model(model) != 0) {
                    svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
                }
            }
            svm_predict.predict(input, output, model, predict_probability);
            input.close();
            output.close();
        } catch (final FileNotFoundException e) {
            svm_predict.exit_with_help();
        } catch (final ArrayIndexOutOfBoundsException e) {
            svm_predict.exit_with_help();
        }
    }
}
