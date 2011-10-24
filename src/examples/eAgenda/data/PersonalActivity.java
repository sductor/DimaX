package examples.eAgenda.data;

/** Simplest activity : an individual one, with no other caracterisitcs */
public class PersonalActivity extends Activity {

	/**
	 *
	 */
	private static final long serialVersionUID = 834444406489233462L;
	public PersonalActivity() {
		super();
	}
	public PersonalActivity(final String titl, final String desc, final boolean movable) {
		super(titl, desc, movable);
	}
}
