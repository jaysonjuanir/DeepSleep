package com.person.dao;

/**
 * Hello world!
 *
 */

import com.person.util.UtilSession;
import com.person.model.Person;
import com.person.model.Contact;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import java.util.List;
import java.util.ArrayList;
public class PersonDao
{
	//UtilSession utilSession;
	Session session;
	Transaction transac;
	//public PersonDao(){
	//	utilSession = new UtilSession();	
	//}
    public void addPerson(Person person){
		//UtilSession utilSession = new UtilSession();
		session = UtilSession.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(person);
		session.getTransaction().commit();
		System.out.println("PERSON CREATED!!");
		session.close();
	}
	public List<Person> getPeople(){
		//UtilSession utilSession = new UtilSession();
		session = UtilSession.getSessionFactory().openSession();
		List<Person> persons = null;
		try{
			persons = session.createCriteria(Person.class).setCacheable(true).setCacheRegion("person").list()	;
			//session.close();
			//session.flush();
			System.out.println(UtilSession.getSessionFactory().getStatistics().getEntityFetchCount());   
			System.out.println(UtilSession.getSessionFactory().getStatistics().getSecondLevelCacheHitCount());
		}catch(HibernateException hex){
			hex.printStackTrace();
		}finally{
			session.close();
		}
		//session.close();
		return persons;
	}
	public void updatePerson(Person person){
		session = UtilSession.getSessionFactory().openSession();
		try{
			transac = session.beginTransaction();
			session.update(person);
			transac.commit();
			System.out.println("PERSON UPDATED!");
		}catch(HibernateException hex){
			if(transac!=null)
				transac.rollback();
			hex.printStackTrace();
		}finally{
			session.close();
		}
	}
	public void deletePerson(Person deleteThisPerson){
		session=UtilSession.getSessionFactory().openSession();
		try{
			transac = session.beginTransaction();
			session.delete(deleteThisPerson);
			transac.commit();
			System.out.println("PERSON DELETED!!");
		}catch(HibernateException hex){
			if(transac!=null)
				transac.rollback();
			hex.printStackTrace();
		}finally{
			session.close();
		}
	}
	public List<Person> getPersonByLastName(){
		List<Person> people = new ArrayList<>();
		session = UtilSession.getSessionFactory().openSession();
		Transaction tx = null;
		try{
			//tx = session.beginTransaction();
			//String hql = "FROM com.person.model.Person ORDER BY person_last_name";
			//Query query = session.createQuery(hql);
			//query.setParameter("id",1);
			
			people = session.createCriteria(Person.class).addOrder( Order.asc("person_last_name") ).setCacheable(true).setCacheRegion("person").list();
			//people = query.setCacheable(true).list();
		}catch(RuntimeException e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return people;
	}
	public List<Person> getPersonByGWA(){
		List<Person> people = new ArrayList<>();
		session = UtilSession.getSessionFactory().openSession();
		Transaction tx = null;
		try{
			/*tx = session.beginTransaction();
			String hql = "from person where gwa = :gwa";
			Query query = session.createQuery(hql);
			query.setParameter("gwa",gwa);
			persons = query.setCacheable(true).list();*/
			people = session.createCriteria(Person.class).addOrder( Order.asc("person_gwa") ).setCacheable(true).setCacheRegion("person").list();
		}catch(RuntimeException e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return people;
	}
	public List<Person> getPersonByDateHired(){
		List<Person> people = new ArrayList<>();
		session = UtilSession.getSessionFactory().openSession();
		Transaction tx = null;
		try{
			/*tx = session.beginTransaction();
			String hql = "from person where gwa = :gwa";
			Query query = session.createQuery(hql);
			query.setParameter("gwa",gwa);
			persons = query.setCacheable(true).list();*/
			people = session.createCriteria(Person.class).addOrder( Order.asc("date_hired") ).add( Restrictions.isNotNull("date_hired") ).setCacheable(true).setCacheRegion("person").list();
		}catch(RuntimeException e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		return people;
	}
	public Person getPersonById(int id){
		session = UtilSession.getSessionFactory().openSession();
		Person thisPerson = (Person)session.get(Person.class, id);
		session.close();
		return thisPerson;
	}
	
	public void closeSessionFactory(){
		UtilSession.closeSessionFactory();
	}
}
