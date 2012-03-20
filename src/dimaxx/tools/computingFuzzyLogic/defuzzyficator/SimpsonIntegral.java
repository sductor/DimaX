package dimaxx.tools.computingFuzzyLogic.defuzzyficator;

public abstract class SimpsonIntegral {

	protected abstract double F1(double x);

	protected abstract double F2(double x);

	public double simpson1(final int N, final double A, final double B) {
		double X, h, Iapp0, Iapp1, Iapp2, Iapp;
		int NN, i;

		// Etape 1
		h = (B - A) / N;

		// Etape 2
		Iapp0 = this.F1(A) + this.F1(B);
		Iapp1 = 0.0;
		Iapp2 = 0.0;

		// Etape 3
		NN = N - 1;
		for (i = 1; i <= NN; i++) {
			// Etape 4
			X = A + i * h;
			// Etape 5
			if (i % 2 == 0) {
				Iapp2 = Iapp2 + this.F1(X);
			} else {
				Iapp1 = Iapp1 + this.F1(X);
			}
		}

		// Etape 6
		Iapp = (Iapp0 + 2.0 * Iapp2 + 4.0 * Iapp1) * h / 3.0;

		// Etape 7
		return Iapp;
	}

	public double simpson2(final int N, final double A, final double B) {
		double X, h, Iapp0, Iapp1, Iapp2, Iapp;
		int NN, i;

		// Etape 1
		h = (B - A) / N;

		// Etape 2
		Iapp0 = this.F2(A) + this.F2(B);
		Iapp1 = 0.0;
		Iapp2 = 0.0;

		// Etape 3
		NN = N - 1;
		for (i = 1; i <= NN; i++) {
			// Etape 4
			X = A + i * h;
			// Etape 5
			if (i % 2 == 0) {
				Iapp2 = Iapp2 + this.F2(X);
			} else {
				Iapp1 = Iapp1 + this.F2(X);
			}
		}

		// Etape 6
		Iapp = (Iapp0 + 2.0 * Iapp2 + 4.0 * Iapp1) * h / 3.0;

		// Etape 7
		return Iapp;
	}
}
