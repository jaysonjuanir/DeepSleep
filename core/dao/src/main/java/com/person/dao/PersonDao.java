package com.person.dao;

/**
 * Hello world!
 *
 */

 
import com.person.util.UtilSession;
import com.person.model.*;
import com.person.dto.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.apache.commons.beanutils.BeanUtils;
import java.util.*;
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
	public List<PersonDto> getPeople(){
		//UtilSession utilSession = new UtilSession();
		session = UtilSession.getSessionFactory().openSession();
		List<Person> persons = null;
		try{
			persons = session.createCriteria(Person.class).setCacheable(true).setCacheRegion("person").list()	;
			//session.close();
			//session.flush();
			//System.out.println(persons);
			System.out.println(UtilSession.getSessionFactory().getStatistics().getEntityFetchCount());   
			System.out.println(UtilSession.getSessionFactory().getStatistics().getSecondLevelCacheHitCount());
		}catch(HibernateException hex){
			hex.printStackTrace();
		}finally{
			session.close();
		}
		//session.close();
		List<PersonDto> personDto = new ArrayList<>();
		for(Person p: persons){
			personDto.add(toDto(p));
		}
		//persons.forEach(p->{personDto.add(toDto(p));});
		//System.out.println(personDto);
		return personDto;
	}
	public void updatePerson(PersonDto personDto){
		session = UtilSession.getSessionFactory().openSession();
		try{
			transac = session.beginTransaction();
			Person person = toEntity(personDto);
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
	public List<PersonDto> getPersonByLastName(){
		List<Person> people = new ArrayList<>();
		session = UtilSession.getSessionFactory().openSession();
		Transaction tx = null;
		try{
			//tx = session.beginTransaction();
			//String hql = "FROM com.person.model.Person ORDER BY person_last_name";
			//Query query = session.createQuery(hql);
			//query.setParameter("id",1);
			
			people = session.createCriteria(Person.class).addOrder( Order.asc("name.person_last_name") ).setCacheable(true).setCacheRegion("person").list();
			//people = query.setCacheable(true).list();
		}catch(RuntimeException e){
			e.printStackTrace();
		}finally{
			session.close();
		}
		List<PersonDto> peopleDto = new ArrayList<>();
		for(Person p: people){
			peopleDto.add(toDto(p));
		}
		return peopleDto;
	}
	public List<PersonDto> getPersonByGWA(){
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
		List<PersonDto> peopleDto = new ArrayList<>();
		for(Person p: people){
			peopleDto.add(toDto(p));
		}
		return peopleDto;
	}
	public List<PersonDto> getPersonByDateHired(){
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
		List<PersonDto> peopleDto = new ArrayList<>();
		for(Person p: people){
			peopleDto.add(toDto(p));
		}
		return peopleDto;
	}
	public PersonDto getPersonById(int id){
		session = UtilSession.getSessionFactory().openSession();
		Person thisPerson = (Person)session.get(Person.class, id);
		session.close();
		return toDto(thisPerson); 
	}
	public PersonDto toDto(Person person){
		PersonDto personDto = new PersonDto();
		NameDto nameDto = new NameDto();
		AddressDto addressDto = new AddressDto();
		Set<ContactDto> contactDtos = new HashSet<>();
		Set<RolesDto> roleDtos = new HashSet<>();
		try{
			//BeanUtils.copyProperties(personDto,person);
			BeanUtils.copyProperties(nameDto,person.getName());
			BeanUtils.copyProperties(addressDto,person.getAddress());
			
			personDto.setName(nameDto);
			personDto.setBirthday(person.getBirthday());
			personDto.setDate_hired(person.getDate_hired());
			personDto.setEmployed(person.getEmployed());
			personDto.setPerson_GWA(person.getPerson_GWA());
			personDto.setAddress(addressDto);
			personDto.setPerson_contact(contactDtos);
			
			personDto.setId(person.getId());
			
			for(Roles r : person.getRoles()){
				RolesDto roleDto = new RolesDto();
				BeanUtils.copyProperties(roleDto, r);
				roleDtos.add(roleDto);
			}
			personDto.setRoles(roleDtos);
			for(Contact c : person.getPerson_contact()){
				ContactDto contactDto = new ContactDto();
				ContactType type=c.getContact_type();
				
				contactDto.setContact_type(type);
				contactDto.setContact_value(c.getContact_value());
				//PersonDto prs = new PersonDto();
				//BeanUtils.copyProperties(prs, c.getContact_person());
				contactDto.setContact_person(personDto);
				contactDto.setId(c.getId());
				contactDtos.add(contactDto);
				
				//ContactDto contactDto = new ContactDto();
				//BeanUtils.copyProperties(contactDto, c);
			}
			personDto.setPerson_contact(contactDtos);
		}catch(Exception e){
			
			System.out.println("catched!");
			e.printStackTrace();
		}
		
		return personDto;
	}

	public Person toEntity(PersonDto personDto){
		Person person = new Person();
		Name name = new Name();
		Address address = new Address();
		Set<Contact> contacts = new HashSet<>();
		Set<Roles> roles = new HashSet<>();
		try{
			//BeanUtils.copyProperties(personDto,person);
			BeanUtils.copyProperties(name,personDto.getName());
			BeanUtils.copyProperties(address,personDto.getAddress());
			
			person.setName(name);
			person.setBirthday(personDto.getBirthday());
			person.setDate_hired(personDto.getDate_hired());
			person.setEmployed(personDto.getEmployed());
			person.setPerson_GWA(personDto.getPerson_GWA());
			person.setAddress(address);
			person.setId(personDto.getId());
			
			for(RolesDto r : personDto.getRoles()){
				Roles role = new Roles();
				BeanUtils.copyProperties(role, r);
				roles.add(role);
			}
			
			for(ContactDto c : personDto.getPerson_contact()){
				Contact contact = new Contact();
				ContactType type=c.getContact_type();
				
				contact.setContact_type(type);
				contact.setContact_value(c.getContact_value());
				//PersonDto prs = new PersonDto();
				//BeanUtils.copyProperties(prs, c.getContact_person());
				contact.setContact_person(person);
				contact.setId(c.getId());
				contacts.add(contact);
				
				//ContactDto contactDto = new ContactDto();
				//BeanUtils.copyProperties(contactDto, c);
			}
			
			person.setRoles(roles);
			person.setPerson_contact(contacts);
		}catch(Exception e){
			
			System.out.println("catched!");
			e.printStackTrace();
		}
		
		return person;
	}
	
	public void closeSessionFactory(){
		UtilSession.closeSessionFactory();
	}
}
