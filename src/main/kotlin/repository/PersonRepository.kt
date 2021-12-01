package repository

import hibernate.HibernateController
import model.Person
import java.sql.SQLException


class PersonRepository: CrudRespository<Person, Long> {
    // Si quiero devolver la excepcion debo anotarlas como throws
    @Throws(SQLException::class)
    override fun findAll(): List<Person>? {
        try {
            val query = HibernateController.manager
                .createNamedQuery(
                    "Person.findAll",
                    Person::class.java
                )
            //HibernateController.close()
            return query.resultList
        } catch (e: Exception) {
            throw SQLException("Error PersonRepository findAll")
        }
    }

    @Throws(SQLException::class) // No es obligatorio, pero es util para compatibilidad con Java
    override fun findById(id: Long): Person? {
        return HibernateController.manager
            .find(Person::class.java, id) ?:
            throw SQLException("Error PersonRepository findById no existe Person con ID: $id")
    }

    @Throws(SQLException::class)
    override fun save(person: Person): Person {
        try {
            HibernateController.transaction.begin();
            HibernateController.manager.persist(person)
            HibernateController.transaction.commit();
            return person
        } catch (e: Exception) {
            HibernateController.transaction.rollback();
            throw SQLException("Error PersonRepository al insertar Person en BD: ${e.message}: ${e.message}")
        }
    }

    @Throws(SQLException::class)
    override fun update(person: Person): Person {
        try {
            HibernateController.transaction.begin();
            HibernateController.manager.merge(person)
            HibernateController.transaction.commit();
            return person
        } catch (e: Exception) {
            HibernateController.transaction.rollback();
            println(e.stackTraceToString())
            throw SQLException("Error PersonRepository update al actualizar Person ID: ${person.id}: ${e.message}");
        }
    }

    @Throws(SQLException::class)
    override fun delete(person: Person): Person {
        try {
            // Debemos buscarlo priomero para evitar
            // Exception Removing a detached instance model.
            // Deben estar en la misma sesion lo que buscas y borras
            val personToDelete = HibernateController.manager.find(Person::class.java, person.id)
            HibernateController.transaction.begin();
            HibernateController.manager.remove(personToDelete)
            HibernateController.transaction.commit();
            return personToDelete
        } catch (e: Exception) {
            HibernateController.transaction.rollback();
            throw SQLException("Error PersonRepository delete al actualizar Person ID: ${person.id}: ${e.message}")
        }
    }
}