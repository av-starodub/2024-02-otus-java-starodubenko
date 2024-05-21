package ru.otus.advjdbc;


import ru.otus.advjdbc.datasource.DataSourceProvider;
import ru.otus.advjdbc.dbmigration.DbMigrator;
import ru.otus.advjdbc.model.User;
import ru.otus.advjdbc.reposistory.AbstractRepository;
import ru.otus.advjdbc.reposistory.UsersDao;
import ru.otus.advjdbc.reposistory.UsersDaoImpl;

public class Application {
    // Домашнее задание:
    // - Реализовать класс DbMigrator - он должен при старте создавать все необходимые таблицы из файла init.sql
    // Доработать AbstractRepository
    // - Доделать findById(id), findAll(), update(), deleteById(id), deleteAll()
    // - Сделать возможность указывать имя столбца таблицы для конкретного поля (например, поле accountType маппить на столбец с именем account_type)
    // - Добавить проверки, если по какой-то причине невозможно проинициализировать репозиторий, необходимо бросать исключение, чтобы
    // программа завершила свою работу (в исключении надо объяснить что сломалось)
    // - Работу с полями объектов выполнять только через геттеры/сеттеры

    public static void main(String[] args) {
        var dataSource = DataSourceProvider.creatHikariConnectionPool("hikari.properties");
        var dbMigrator = new DbMigrator(dataSource);
        dbMigrator.migrate();

        try {

            UsersDao usersDao = new UsersDaoImpl(dataSource);
            System.out.println(usersDao.findAll());

            AbstractRepository<User> repository = new AbstractRepository<>(dataSource, User.class);
            User user = new User("bob", "123", "bob");
            repository.create(user);

            System.out.println(usersDao.findAll());

/*
            AbstractRepository<Account> accountAbstractRepository = new AbstractRepository<>(dataSource, Account.class);
            Account account = new Account(100L, "credit", "blocked");
            accountAbstractRepository.create(account);
*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbMigrator.deleteDataBase();
        }
    }
}
