package biz.daich.common.tools.jpa;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import biz.daich.common.interfaces.IHasId;

/**
 * Class for generate entity id in form of the UUID String
 *
 * @since 0.2
 */
public class CustomUUIDStringGenerator implements IdentifierGenerator
{
	/**
	 * @see org.hibernate.id.IdentifierGenerator#generate(org.hibernate.engine.spi.SharedSessionContractImplementor, java.lang.Object)
	 *
	 *      this code inspired by code written by Andrew Kolisnichenko - kain81@gmail.com for riggoh.com project
	 * @throws
	 * 			HibernateException
	 *             - in any case of troubles
	 */
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object)// throws HibernateException
	{
		IHasId objectHasId = (IHasId) object;
		if (objectHasId.getId() != null)
		{
			// it already has an ID - keep it
			return objectHasId.getId();
		}
		// generate a new one
		return UUID.randomUUID().toString();
	}

}
