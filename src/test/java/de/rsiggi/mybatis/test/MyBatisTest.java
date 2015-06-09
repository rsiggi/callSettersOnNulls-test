package de.rsiggi.mybatis.test;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import dbunit.DBUnit;
import de.rsiggi.mybatis.sample.ChildBean;
import de.rsiggi.mybatis.sample.ParentBean;

public class MyBatisTest {

	@Rule public DBUnit dbunit = new DBUnit(databaseTester, DatabaseOperation.CLEAN_INSERT, null, dataSet, null);

	@Rule public TestName testName = new TestName();

	private static SqlSessionFactory sqlSessionFactory;
	private SqlSession session;

	private static DataSource dataSource;

	private static IDatabaseTester databaseTester;

	private static IDataSet dataSet;

	@BeforeClass
	public static void beforeClass() throws IOException, DataSetException {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		dataSource = sqlSessionFactory.getConfiguration().getEnvironment().getDataSource();
		databaseTester = new DataSourceDatabaseTester(dataSource);
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(false);
		dataSet = builder.build(new File("src/test/data/testdata-test.xml"));
	}

	@Before
	public void setUp() throws SQLException, IOException, DataSetException {
		session = sqlSessionFactory.openSession();

		dbunit = new DBUnit(databaseTester, null, null, dataSet, null);
	}

	@After
	public void tearDown() {
		session.close();
	}

	@Test
	public void test() throws IOException, DatabaseUnitException {
		dbunit = new DBUnit(databaseTester, DatabaseOperation.CLEAN_INSERT, null, dataSet, null);
		ParentBean parentBean = session.selectOne("mapper." + testName.getMethodName());
		assertThat("bean should not be NULL", parentBean, CoreMatchers.is(CoreMatchers.notNullValue()));
		assertThat(parentBean.getName(), CoreMatchers.is("p1"));
		System.out.println(parentBean);
		ChildBean childBean = parentBean.getClient();
		assertThat(parentBean.toString(), CoreMatchers.is("ParentBean [name=p1, client=ChildBean [name=null, child=ChildBean [name=null, child=null, beans=null], beans=null]]"));
		checkChild(parentBean.toString(), childBean);
		childBean = childBean.getChild();
		checkChild(childBean.toString(),childBean);
	}

	private void checkChild(String msg, ChildBean bean) {
		assertThat(msg, bean, CoreMatchers.is(CoreMatchers.notNullValue()));
	}

}
