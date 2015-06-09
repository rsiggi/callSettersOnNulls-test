package dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.IOperationListener;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.rules.ExternalResource;

/**
 * Usage :
 *
 * like this.
 *
 * class SomeTest {
 *	@Rule
 *	public final DBUnit dbunit;
 *
 *	public SomeTest() throws DataSetException {
 *		dbunit = new DBUnit(
 *			new JdbcDatabaseTester("some.Driver","jdbc:someurl","user","pass"),
 *			DatabaseOperation.CLEAN_INSERT, null, new CsvDataSet(new File("./src/test/dataset/default")), null);
 *	}
 * }

 * @see https://gist.github.com/gensan/3366412#file-dbunit-java
 *
 * @author gensan
 */
public class DBUnit extends ExternalResource {

	private final IDatabaseTester databaseTester;
	private final DatabaseOperation setupOperation;
	private final DatabaseOperation tearDownOperation;
	private final IDataSet dataSet;
	private final IOperationListener operationListener;

	public DBUnit(IDatabaseTester databaseTester,
			DatabaseOperation setupOperation, DatabaseOperation tearDownOperation, IDataSet dataSet,
			IOperationListener operationListener) {
		this.databaseTester = databaseTester;
		this.setupOperation = setupOperation == null ? DatabaseOperation.CLEAN_INSERT : setupOperation;
		this.tearDownOperation = tearDownOperation == null ? DatabaseOperation.NONE : tearDownOperation;
		this.dataSet = dataSet;
		this.operationListener = operationListener;
	}

	@Override
	protected void before() throws Throwable {
		final IDatabaseTester databaseTester = getDatabaseTester();
		databaseTester.setSetUpOperation(getSetUpOperation());
		databaseTester.setDataSet(getDataSet());
		databaseTester.setOperationListener(getOperationListener());
		databaseTester.onSetup();
	}

	protected IOperationListener getOperationListener() {
		return operationListener;
	}

	protected IDataSet getDataSet() {
		return dataSet;
	}

	protected DatabaseOperation getSetUpOperation() {
		return setupOperation;
	}

	protected IDatabaseTester getDatabaseTester() {
		return databaseTester;
	}

	@Override
	protected void after() {
		try {
			final IDatabaseTester databaseTester = getDatabaseTester();
			databaseTester.setTearDownOperation(getTearDownOperation());
			databaseTester.setDataSet(getDataSet());
			databaseTester.setOperationListener(getOperationListener());
			databaseTester.onTearDown();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DatabaseOperation getTearDownOperation() {
		return tearDownOperation;
	}

}