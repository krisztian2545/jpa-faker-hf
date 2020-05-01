package person;

import com.github.javafaker.Faker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.ZoneId;

public class Main {

    private static Person randomPerson() {
        Person p = new Person();
        Address a = new Address();
        Faker faker = new Faker();

        a.setCountry( faker.address().country() );
        a.setState( faker.address().state() );
        a.setCity( faker.address().city() );
        a.setStreetAddress( faker.address().streetAddress() );
        a.setZip( faker.address().zipCode() );

        p.setName( faker.name().fullName() );
        p.setEmail( faker.internet().emailAddress() );
        p.setProfession( faker.company().profession() );
        p.setAddress( a );
        p.setGender( faker.options().option(Person.Gender.class) );

        java.util.Date date;
        date = faker.date().birthday();
        java.time.LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        p.setDob( localDate );

        //System.out.println(p);

        return p;
    }

    private static void fillWithRandomPersons(EntityManager em, int numberOfPersons, int batchSize) {
        em.getTransaction().begin();

        for(int i = 0; i < numberOfPersons; i++) {
            em.persist(randomPerson());

            if( i % batchSize == 0 ) {
                //System.out.println("........................................................... flush time :)");
                em.flush();
                em.clear();
            }
        }

        em.getTransaction().commit();
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");
        EntityManager em = emf.createEntityManager();

        fillWithRandomPersons(em, 1000, 100);

        em.close();
        emf.close();
    }
}
