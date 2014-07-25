package br.com.mvbos.test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.mvbos.nodethunder.core.NodeThunder;
import br.com.mvbos.test.vo.Client;

/**
 * 
 * @author Marcus Becker
 * 
 */

public class NodeThunderTest {

	private Repository repository = null;
	private Session session;
	private Node root;
	private Client clientVO;

	@Before
	public void before() throws Exception {
		session = getSession();
		root = session.getNode("/content");

		List<String> fakePhones = Arrays.asList(new String[] { "55-11-123-321",
				"55-11-456-654", "55-11-789-987" });

		clientVO = new Client(1L, "Aa«„o", 2900.00, new BigDecimal(170000.20),
				Calendar.getInstance(), Boolean.FALSE);

		clientVO.setPhones(fakePhones);

		if (root.hasNode("nodethunder")) {
			root = root.getNode("nodethunder");
		} else {
			root = root.addNode("nodethunder");
		}

		if (root.hasNode("1")) {
			root.getNode("1").remove();
			session.save();
		}

	}

	@After
	public void after() {
		session.logout();
	}

	@Test
	public void save() throws Exception {

		NodeThunder nt = NodeThunder.create(session, root);

		nt.save(clientVO);

		Assert.assertTrue(root.hasNode("1"));

	}

	@Test
	public void load() throws Exception {

		NodeThunder nt = NodeThunder.create(session, root);

		nt.save(clientVO);

		Client clt = nt.popule(root.getNode("1"), Client.class);

		System.out.println(clientVO);
		System.out.println(clt);
		
		Assert.assertTrue(clt.getId().equals(clientVO.getId())
				&& clt.getName().equals(clientVO.getName())
				&& clt.getSalary().equals(clientVO.getSalary())
				&& clt.getAcountBalance().equals(clientVO.getAcountBalance())
				&& clt.getLastCheck().getTimeInMillis() == clientVO.getLastCheck().getTimeInMillis()
				&& clt.getReciveNews().equals(clientVO.getReciveNews())
				&& clt.getPhones().equals(clientVO.getPhones()));

	}

	public Session getSession() {
		try {

			repository = JcrUtils
					.getRepository("http://localhost:4502/crx/server");

			Session s = repository.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			return s;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
