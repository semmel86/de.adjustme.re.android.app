package re.adjustme.de.readjustme.Prediction.internal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import re.adjustme.de.readjustme.Bean.MotionDataSetDto;

public class svm_train{
    private svm_parameter              param;                                                // set by
    // parse_command_line
    private svm_problem                prob;                                                 // set by
                                                                                             // read_problem
    private svm_model                  model;
    private String                     input_file_name;                                      // set by
                                                                                             // parse_command_line
    private String                     model_file_name;                                      // set by
                                                                                             // parse_command_line
    private String                     error_msg;
    private int                        cross_validation;
    private int                        nr_fold;

    private static svm_print_interface svm_print_null = new svm_print_interface(){
                                                          @Override
                                                          public void print(final String s){}
                                                      };

    public svm_train(){
        this.init();
    }

    // private static void exit_with_help()
    // {
    // System.out.print(
    // "Usage: svm_train [options] training_set_file [model_file]\n"
    // +"options:\n"
    // +"-s svm_type : set type of SVM (default 0)\n"
    // +" 0 -- C-SVC (multi-class classification)\n"
    // +" 1 -- nu-SVC (multi-class classification)\n"
    // +" 2 -- one-class SVM\n"
    // +" 3 -- epsilon-SVR (regression)\n"
    // +" 4 -- nu-SVR (regression)\n"
    // +"-t kernel_type : set type of kernel function (default 2)\n"
    // +" 0 -- linear: u'*v\n"
    // +" 1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
    // +" 2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
    // +" 3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
    // +" 4 -- precomputed kernel (kernel values in training_set_file)\n"
    // +"-d degree : set degree in kernel function (default 3)\n"
    // +"-g gamma : set gamma in kernel function (default 1/num_features)\n"
    // +"-r coef0 : set coef0 in kernel function (default 0)\n"
    // +"-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
    // +"-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
    // +"-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
    // +"-m cachesize : set cache memory size in MB (default 100)\n"
    // +"-e epsilon : set tolerance of termination criterion (default 0.001)\n"
    // +"-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
    // +"-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
    // +"-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
    // +"-v n : n-fold cross validation mode\n"
    // +"-q : quiet mode (no outputs)\n"
    // );
    // System.exit(1);
    // }

    private void do_cross_validation(){
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        final double[] target = new double[this.prob.l];

        svm.svm_cross_validation(this.prob, this.param, this.nr_fold, target);
        if(this.param.svm_type == svm_parameter.EPSILON_SVR || this.param.svm_type == svm_parameter.NU_SVR){
            for(i = 0; i < this.prob.l; i++){
                final double y = this.prob.y[i];
                final double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }
            System.out.print("Cross Validation Mean squared error = " + total_error / this.prob.l + "\n");
            System.out.print(
                    "Cross Validation Squared correlation coefficient = "
                            + ((this.prob.l * sumvy - sumv * sumy) * (this.prob.l * sumvy - sumv * sumy))
                                    / ((this.prob.l * sumvv - sumv * sumv) * (this.prob.l * sumyy - sumy * sumy))
                            + "\n");
        }else{
            for(i = 0; i < this.prob.l; i++){
                if(target[i] == this.prob.y[i]){
                    ++total_correct;
                }
            }
            System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / this.prob.l + "%\n");
        }
    }

    public svm_model train(List<MotionDataSetDto> motionDataSetDtos){
        this.read_problem(motionDataSetDtos);
        if(this.cross_validation != 0){
            this.do_cross_validation();
        }else{
            this.model = svm.svm_train(this.prob, this.param);
//            try{
//                svm.svm_save_model(this.model_file_name, this.model);
//            }
//            catch(final IOException e){
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
        return this.model;
    }
    // train modell adn save to file
    public svm_model train(final String filePath){
        this.input_file_name = filePath;
        this.model_file_name = filePath + ".model";
        try{
            this.read_problem();
        }
        catch(final IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(svm.svm_check_parameter(this.prob, this.param));

        if(this.cross_validation != 0){
            this.do_cross_validation();
        }else{
            this.model = svm.svm_train(this.prob, this.param);
            try{
                svm.svm_save_model(this.model_file_name, this.model);
            }
            catch(final IOException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return this.model;
    }

    // private void run(final String argv[]) throws IOException{
    // this.parse_command_line();
    // this.read_problem();
    // this.error_msg = svm.svm_check_parameter(this.prob, this.param);
    //
    // if(this.error_msg != null){
    // System.err.print("ERROR: " + this.error_msg + "\n");
    // System.exit(1);
    // }
    //
    // if(this.cross_validation != 0){
    // this.do_cross_validation();
    // }else{
    // this.model = svm.svm_train(this.prob, this.param);
    // svm.svm_save_model(this.model_file_name, this.model);
    // }
    // }

    // public static void main(final String argv[]) throws IOException{
    // final svm_train t = new svm_train();
    // t.run(argv);
    // }

    private static double atof(final String s){
        final double d = Double.valueOf(s).doubleValue();
        if(Double.isNaN(d) || Double.isInfinite(d)){
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return (d);
    }

    private static int atoi(final String s){
        return Integer.parseInt(s);
    }

    private void init(){
        final int i;
        final svm_print_interface print_func = null; // default printing to stdout

        this.param = new svm_parameter();
        // default values
        this.param.svm_type = svm_parameter.C_SVC;
        this.param.kernel_type = svm_parameter.POLY;
        this.param.degree = 3;
        this.param.gamma = 0; // 1/num_features
        this.param.coef0 = 0;
        this.param.nu = 0.5;
        this.param.cache_size = 100;
        this.param.C = 1;
        this.param.eps = 1e-3;
        this.param.p = 0.1;
        this.param.shrinking = 1;
        this.param.probability = 0;
        this.param.nr_weight = 0;
        this.param.weight_label = new int[0];
        this.param.weight = new double[0];
        this.cross_validation = 0;

        // // parse options
        // for(i = 0; i < argv.length; i++){
        // if(argv[i].charAt(0) != '-'){
        // break;
        // }
        // if(++i >= argv.length){
        // svm_train.exit_with_help();
        // }
        // switch(argv[i - 1].charAt(1)) {
        // case 's':
        // this.param.svm_type = svm_train.atoi(argv[i]);
        // break;
        // case 't':
        // this.param.kernel_type = svm_train.atoi(argv[i]);
        // break;
        // case 'd':
        // this.param.degree = svm_train.atoi(argv[i]);
        // break;
        // case 'g':
        // this.param.gamma = svm_train.atof(argv[i]);
        // break;
        // case 'r':
        // this.param.coef0 = svm_train.atof(argv[i]);
        // break;
        // case 'n':
        // this.param.nu = svm_train.atof(argv[i]);
        // break;
        // case 'm':
        // this.param.cache_size = svm_train.atof(argv[i]);
        // break;
        // case 'c':
        // this.param.C = svm_train.atof(argv[i]);
        // break;
        // case 'e':
        // this.param.eps = svm_train.atof(argv[i]);
        // break;
        // case 'p':
        // this.param.p = svm_train.atof(argv[i]);
        // break;
        // case 'h':
        // this.param.shrinking = svm_train.atoi(argv[i]);
        // break;
        // case 'b':
        // this.param.probability = svm_train.atoi(argv[i]);
        // break;
        // case 'q':
        // print_func = svm_train.svm_print_null;
        // i--;
        // break;
        // case 'v':
        // this.cross_validation = 1;
        // this.nr_fold = svm_train.atoi(argv[i]);
        // if(this.nr_fold < 2){
        // System.err.print("n-fold cross validation: n must >= 2\n");
        // svm_train.exit_with_help();
        // }
        // break;
        // case 'w':
        // ++this.param.nr_weight;{
        // final int[] old = this.param.weight_label;
        // this.param.weight_label = new int[this.param.nr_weight];
        // System.arraycopy(old, 0, this.param.weight_label, 0, this.param.nr_weight - 1);
        // }
        //
        // {
        // final double[] old = this.param.weight;
        // this.param.weight = new double[this.param.nr_weight];
        // System.arraycopy(old, 0, this.param.weight, 0, this.param.nr_weight - 1);
        // }
        //
        // this.param.weight_label[this.param.nr_weight - 1] = svm_train.atoi(argv[i - 1].substring(2));
        // this.param.weight[this.param.nr_weight - 1] = svm_train.atof(argv[i]);
        // break;
        // default:
        // System.err.print("Unknown option: " + argv[i - 1] + "\n");
        // svm_train.exit_with_help();
        // }
        // }
        //
        // svm.svm_set_print_string_function(print_func);
        //
        // // determine filenames
        //
        // if(i >= argv.length){
        // svm_train.exit_with_help();
        // }
        //
        // this.input_file_name = argv[i];
        //
        // if(i < argv.length - 1){
        // this.model_file_name = argv[i + 1];
        // }else{
        // int p = argv[i].lastIndexOf('/');
        // ++p; // whew...
        // this.model_file_name = argv[i].substring(p) + ".model";
        // }
    }

    void read_problem(List<MotionDataSetDto> motionDataSetDtos){

        final Vector<Double> vy = new Vector<Double>();
        final Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;

        for(MotionDataSetDto currentMotion:motionDataSetDtos) {
            // while(true){
            if (motionDataSetDtos != null) {
                final String line = currentMotion.toSVMLightStr();
                if (line == null) {
                    break;
                }
                final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

                vy.addElement(svm_train.atof(st.nextToken()));
                final int m = st.countTokens() / 2;
                final svm_node[] x = new svm_node[m];
                for (int j = 0; j < m; j++) {
                    x[j] = new svm_node();
                    x[j].index = svm_train.atoi(st.nextToken());
                    x[j].value = svm_train.atof(st.nextToken());
                }
                if (m > 0) {
                    max_index = Math.max(max_index, x[m - 1].index);
                }
                vx.addElement(x);
            }

            this.prob = new svm_problem();
            this.prob.l = vy.size();
            this.prob.x = new svm_node[this.prob.l][];
            for (int i = 0; i < this.prob.l; i++) {
                this.prob.x[i] = vx.elementAt(i);
            }
            this.prob.y = new double[this.prob.l];
            for (int i = 0; i < this.prob.l; i++) {
                this.prob.y[i] = vy.elementAt(i);
            }

            if (this.param.gamma == 0 && max_index > 0) {
                this.param.gamma = 1.0 / max_index;
            }

            if (this.param.kernel_type == svm_parameter.PRECOMPUTED) {
                for (int i = 0; i < this.prob.l; i++) {
                    if (this.prob.x[i][0].index != 0) {
                        System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                        System.exit(1);
                    }
                    if ((int) this.prob.x[i][0].value <= 0 || (int) this.prob.x[i][0].value > max_index) {
                        System.err.print("Wrong input format: sample_serial_number out of range\n");
                        System.exit(1);
                    }
                }
            }
        }
    }
    // read in a problem (in svmlight format)
    void read_problem() throws IOException{
        System.out.println(this.input_file_name);
        final BufferedReader fp = new BufferedReader(new FileReader(this.input_file_name));
        final Vector<Double> vy = new Vector<Double>();
        final Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;

        while(true){
            final String line = fp.readLine();
            if(line == null){
                break;
            }

            final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            vy.addElement(svm_train.atof(st.nextToken()));
            final int m = st.countTokens() / 2;
            final svm_node[] x = new svm_node[m];
            for(int j = 0; j < m; j++){
                x[j] = new svm_node();
                x[j].index = svm_train.atoi(st.nextToken());
                x[j].value = svm_train.atof(st.nextToken());
            }
            if(m > 0){
                max_index = Math.max(max_index, x[m - 1].index);
            }
            vx.addElement(x);
        }

        this.prob = new svm_problem();
        this.prob.l = vy.size();
        this.prob.x = new svm_node[this.prob.l][];
        for(int i = 0; i < this.prob.l; i++){
            this.prob.x[i] = vx.elementAt(i);
        }
        this.prob.y = new double[this.prob.l];
        for(int i = 0; i < this.prob.l; i++){
            this.prob.y[i] = vy.elementAt(i);
        }

        if(this.param.gamma == 0 && max_index > 0){
            this.param.gamma = 1.0 / max_index;
        }

        if(this.param.kernel_type == svm_parameter.PRECOMPUTED){
            for(int i = 0; i < this.prob.l; i++){
                if(this.prob.x[i][0].index != 0){
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if((int) this.prob.x[i][0].value <= 0 || (int) this.prob.x[i][0].value > max_index){
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }
        }

        fp.close();
    }
    
    
 // read in a problem (in svmlight format)
    void setProblem(Vector<Double> features) throws IOException{
        System.out.println(this.input_file_name);
        final BufferedReader fp = new BufferedReader(new FileReader(this.input_file_name));
        final Vector<Double> vy = new Vector<Double>();
        final Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;

        while(true){
            final String line = fp.readLine();
            if(line == null){
                break;
            }

            final StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            vy.addElement(svm_train.atof(st.nextToken())); //first set class 
            final int m = st.countTokens() / 2; // get feature amount
            final svm_node[] x = new svm_node[m]; // create node
            for(int j = 0; j < m; j++){
                x[j] = new svm_node();
                x[j].index = svm_train.atoi(st.nextToken()); // key
                x[j].value = svm_train.atof(st.nextToken()); // value
             }
            if(m > 0){
                max_index = Math.max(max_index, x[m - 1].index); 
            }
            vx.addElement(x);
        }

        this.prob = new svm_problem();
        this.prob.l = vy.size();
        this.prob.x = new svm_node[this.prob.l][];
        for(int i = 0; i < this.prob.l; i++){
            this.prob.x[i] = vx.elementAt(i);
        }
        this.prob.y = new double[this.prob.l];
        for(int i = 0; i < this.prob.l; i++){
            this.prob.y[i] = vy.elementAt(i);
        }

        if(this.param.gamma == 0 && max_index > 0){
            this.param.gamma = 1.0 / max_index;
        }

        if(this.param.kernel_type == svm_parameter.PRECOMPUTED){
            for(int i = 0; i < this.prob.l; i++){
                if(this.prob.x[i][0].index != 0){
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if((int) this.prob.x[i][0].value <= 0 || (int) this.prob.x[i][0].value > max_index){
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }
        }

        fp.close();
    }
}
