import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


public class word_par {
    public double TF;
    public int size;
    public double score;

    public word_par() {
        size=0;
        score=0;
        TF=0;
    }

    public word_par(double TF, int size, double score) {
        this.TF = TF;
        this.size = size;
        this.score = score;
    }

    public word_par sum(word_par w) {
        this.score += w.score;
        this.TF += w.TF;
        return this;
    }

}
