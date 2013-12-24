package br.com.mvbos.test;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;


import org.apache.jackrabbit.commons.JcrUtils;
import org.junit.Assert;
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

	@Test
	public void save() throws Exception {
		Session session = getSession();

		Node root = session.getNode("/content");

		if (root.hasNode("nodethunder")) {
			root = root.getNode("nodethunder");
		} else {
			root = root.addNode("nodethunder");
		}

		NodeThunder nt = NodeThunder.create(session, root);
		
		Client c = new Client(1L, "Aaa", 2900.00, new BigDecimal(170000.00), Calendar.getInstance(), Boolean.FALSE);

		nt.save(c);
		
		Assert.assertTrue(root.hasNode("1"));
		
		session.logout();
	}

	public Session getSession() {
		try {

			repository = JcrUtils
					.getRepository("http://localhost:4502/crx/server");

			Session session = repository.login(new SimpleCredentials("admin",
					"admin".toCharArray()));

			return session;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
