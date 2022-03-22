import java.util.Objects;

public class Komplex {
    double r = 0;
    double im = 0;

    public Komplex(double r, double im)
    {
        this.r = r;
        this.im = im;
    }

    public Komplex add(Komplex b) {
        return new Komplex(b.r + r, b.im + im);
    }

    public Komplex multiply(Komplex b)
    {
        double vH;
        double hH;
        vH = r * b.r - im * b.im;
        hH = im * b.r + r * b.im;
        return new Komplex(vH, hH);
    }

    public Komplex arg()
    {
        double arctan;
        arctan = r/im;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Komplex komplex = (Komplex) o;
        return Double.compare(komplex.r, r) == 0 && Double.compare(komplex.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, im);
    }

    @Override
    public String toString() {
        return "r=" + r + ", im=" + im;
    }
}

